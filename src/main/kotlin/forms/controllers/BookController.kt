package forms.controllers

import forms.google.GoogleSheetsService
import forms.model.Book
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import javax.inject.Inject

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/books")
class BookController {

    @Inject lateinit var googleSheetsService: GoogleSheetsService

    @Get
    fun books(): HttpResponse<List<Book>> {
        val values = googleSheetsService.readFromSpreadSheet(googleSheetsService.SPREADSHEET_ID, "Books!A:F")
        val books = values?.drop(1)?.map {
            val bookValues = it as List<String>
            Book(bookValues[0], bookValues[1], bookValues[5])
        }
        return if (books == null) {
            HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        } else {
            HttpResponse.accepted<List<Book>>().body(books)
        }
    }

}