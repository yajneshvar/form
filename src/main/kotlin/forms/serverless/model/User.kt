package forms.serverless.model

interface ListValues {
    fun toList(): List<String>
}

data class User (var id: String?, val firstName: String, val lastName: String?, val address: Address, val email: String, val cellPhone: String?, val homePhone: String?, val creator: String) : ListValues {
    override fun toList(): List<String> {
        return listOf(id.orEmpty(), firstName, lastName.orEmpty()) + address.toList() + listOf(email, cellPhone.orEmpty(), homePhone.orEmpty(), creator)
    }

}

fun User.toUserResponse(): UserResponse {
    return UserResponse(this.id, this.firstName, this.lastName, this.address.postalCode, this.email)
}


data class UserResponse (var id: String?, val firstName: String, val lastName: String?, val postalCode: String, val email: String)


data class Address(val line: String, val line2: String?, val postalCode: String, val city: String, val country: String?) : ListValues {
    override fun toList(): List<String> {
        return listOf(line, line2.orEmpty(), postalCode, city, country.orEmpty())
    }

}
