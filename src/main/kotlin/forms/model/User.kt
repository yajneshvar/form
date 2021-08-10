package forms.model

interface ListValues {
    fun toList(): List<String?>
}

class User () : ListValues {
    var id: String = ""
    var firstName: String = ""
    var lastName: String = ""
    var address: Address? = null
    var email: String = ""
    var cellPhone: String = ""
    var homePhone: String = ""
    var creator: String? = null
    override fun toList(): List<String?> {
        return listOf(id, firstName, lastName) + address!!.toList() + listOf(email, cellPhone, homePhone, creator)
    }

}

fun User.toUserResponse(): UserResponse {
    return UserResponse().let {
        it.id = this.id
        it.firstName = this.firstName
        it.lastName = this.lastName
        it.postalCode = this.address!!.postalCode
        it.email = this.email
        it
    }
}

fun User.toUserListResponse(): UserList {
    return UserList().let {
        it.id = this.id
        it.firstName  = this.firstName
        it.lastName = this.lastName
        it
    }
}


class UserResponse () {
    var id: String=""
    var firstName: String = ""
    var lastName: String = ""
    var postalCode: String = ""
    var email: String = ""
}

class UserList() {
    var id: String = ""
    var firstName: String = ""
    var lastName: String = ""
}

class Address() : ListValues {
    var line: String = ""
    var line2: String = ""
    var postalCode: String = ""
    var city: String = ""
    var country: String = ""

    override fun toList(): List<String> {
        return listOf(line, line2, postalCode, city, country)
    }
}
