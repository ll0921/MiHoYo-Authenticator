package hat.auth.data

import com.google.gson.annotations.SerializedName
import hat.auth.utils.digest

data class Account(
    @SerializedName("uid")
    val uid: String = "0",
    @SerializedName("guid")
    val guid: String = "0",
    @SerializedName("name")
    val name: String = "null",
    @SerializedName("ticket")
    val ticket: String = "null",
    @SerializedName("tokens")
    val lsToken: LSToken = LSToken("null","null"),
    @SerializedName("aUrl")
    val avatar: String = "https://img-static.mihoyo.com/avatar/avatar1.png"
) {

    val aHash get() = avatar.digest("MD5")

}