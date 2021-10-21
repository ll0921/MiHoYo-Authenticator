package hat.auth.activities

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.Settings
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.DecoratedBarcodeView
import hat.auth.utils.QRCodeUrlRegex
import hat.auth.utils.toast
import hat.auth.utils.ui.PermissionRequiredDialog
import hat.auth.utils.ui.QRCodeScanner
import hat.auth.utils.ui.TextButton

var captureManager by mutableStateOf<CaptureManager?>(null)
var barcodeView    by mutableStateOf<DecoratedBarcodeView?>(null)

private fun stopCamera() {
    captureManager?.onPause()
    captureManager = null
    barcodeView = null
}

private var isDialogShowing by mutableStateOf(false)

fun showQRCodeScannerDialog() { isDialogShowing = true }

@Composable
@ExperimentalPermissionsApi
fun MainActivity.QRCodeScannerDialog(
    callback: (BarcodeResult) -> Unit
) {
    if (isDialogShowing) QCD(callback)
}

@Composable
@ExperimentalPermissionsApi
private fun MainActivity.QCD(
    callback: (BarcodeResult) -> Unit,
    onDismissRequest: () -> Unit = { isDialogShowing = false }
) = PermissionRequiredDialog(
    permission = Manifest.permission.CAMERA,
    permissionNotGrantedContent = {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text("需要权限")
            },
            text = {
                Text(
                    buildAnnotatedString {
                        append("扫描二维码需要您授予")
                        withStyle(SpanStyle(Color.Red)) {
                            append(" 相机 ")
                        }
                        append("权限")
                    }
                )
            },
            confirmButton = {
                TextButton("确定") {
                    it.launchPermissionRequest()
                }
            }
        )
    },
    permissionNotAvailableContent = {
        AlertDialog(
            onDismissRequest = onDismissRequest,
            title = {
                Text("您拒绝了权限申请")
            },
            text = {
                Text(
                    buildAnnotatedString {
                        append("应用程序无法在不使用")
                        withStyle(SpanStyle(Color.Red)) {
                            append(" 相机 ")
                        }
                        append("权限的情况下正常提供服务")
                    }
                )
            },
            confirmButton = {
                TextButton("打开应用设置") {
                    startActivity(
                        Intent(
                            Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", packageName, null)
                        )
                    )
                }
            }
        )
    }
) {
    Dialog(
        onDismissRequest = {
            onDismissRequest()
            stopCamera()
        }
    ) {
        QRCodeScanner(
            status = currentAccount.uid,
            modifier = Modifier
                .size(275.dp)
                .clip(RoundedCornerShape(15.dp)),
            callback = {
                if (text.matches(QRCodeUrlRegex)) {
                    stopCamera()
                    onDismissRequest()
                    callback(this)
                } else {
                    toast("无效的二维码")
                }
            }
        )
    }
}
