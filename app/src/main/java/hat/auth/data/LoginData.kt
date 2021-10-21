package hat.auth.data

data class PLData(
    val username: String,
    val password: EncryptedPassword
)

data class CLData(
    val phone: String,
    val code : String
)
