package forms.serverless

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import forms.serverless.google.GoogleSheetsService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import forms.serverless.google.GoogleGmailService
import forms.serverless.model.*
import org.apache.http.HttpStatus
import org.apache.http.protocol.HTTP
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*

class FormFunction : HttpFunction {

    private val googleClient = GoogleSheetsService()
    private val googleEmailClient = GoogleGmailService()
    private val objectMapper = jacksonObjectMapper()

    override fun service(request: HttpRequest?, response: HttpResponse?) {

        if (request == null || response == null) {
            return
        }

        // CORS
        // Access-Control-Allow-Methods: 'HEAD, GET, POST, PUT, PATCH, DELETE'
        //Access-Control-Allow-Headers: 'Origin, Content-Type, X-Auth-Token';
        response.apply {
            appendHeader("Access-Control-Allow-Origin", "*")
            appendHeader("Access-Control-Allow-Methods", "HEAD, GET, POST, PUT, PATCH, DELETE")
            appendHeader("Access-Control-Allow-Headers", "Origin, Content-Type, X-Auth-Token")
        }

        if (request.method == "OPTIONS") {
            response.setStatusCode(HttpStatus.SC_OK)
            println("OPTIONS")
            return
        }

        when {
            request.isFor("/user", "POST") -> handleNewUser(request, response)
            request.isFor("/order", "POST") -> handleNewOrder(request, response)
            request.isFor("/users", "GET") -> handleGetUsers(request, response)
            request.isFor("/books", "GET") -> handleGetBooks(request, response)
            request.isFor("/channels", "GET") -> handleGetChannelTypes(request, response)
            else -> response.setStatusCode(HttpStatus.SC_NOT_FOUND)
        }

    }

    fun handleGetUsers(request: HttpRequest, response: HttpResponse) {
        val values = googleClient.readFromSpreadSheet(googleClient.SPREADSHEET_ID, "Sheet1")
        if (values != null) {
            val userReponses = values.map {
                val stringValues = it as List<String>
                println(stringValues.toString())
                UserResponse(stringValues[0], stringValues[1], stringValues[2], stringValues[5], stringValues[8])
            }
            response.writer.write(objectMapper.writeValueAsString(userReponses))
            response.setStatusCode(HttpStatus.SC_OK)
        } else {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    fun handleNewUser(request: HttpRequest, response: HttpResponse) {
        val payload = request.reader.readText()
        println("-----------")
        println(payload)
        val user: User = objectMapper.readValue(payload)
        user.id = "${user.firstName}-${user.address.city}-${ZonedDateTime.now(ZoneOffset.UTC).toEpochSecond()}"
        val sheetResponse = googleClient.writeToSpreadSheet(googleClient.SPREADSHEET_ID, listOf(user.toList()), "Sheet1!A1")
        if (sheetResponse != null) {
            response.writer.write(objectMapper.writeValueAsString(user.toUserResponse()))
            response.setStatusCode(HttpStatus.SC_OK)
        } else {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    fun handleNewOrder(request: HttpRequest, response: HttpResponse) {
        val payload = request.reader.readText()
        println("-----------")
        println(payload)
        val order: Order = objectMapper.readValue(payload)
        order.createdDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        order.orderId = UUID.randomUUID().toString()
        val sheetResponse = googleClient.writeToSpreadSheet(googleClient.SPREADSHEET_ID, order.toList(), "Order!A:H")
        val email = googleEmailClient.createEmail(order.creator, "yajneshvar@tst-ims.com", "New Order for ${order.customerId}", order.toEmailText() )
        googleEmailClient.sendEmail("yajneshvar@tst-ims.com", email!!)
        if (sheetResponse != null) {
            response.setStatusCode(HttpStatus.SC_CREATED)
        } else {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    fun handleGetBooks(request: HttpRequest, response: HttpResponse) {
        val values = googleClient.readFromSpreadSheet(googleClient.SPREADSHEET_ID, "Books!A:F")
        val books = values?.drop(1)?.map {
            val bookValues = it as List<String>
            Book(bookValues[0], bookValues[1], bookValues[5])
        }
        response.setStatusCode(HttpStatus.SC_OK)
        response.writer.write(objectMapper.writeValueAsString(books))
    }

    fun handleGetChannelTypes(request: HttpRequest, response: HttpResponse) {
        val values = googleClient.readFromSpreadSheet(googleClient.SPREADSHEET_ID, "Books!G:G")
        val channelTypes: List<String> = values?.drop(1)?.map {
            it[0] as String
        } ?: emptyList()
        response.setStatusCode(HttpStatus.SC_OK)
        response.writer.write(objectMapper.writeValueAsString(channelTypes))
    }

    fun HttpRequest.isFor(path: String, method: String): Boolean {
        return this.path == path && this.method == method
    }

}