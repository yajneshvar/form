package forms.serverless.model

data class Order(var createdDate: String?, var orderId: String?, val customerId: String, val type: String = "Sale",  val books: List<BookQuantity>, val channel: String, val delivery: Boolean, val deliveryNotes: String?, val paymentNotes: String?, val creator: String)

fun Order.toList(): List<List<String>> {
    return books.map {
        listOf(this.createdDate!!, this.orderId!!, this.customerId, this.type, it.code, it.title, it.quantity.toString(), channel, delivery.toString(), deliveryNotes.orEmpty(), paymentNotes.orEmpty(), creator)
    }
}

fun Order.toEmailText(): String {
    return """
        Successfully created Order for ${this.customerId}
        Order Info:
        ${books.map { "${it.title} : ${it.quantity}" }.joinToString(separator="\n")}
    """.trimIndent()
}

data class BookQuantity(val title: String, val code: String, val type: String, val quantity: Int)