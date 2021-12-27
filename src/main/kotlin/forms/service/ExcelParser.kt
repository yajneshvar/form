package forms.service

import forms.model.Item

import jakarta.inject.Singleton

import java.io.InputStream
import org.apache.poi.ss.usermodel.WorkbookFactory
import org.apache.poi.ss.usermodel.Workbook

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
class ExcelParser {

    val log = LogManager.getLogger("ExcelParser")

    val ITEM_HEADERS = setOf("Book Name", "Book Code", "Books/Unit", "Type")

    private fun getWorkBook(inputStream: InputStream): Workbook {
        return WorkbookFactory.create(inputStream)
    }

    fun getItems(inputStream: InputStream): List<Item> {
        val workBook = getWorkBook(inputStream)
        val sheet = workBook.getSheet("items")
        val headers = verifyInputSheetHeaders(sheet.first().map { it.getStringCellValue() })
        val headerToIndex = getHeaderToIndex(headers)
        val entries = sheet.drop(1)
        return entries.map {
            Item(null, it.getCell(0).getStringCellValue(), it.getCell(1).getStringCellValue(), it.getCell(3).getStringCellValue(), mapOf("unit" to it.getCell(2).getNumericCellValue().toString()))
        }
    }
    
    fun verifyInputSheetHeaders(headers: List<String>): List<String> {
        log.info(headers);
        require(headers.containsAll(ITEM_HEADERS)) { "Incorrect headers ${headers}" } 
        return headers
    }
    
    private fun getHeaderToIndex(headers: List<String>): Map<String, Int> {
        return headers.mapIndexed { index, header -> 
            header to index
        }.toMap()
    }
    
    
}