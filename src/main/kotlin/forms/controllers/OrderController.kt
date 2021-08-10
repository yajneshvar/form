package forms.controllers

import forms.google.FirestoreService
import forms.google.GoogleGmailService
import forms.google.GoogleSheetsService
import forms.model.Order
import forms.model.toEmailText
import forms.model.toList
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Post
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@Controller("/orders")
class OrderController {


    @Inject
    lateinit var firestoreService: FirestoreService
    @Inject
    lateinit var googleSheetsService: GoogleSheetsService
    @Inject
    lateinit var googleEmailClient: GoogleGmailService

    @Post
    fun saveOrder(order: Order): HttpResponse<Order> {
        order.createdDate = LocalDateTime.now().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
        firestoreService.upsertOrder(order)
        val sheetResponse = googleSheetsService.writeToSpreadSheet(googleSheetsService.SPREADSHEET_ID, order.toList(), "Order!A:H")
        val email = googleEmailClient.createEmail(order.creator, "yajneshvar@tst-ims.com", "New Order for ${order.customerId}", order.toEmailText() )
        googleEmailClient.sendEmail("yajneshvar@tst-ims.com", email!!)
        return if (sheetResponse != null) {
            HttpResponse.accepted<Order>().body(order)
        } else {
           HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}