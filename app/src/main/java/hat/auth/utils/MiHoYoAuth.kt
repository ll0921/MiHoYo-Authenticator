package hat.auth.utils

import hat.auth.data.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun PLData.login(
    captchaData: CaptchaData
) = withContext(Dispatchers.IO) {
    /* 使用密码登录，获取用户ID和登录凭据 */
    val accountData = MiHoYoAPI.loginByPassword(this@login,captchaData)
    val (uid,wToken) = accountData.getAsJsonObject("account_info").let {
        it["account_id"].asString to it["weblogin_token"].asString
    }
    /* 通过登录凭据获取两份令牌(ltoken,stoken) */
    val tokens = MiHoYoAPI.getMultiTokenByLoginTicket(uid,wToken)
    val (lToken,sToken) = tokens.pair
    /* 获取米游社个人信息(头像Url) */
    val userInfo = MiHoYoAPI.getUserFullInfo(uid,sToken)
    val avatar = userInfo.getAsJsonObject("user_info")["avatar_url"].asString
    /* 获取玩家信息(UID,昵称) */
    val gameRoles = MiHoYoAPI.getUserGameRolesByCookie(uid,lToken)
    val sProfile = gameRoles.getOrNull(0)
    checkNotNull(sProfile) { "空账号" }
    /* 创建Account对象并返回 */
    Account(
        uid = uid,
        guid = sProfile.uid,
        name = sProfile.name,
        ticket = wToken,
        lsToken = tokens,
        avatar = avatar
    )
}

suspend fun CLData.login() = withContext(Dispatchers.IO) {
    /* 使用验证码登录，获取用户ID和登录凭据 */
    val accountData = MiHoYoAPI.loginByMobileCaptcha(phone,code)
    val accountInfo = accountData.getAsJsonObject("account_info")
    val uid = accountInfo["account_id"].asString
    val webLoginToken = accountInfo["weblogin_token"].asString
    /* 通过登录凭据获取两份令牌(ltoken,stoken) */
    val tokens = MiHoYoAPI.getMultiTokenByLoginTicket(uid,webLoginToken)
    val (lToken,sToken) = tokens.pair
    /* 获取米游社个人信息(头像Url) */
    val userInfo = MiHoYoAPI.getUserFullInfo(uid, sToken)
    val avatar = userInfo.getAsJsonObject("user_info")["avatar_url"].asString
    /* 获取玩家信息(UID,昵称) */
    val gameRoles = MiHoYoAPI.getUserGameRolesByCookie(uid, lToken)
    val sProfile = gameRoles.getOrNull(0)
    checkNotNull(sProfile) { "空账号" }
    /* 创建Account对象并返回 */
    Account(
        uid = uid,
        guid = sProfile.uid,
        name = sProfile.name,
        ticket = webLoginToken,
        lsToken = tokens,
        avatar = avatar
    )
}
