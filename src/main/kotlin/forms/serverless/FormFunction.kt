package forms.serverless

import com.google.cloud.functions.HttpFunction
import com.google.cloud.functions.HttpRequest
import com.google.cloud.functions.HttpResponse
import forms.serverless.google.GoogleSheetsService
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import forms.serverless.google.FirestoreService
import forms.serverless.google.GoogleGmailService
import forms.serverless.model.*
import org.apache.http.HttpStatus
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class FormFunction : HttpFunction {

    private val googleClient = GoogleSheetsService()
    private val googleEmailClient = GoogleGmailService()
    private val objectMapper = jacksonObjectMapper()
    private val firestoreService = FirestoreService()

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
            request.isFor("/user/.*", "GET", true) -> handleGetUserById(request, response)
            request.isFor("/order", "POST") -> handleNewOrder(request, response)
            request.isFor("/users", "GET") -> handleGetUsers(request, response)
            request.isFor("/books", "GET") -> handleGetBooks(request, response)
            request.isFor("/channels", "GET") -> handleGetChannelTypes(request, response)
            else -> response.setStatusCode(HttpStatus.SC_NOT_FOUND)
        }

    }

    fun handleGetUsers(request: HttpRequest, response: HttpResponse) {
        val values = firestoreService.getUsers()
        if (values != null) {
            val userReponses = values.map {
                it.toUserResponse()
            }
            response.writer.write(objectMapper.writeValueAsString(userReponses))
            response.setStatusCode(HttpStatus.SC_OK)
        } else {
            response.setStatusCode(HttpStatus.SC_INTERNAL_SERVER_ERROR)
        }
    }

    fun handleGetUserById(request: HttpRequest, response: HttpResponse) {
        val id = request.path.removePrefix("/user/")
        val user = firestoreService.getUserById(id)
        if (user != null) {
            response.writer.write(objectMapper.writeValueAsString(user))
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
        firestoreService.upsertUser(user)
        val sheetResponse = googleClient.writeToSpreadSheet(googleClient.SPREADSHEET_ID, listOf(user.toList()), "Customer!A1")
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
        firestoreService.upsertOrder(order)
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

    fun handleConsignment(request: HttpRequest, response: HttpResponse) {
        val payload = request.reader.readText();
        val consignment: Consignment = objectMapper.readValue(payload)

    }

    fun HttpRequest.isFor(path: String, method: String, useRegex: Boolean = false): Boolean {
        println(this.path)
        if (useRegex) {
            val regex = Regex(path, RegexOption.DOT_MATCHES_ALL)
            println(regex.pattern)
            return regex.matches(this.path) && this.method == method
        }
        return this.path == path && this.method == method
    }

}