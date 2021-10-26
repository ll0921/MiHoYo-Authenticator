package hat.auth.activities.main

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.zIndex
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import hat.auth.Application.Companion.context
import hat.auth.R
import hat.auth.activities.MainActivity
import hat.auth.activities.TapAuthActivity
import hat.auth.data.IAccount
import hat.auth.data.MiAccount
import hat.auth.data.TapAccount
import hat.auth.data.avatarHash
import hat.auth.utils.*
import hat.auth.utils.TapAPI.confirm
import hat.auth.utils.TapAPI.getPage
import hat.auth.utils.ui.CircularProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.HttpUrl.Companion.toHttpUrl
import java.io.File

private val cJobs = mutableMapOf<String, Job>()
val loadedBitmaps = mutableStateMapOf<String,ImageBitmap>()

private val imageCacheDir by lazy {
    File(context.cacheDir,"imageCache").apply { mkdir() }
}

private val defaultAvatar by lazy {
    getDrawableAsImageBitmap(R.drawable.ic_avatar_default)
}

val unknownAvatar by lazy {
    getDrawableAsImageBitmap(R.drawable.ic_unknown)
}

private suspend fun MainActivity.getAvatar(
    url: String,
    callback: (ImageBitmap) -> Unit
) = withContext(Dispatchers.IO) {
    val cache = File(imageCacheDir,url.digest("MD5"))
    if (cache.exists()) {
        BitmapFactory.decodeFile(cache.absolutePath)
    } else {
        getBitmap(url)?.apply {
            cache.outputStream().runCatching {
                compress(Bitmap.CompressFormat.PNG,100,this)
            }.onFailure {
                it.printStackTrace()
            }
        }
    }?.asImageBitmap()?.let(callback)
}

fun MainActivity.loadImage(
    tid: String,
    url: String,
    urlHash: String,
    map: MutableMap<String, ImageBitmap>
) {
    if (cJobs[tid] == null) {
        cJobs[tid] = ioScope.launch {
            getAvatar(url) {
                map[urlHash] = it
            }
            cJobs.remove(tid)
        }
    }
}

var currentAccount by mutableStateOf(IAccount("","",""))

fun MainActivity.processException(e: Throwable) {
    Log.e(tr = e)
    toast(e.message ?: "未知错误")
}

/** =========================================== Main =========================================== **/

@Composable
@ExperimentalMaterialApi
@ExperimentalAnimationApi
@ExperimentalComposeUiApi
@ExperimentalPermissionsApi
fun MainActivity.UI() {
    val lst = remember { accountList }
    TopAppBar(
        a = lst.size,
        normalDropdownItems = buildDropdownMenuItems {
            add("米哈游登录") {
                showMiHuYoLoginDialog()
            }
            add("Taptap登录(Beta)") {
                launcher.launch(Intent(this@UI,TapAuthActivity::class.java))
            }
        },
        debugDropdownItems = buildDropdownMenuItems {
            add("decrypt") {
                ioScope.launch {
                    decryptAll()
                    toast("done.")
                }
            }
        }
    )
    val avatars = remember { loadedBitmaps }
    LazyColumn(
        modifier = Modifier.fillMaxSize().background(Color.White).zIndex(-233F)
    ) {
        items(lst) { a ->
            AccountItem(
                ia = a,
                avatar = avatars[a.avatarHash].let {
                    if (it == null) {
                        loadImage(a.uid,a.avatar,a.avatarHash,avatars)
                        defaultAvatar
                    } else it
                },
                onInfoClick = {
                    ioScope.launch {
                        isLoadingDialogShowing = true
                        runCatching {
                            val mA = currentAccount as MiAccount
                            val dn = MiHoYoAPI.getDailyNote(mA)
                            val gr = MiHoYoAPI.getGameRecord(mA)
                            val jn = with(MiHoYoAPI.getCookieToken(mA.uid,mA.sToken)) {
                                MiHoYoAPI.getJournalNote(mA,this)
                            }
                            showInfoDialog(dn,gr,jn)
                        }.onFailure {
                            processException(it)
                        }
                        isLoadingDialogShowing = false
                    }
                },
                onTestClick = {
                    ioScope.launch {
                        isLoadingDialogShowing = true
                        runCatching {
                            (currentAccount as? TapAccount)?.let {
                                val c = TapAPI.getCode()
                                val s = with(c.getPage(it)) {
                                    second.confirm(first,c.verificationUrl)
                                }
                                toast("Success: $s")
                            }
                        }.onFailure {
                            processException(it)
                        }
                        isLoadingDialogShowing = false
                    }
                }
            ) {
                showQRCodeScannerDialog()
            }
        }
    }
    InfoDialog()
    AboutDialog()
    LoadingDialog()
    MiHoYoLoginDialog()
    DeleteAccountDialog()
    QRCodeScannerDialog()
}

fun MainActivity.onCookieReceived(s: String) {
    ioScope.launch {
        isLoadingDialogShowing = true
        runCatching {
            val a0 = with(cookieStringToMap(s)) {
                TapAccount(
                    acw = getValue("acw_tc"),
                    locale = getValue("locale"),
                    uid = getValue("user_id"),
                    logFrom = getValue("ACCOUNT_LOGGED_USER_FROM_WWW"),
                    tokenFrom = getValue("CONSOLES_TOKEN_FROM_WWW"),
                    xToken = getValue("XSRF-TOKEN"),
                    session = getValue("tap_sess")
                )
            }
            check(!a0.exists()) { "已经存在相同UID的账户了" }
            with(TapAPI.getCode().getPage(a0)) {
                val p = second.getBasicProfile()
                first.copy(
                    name = p.name,
                    avatar = p.avatar
                )
            } addTo accountList
        }.onFailure {
            processException(it)
        }
        isLoadingDialogShowing = false
    }
}

fun MainActivity.registerScanCallback() = registerScanCallback { result ->
    ioScope.launch {
        val u = result.text
        isLoadingDialogShowing = true
        runCatching {
            when (currentAccount) {
                is MiAccount -> {
                    MiHoYoAPI.scanQRCode(u)
                    MiHoYoAPI.confirmQRCode(currentAccount as MiAccount,u)
                }
                is TapAccount -> {
                    val au = checkNotNull(u.toHttpUrl().queryParameterValue(0))
                    with(getPage(au,currentAccount as TapAccount)) {
                        second.confirm(first)
                    }
                }
                else -> throw IllegalArgumentException("Unknown account type.")
            }
            toast("登录成功")
        }.onFailure {
            processException(it)
        }
        isLoadingDialogShowing = false
    }
}

private var loadingDialogText by mutableStateOf("正在处理请求")
private var isLoadingDialogShowing by mutableStateOf(false)

fun showLoadingDialog(msg: String = "") {
    if (msg.isNotEmpty()) loadingDialogText = msg
    isLoadingDialogShowing = true
}

fun hideLoadingDialog() {
    loadingDialogText = "正在处理请求"
    isLoadingDialogShowing = false
}

@Composable
fun MainActivity.LoadingDialog() = run {
    if (isLoadingDialogShowing) CircularProgressDialog(loadingDialogText)
}
