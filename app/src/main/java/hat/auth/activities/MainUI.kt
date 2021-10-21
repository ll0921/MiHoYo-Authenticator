package hat.auth.activities

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import hat.auth.Application.Companion.context
import hat.auth.R
import hat.auth.data.Account
import hat.auth.utils.*
import hat.auth.utils.ui.CircularProgressDialog
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

/** ========================================== Avatar ========================================== **/

private val cJobs = mutableMapOf<String, Job>()
val loadedBitmaps = mutableStateMapOf<String,ImageBitmap>() // TODO: Move to utils

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

fun MainActivity.loadAvatar(
    uid: String,
    url: String,
    urlHash: String,
    avatars: MutableMap<String, ImageBitmap>
) {
    if (cJobs[uid] == null) {
        cJobs[uid] = ioScope.launch {
            getAvatar(url) {
                avatars[urlHash] = it
            }
            cJobs.remove(uid)
        }
    }
}

var currentAccount by mutableStateOf(Account())

fun MainActivity.processException(e: Throwable) {
    Log.d(tr = e)
    toast(e.message ?: "未知错误")
}

/** =========================================== Main =========================================== **/

@Composable
@ExperimentalMaterialApi
@ExperimentalPermissionsApi
fun MainActivity.UI() {
    val lst = remember { accountList }
    TopAppBar(
        a = lst.size,
        normalDropdownItems = buildDropdownMenuItems {
            add("验证码登录") {
                showCodeLoginDialog()
            }
            add("密码登录") {
                showPasswordLoginDialog()
            }
        },
        debugDropdownItems = buildDropdownMenuItems { }
    )
    val avatars = remember { loadedBitmaps }
    LazyColumn(
        modifier = Modifier.fillMaxSize()
    ) {
        items(lst) { a ->
            AccountsColumnItem(
                account = a,
                avatar = avatars[a.aHash].let {
                    if (it == null) {
                        loadAvatar(a.uid,a.avatar,a.aHash,avatars)
                        defaultAvatar
                    } else it
                },
                onInfoClick = {
                    ioScope.launch {
                        isLoadingDialogShowing = true
                        runCatching {
                            val dn = MiHoYoAPI.getDailyNote(currentAccount)
                            val gr = MiHoYoAPI.getGameRecord(currentAccount)
                            val jn = with(MiHoYoAPI.getCookieToken(
                                currentAccount.uid,
                                currentAccount.lsToken.sToken)) {
                                MiHoYoAPI.getJournalNote(currentAccount,this)
                            }
                            showInfoDialog(dn,gr,jn)
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
    CodeLoginDialog()
    DeleteAccountDialog()
    PasswordLoginDialog()
    QRCodeScannerDialog { result ->
        ioScope.launch {
            val u = result.text
            isLoadingDialogShowing = true
            runCatching {
                MiHoYoAPI.scanQRCode(u)
                MiHoYoAPI.confirmQRCode(currentAccount,u).checkRetCode()
                toast("登录成功")
            }.onFailure {
                processException(it)
            }
            isLoadingDialogShowing = false
        }
    }
}

private var isLoadingDialogShowing by mutableStateOf(false)

@Composable
fun MainActivity.LoadingDialog() = run {
    if (isLoadingDialogShowing) CircularProgressDialog("正在处理请求")
}
