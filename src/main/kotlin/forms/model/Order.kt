package forms.model

data class Order(
    var createdDate: String?,
    var orderId: String?, 
    var customerId: String?, 
    var recepient: Recepient?,
    var type: String = "Sale", 
    var books: List<BookQuantity> = emptyList(),
    var quantities: List<Quantity> = emptyList(),
    var channel: String?, 
    var delivery: Boolean = false, 
    var deliveryNotes: String = "", 
    var paymentNotes: String = "", 
    var additionalNotes: String = "", 
    var creator: String = ""
    )
    
data class Recepient(
    var customerId: String?,
    var anonymousUser: String?,
    var additionalNotes: String?
)

fun Order.toList(): List<List<String?>> {
    return books.map {
        val notes = if (paymentNotes.isEmpty()) additionalNotes else paymentNotes;
        listOf(this.createdDate, this.orderId, this.customerId, this.type, it.code, it.title, it.startCount.toString(), channel, delivery.toString(), deliveryNotes, notes, creator)
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
        ${books.map { "${it.title} : ${it.startCount}" }.joinToString(separator="\n")}
        ${quantities.map { "${it.title} : ${it.startCount}" }.joinToString(separator="\n")}}
        Payment Notes: ${this.paymentNotes}
        $deliveryText
        $deliveryNotesText
    """.trimIndent()
}

data class BookQuantity(val title: String, val code: String, val type: String, val startCount: Int)

data class OrderQuantity(
    var quantities: List<Quantity> = emptyList()
)