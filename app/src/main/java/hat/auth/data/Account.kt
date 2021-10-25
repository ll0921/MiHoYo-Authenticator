package hat.auth.data

import hat.auth.utils.digest
import com.google.gson.annotations.SerializedName
import hat.auth.utils.cookieStringToMap

open class IAccount(
    @Ignore
    open val uid:String,
    @Ignore
    open val name: String,
    @Ignore
    open val avatar: String
)

data class MiAccount(
    @SerializedName("a")
    val guid: String = "0",
    @SerializedName("b")
    val ticket: String = "null",
    @SerializedName("c")
    val lToken: String = "null",
    @SerializedName("d")
    val sToken: String = "null",
    @SerializedName("e")
    override val uid: String = "0",
    @SerializedName("f")
    override val name: String = "null",
    @SerializedName("g")
    override val avatar: String = "https://img-static.mihoyo.com/avatar/avatar1.png"
): IAccount(uid,name,avatar)

val IAccount.avatarHash get() = avatar.digest("MD5")

data class TapAccount(
    @CookieName("locale")
    @SerializedName("a")
    val locale: String = "zh_CN",
    @CookieName("acw_tc")
    @SerializedName("b")
    val acw: String = "",
    @CookieName("ACCOUNT_SID")
    @SerializedName("d")
    val sid: String = "",
    @CookieName("tap_sess")
    @SerializedName("e")
    val session: String = "",
    @CookieName("XSRF-TOKEN")
    @SerializedName("f")
    val xToken: String = "",
    @CookieName("ACCOUNT_LOGGED_USER_FROM_WWW")
    @SerializedName("g")
    val logFrom: String = "",
    @CookieName("CONSOLES_TOKEN_FROM_WWW")
    @SerializedName("h")
    val tokenFrom: String = "",
    @CookieName("user_id")
    @SerializedName("c")
    override val uid: String = "",
    @SerializedName("i")
    override val name: String = "null",
    @SerializedName("j")
    override val avatar: String = "https://img.tapimg.com/market/images/22f1196f825298281376608459bfa7fe.png"
): IAccount(uid,name,avatar) {

    override fun toString(): String {
        val list = mutableListOf<String>()
        this::class.java.declaredFields.filter {
            it.isAnnotationPresent(CookieName::class.java)
        }.forEach {
            with(it.get(this)?.toString() ?: "") {
                val name = it.getAnnotation(CookieName::class.java)!!.value
                if (isNotEmpty()) list.add("$name=$this")
            }
        }
        return list.joinToString("; ")
    }
}

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class CookieName(val value: String)

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class Ignore
