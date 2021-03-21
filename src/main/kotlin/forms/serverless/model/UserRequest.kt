package forms.serverless.model


data class UserRequest(val creator: String, val firstName: String,val lastName: String?, val address: Address, val email: String, val cellPhone: String?, val homePhone: String?) {
    //fun toUser(): User
}