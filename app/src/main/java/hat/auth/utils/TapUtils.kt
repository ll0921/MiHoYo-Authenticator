package hat.auth.utils

import com.google.gson.JsonObject
import com.google.gson.annotations.SerializedName
import hat.auth.data.TapAccount
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import okhttp3.Request
import okhttp3.Response

val TapUrlRegex = Regex("https://www\\.taptap\\.com/qrcode/to\\?url=https%3A%2F%2Fwww\\.taptap\\.com%2Fdevice%3Fqrcode%3D1%26user_code%3D\\w{5}")

data class Profile(val name: String, val avatar: String)

@JvmInline
value class ConfirmPage(private val s: String) {

    val param get() = PARAM_REGEX.findValue(s).substringAfter('"')
    val token get() = TOKEN_REGEX.findValue(s).substringAfter('"')

    fun getBasicProfile() = Profile(
        name   = NAME_REGEX.findValue(s).dropLast(1),
        avatar = AVATAR_REGEX.findValue(s)
    )

    private companion object {
        val NAME_REGEX = Regex("(?<=auth__user-name\">)\\S+<")
        val PARAM_REGEX = Regex("(?<=name=\"params\")\\s+value=\"[a-zA-z0-9]+(?=\">)")
        val TOKEN_REGEX = Regex("(?<=name=\"_token\")\\s+value=\"[a-zA-z0-9]+(?=\">)")
        val AVATAR_REGEX = Regex("https://img3\\.tapimg\\.com/default_avatars/[a-z0-9]+\\.jpg")
    }
}

private fun Regex.findValue(s: String) = find(s)!!.value

object TapAPI {

    suspend fun getCode() = getJson(
        url = "https://www.taptap.com/oauth2/v1/device/code",
        headers = mapOf(
            "User-Agent" to "TapTapUnitySDK/1.0 UnityPlayer/2017.4.30f1",
            "Content-Type" to "application/x-www-form-urlencoded",
            "X-Unity-Version" to "2017.4.30f1"
        ),
        postBody = buildFormBody {
            add("version","1.0.1")
            add("platform","unity")
            add("scope","public_profile")
            add("response_type","device_code")
            add("client_id","WT6NfH8PsSmZtyXNFb")
            add("info","{\"device_id\":\"Windows PC\"}")
        }
    ).checkSuccess().toDataClass(TapOAuthCode::class.java)

    suspend fun getPage(url: String,u: TapAccount) = withContext(Dispatchers.IO) {
        buildHttpRequest {
            url(url)
            addCookie(u)
        }.execute().run {
            u.copy(
                sid = getCookieMap().getValue("ACCOUNT_SID")
            ) to ConfirmPage(getStringBody())
        }
    }

    suspend fun TapOAuthCode.getPage(u: TapAccount) = getPage(url,u)

    suspend fun ConfirmPage.confirm(
        u: TapAccount,
        cUrl: String = "https://www.taptap.com/device"
    ) = withContext(Dispatchers.IO) {
        buildHttpRequest {
            url(cUrl)
            addCookie(u)
            postFormBody {
                add("params",param)
                add("_token",token)
                add("scope","public_profile+")
                add("approve","1")
            }
        }.execute(OkClients.NO_REDIRECT).code == 302
    }

    data class TapOAuthCode(
        @SerializedName("device_code")
        val deviceCode: String,
        @SerializedName("user_code")
        val user: String,
        @SerializedName("verification_url")
        val verificationUrl: String,
        @SerializedName("qrcode_url")
        private val qrcodeUrl: String
    ) {
        val url get() = checkNotNull(qrcodeUrl.toHttpUrl().queryParameterValue(0))
    }

    private fun Request.Builder.addCookie(u: TapAccount) =
        addHeader("Cookie",u.toString())

    private fun Response.getCookieMap() = headers("set-cookie").associate { s ->
        s.split(";")[0].split("=").let { it[0] to it[1] }
    }

    private fun Response.getStringBody() = notNullBody.string()

    private fun JsonObject.checkSuccess(): JsonObject = run {
        if (!this["success"].asBoolean) throw IllegalStateException()
        getAsJsonObject("data")
    }
}
