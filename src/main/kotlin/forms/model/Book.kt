package forms.model

data class Book(val title: String, val code: String, val type: String)

data class ItemOrProduct(val id: String, val title: String, val code: String, val type: String)

class Item() {
    var id: String? = null
    var title: String? = null
    var code: String? = null
    var category: String? = null
    var metadata: Map<String, String> = emptyMap()
}

fun Item.toItemOrProduct(): ItemOrProduct {
    return ItemOrProduct(this.id!!, this.title!!, this.code!!, "Item")
}

class Product() {
    var id: String? = null
    var title: String? = null
    var code: String? = null
    var category: String? = null
    var items: List<String> = emptyList()
}

fun Product.toItemOrProduct(): ItemOrProduct {
    return ItemOrProduct(this.id!!, this.title!!, this.code!!, "Product")
}

data class Quantity(var id: String, val startCount: Int, val category: String, val title: String)
