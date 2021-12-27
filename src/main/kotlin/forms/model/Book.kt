package forms.model

data class Book(val title: String, val code: String, val type: String)

data class Item(var id: String?, val title: String, val code: String, val type: String, val metadata: Map<String, String>)

data class Product(var id: String?, val title: String, val code: String, val type: String, val items: List<String>)