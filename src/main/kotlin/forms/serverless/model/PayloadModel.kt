package forms.serverless.model

data class Order(var createdDate: String?, var orderId: String?, val customerId: String, val type: String = "Sale",  val books: List<BookQuantity>, val channel: String, val delivery: Boolean, val deliveryNotes: String?, val paymentNotes: String?, val additionalNotes: String?, val creator: String)

fun Order.toList(): List<List<String>> {
    return books.map {
        listOf(this.createdDate!!, this.orderId!!, this.customerId, this.type, it.code, it.title, it.quantity.toString(), channel, delivery.toString(), deliveryNotes.orEmpty(), paymentNotes ?: additionalNotes.orEmpty(), creator)
    }
}

fun Order.toEmailText(): String {
    val deliveryText = if (this.delivery)  "Delivery required for this order" else  "Delivery not required for this order"
    val deliveryNotesText = if (delivery) this.deliveryNotes else ""
    return """
        Successfully created Order for ${this.customerId}
        Distributor: ${this.creator}
        ${this.orderId?.let { "Order Id: $it" }}
        Channel: ${this.channel}
        Order Info:
        ${books.map { "${it.title} : ${it.quantity}" }.joinToString(separator="\n")}
        Payment Notes: ${this.paymentNotes}
        $deliveryText
        $deliveryNotesText
    """.trimIndent()
}

data class BookQuantity(val title: String, val code: String, val type: String, val quantity: Int)