package forms.controllers

import forms.google.FirestoreService
import forms.google.GoogleSheetsService
import forms.model.User
import forms.model.UserResponse
import forms.model.toUserResponse
import io.micronaut.http.HttpResponse
import io.micronaut.http.HttpStatus
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import javax.inject.Inject


@Controller("/users")
class UserController {

    @Inject lateinit var firestoreService: FirestoreService
    @Inject lateinit var googleSheetsService: GoogleSheetsService

    @Get
    fun getUsers(): List<UserResponse> {
        return firestoreService.getUsers().map { it.toUserResponse() }
    }

    @Get("/{id}")
    fun getUserById(id: String): HttpResponse<User?> {
        val user = firestoreService.getUserById(id)
        return if (user == null) {
            HttpResponse.status<User?>(HttpStatus.NOT_FOUND)
        } else {
            HttpResponse.accepted<User>().body(user)
        }
    }

    //61ce515f-35e2-43ac-993f-85c2330d52e5
    @Post
    fun createUser(user: User): HttpResponse<UserResponse> {
        firestoreService.upsertUser(user)
        val sheetResponse = googleSheetsService.writeToSpreadSheet(googleSheetsService.SPREADSHEET_ID, listOf(user.toList()), "Customer!A1")
        return if (sheetResponse != null) {
            HttpResponse.accepted<UserResponse>().body(user.toUserResponse())
        } else {
            HttpResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
        }
    }
}