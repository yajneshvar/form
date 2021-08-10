package forms.controllers

import forms.google.GoogleSheetsService
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import javax.inject.Inject

@Controller("/channels")
class ChannelController {

    @Inject lateinit var googleSheetsService: GoogleSheetsService

    @Get
    fun channels(): HttpResponse<List<String>> {
        val values = googleSheetsService.readFromSpreadSheet(googleSheetsService.SPREADSHEET_ID, "Books!G:G")
        val channelTypes: List<String> = values?.drop(1)?.map {
            it[0] as String
        } ?: emptyList()
        return if (values == null) {
            HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        } else {
            HttpResponse.accepted<List<String>>().body(channelTypes)
        }
    }
}