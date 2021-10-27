package hat.auth.utils

import hat.auth.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object MiHoYoAuth {

    suspend fun login(
        username:String,
        password: EncryptedPassword,
        captchaData: CaptchaData
    ) = withContext(Dispatchers.IO) {
        /* 使用密码登录，获取用户ID和登录凭据 */
        var user = with(MiHoYoAPI.loginByPassword(username to password,captchaData)) {
            val i = getAsJsonObject("account_info")
            MiAccount(
                uid = i["account_id"].asString,
                ticket = i["weblogin_token"].asString
            )
        }
        /* 通过登录凭据获取两份令牌(ltoken,stoken) */
        user = MiHoYoAPI.getMultiTokenByLoginTicket(user)
        /* 获取米游社个人信息(头像Url) */
        val avatar = MiHoYoAPI.getAvatar(user)
        /* 获取玩家信息(UID,昵称) */
        val profile = MiHoYoAPI.getUserGameRolesByCookie(user).getOrNull(0)
        checkNotNull(profile) { "空账号" }
        user.copy(
            guid = profile.uid,
            name = profile.name,
            avatar = avatar
        )
    }

    suspend fun login(
        phone: String,
        code: String
    ) = withContext(Dispatchers.IO) {
        /* 使用验证码登录，获取用户ID和登录凭据 */
        var user = with(MiHoYoAPI.loginByMobileCaptcha(phone,code)) {
            val i = getAsJsonObject("account_info")
            MiAccount(
                uid = i["account_id"].asString,
                ticket = i["weblogin_token"].asString
            )
        }
        /* 通过登录凭据获取两份令牌(ltoken,stoken) */
        user = MiHoYoAPI.getMultiTokenByLoginTicket(user)
        /* 获取米游社个人信息(头像Url) */
        val avatar = with(MiHoYoAPI.getUserFullInfo(user)) {
            getAsJsonObject("user_info")["avatar_url"].asString
        }
        /* 获取玩家信息(UID,昵称) */
        val profile = MiHoYoAPI.getUserGameRolesByCookie(user).getOrNull(0)
        checkNotNull(profile) { "空账号" }
        /* 创建Account对象并返回 */
        user.copy(
            guid = profile.uid,
            name = profile.name,
            avatar = avatar
        )
    }
}
