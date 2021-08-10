package forms.model

data class BookCount(val book: Book, val startCount: Int, val endCount: Int)

data class Consignment(var id: String?, val issuedTo: String, val location: String, val issuedBooks: List<BookCount>, val startDate: String, val modifiedAt: String)
