package forms.google

import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport
import com.google.api.client.json.gson.GsonFactory
import com.google.api.services.sheets.v4.Sheets
import com.google.api.services.sheets.v4.SheetsScopes
import com.google.api.services.sheets.v4.model.AppendValuesResponse
import com.google.api.services.sheets.v4.model.Spreadsheet
import com.google.api.services.sheets.v4.model.SpreadsheetProperties
import com.google.api.services.sheets.v4.model.ValueRange
import com.google.auth.http.HttpCredentialsAdapter
import com.google.auth.oauth2.GoogleCredentials
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoogleSheetsService constructor(@Inject var credentials: GoogleCredentials){
    private val httpTransport = GoogleNetHttpTransport.newTrustedTransport()
    private val jacksonFactory = GsonFactory.getDefaultInstance()
    val SHEETS: Sheets = Sheets.Builder(httpTransport, jacksonFactory, HttpCredentialsAdapter( credentials.createScoped(SheetsScopes.SPREADSHEETS, SheetsScopes.DRIVE_FILE)))
            .setApplicationName("FORMS")
            .build()
//    val SPREADSHEET_ID = "1960CUzLpza43RD_2QAnnXutFYSSr5l3WiwKvfD_4N-I"
    val SPREADSHEET_ID = "1RWIy-gnZ0IngeofQ2dNwlXBzgtln_TD7shIQeWGpr4E"

    fun createSheet(): String? {
        val spreadSheet = Spreadsheet()
                .setProperties(SpreadsheetProperties()
                        .setTitle("GoogleCloudFunctionYaj"))

        val values: List<List<Any>> = mutableListOf((1..5).map { it.toString() })

        val valueRange = ValueRange()
                .setMajorDimension("ROWS")
                        .setValues(values)

        val response = SHEETS.spreadsheets().Values().append(SPREADSHEET_ID, "Sheet1!A1", valueRange)
                            .setValueInputOption("RAW")
                            .execute()
        return response.toPrettyString()
    }

    fun writeToSpreadSheet(spreadSheetId: String, values: List<List<Any?>>, range: String): AppendValuesResponse? {
        val valueRange = ValueRange()
                .setMajorDimension("ROWS")
                .setValues(values)
        return SHEETS.spreadsheets().Values().append(spreadSheetId, range, valueRange)
                .setValueInputOption("RAW")
                .execute()
    }

    fun readFromSpreadSheet(spreadSheetId: String, range: String): MutableList<MutableList<Any>>? {
        val response = SHEETS.spreadsheets().Values().get(spreadSheetId, range)
                .setValueRenderOption("FORMATTED_VALUE")
                .execute()
        return response.getValues()
    }
}