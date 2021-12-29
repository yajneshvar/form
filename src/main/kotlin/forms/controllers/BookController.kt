package forms.controllers

import forms.google.FirestoreService
import forms.google.GoogleSheetsService
import forms.service.ExcelParser
import forms.model.Book
import forms.model.Item
import forms.model.ItemOrProduct
import forms.model.toItemOrProduct
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.security.annotation.Secured
import io.micronaut.http.MediaType
import io.micronaut.http.annotation.Post
import io.micronaut.security.rules.SecurityRule
import javax.annotation.security.PermitAll
import jakarta.inject.Inject

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException

@Secured(SecurityRule.IS_AUTHENTICATED)
@Controller("/books")
class BookController {
    val log = LogManager.getLogger("BookController")

    @Inject lateinit var googleSheetsService: GoogleSheetsService
    @Inject lateinit var firestoreService: FirestoreService
    @Inject lateinit var excelParser: ExcelParser

    @Get
    fun getBooks(): HttpResponse<List<Book>> {
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

    @Get(value = "/items")
    // @PermitAll
    fun itemsOrProducts(): HttpResponse<List<ItemOrProduct>> {
        val items = firestoreService.getItems()
        val products = firestoreService.getProducts()
        val itemsOrProducts = items.map { it.toItemOrProduct() } + products.map { it.toItemOrProduct() } 
        return HttpResponse.accepted<List<ItemOrProduct>>().body(itemsOrProducts)
    }
    
    // @PermitAll
    @Post(value = "/upload", consumes = [MediaType.ALL]) 
    fun uploadBytes(file: ByteArray, fileName: String): HttpResponse<String> { 
        return try {
            log.info(fileName)
            val excelStream = file.inputStream()
            val items = excelParser.getItems(excelStream)
            firestoreService.upsertItems(items)
            HttpResponse.ok("Uploaded")
        } catch (exception: IOException) {
            HttpResponse.badRequest("Upload Failed")
        }

    }

}