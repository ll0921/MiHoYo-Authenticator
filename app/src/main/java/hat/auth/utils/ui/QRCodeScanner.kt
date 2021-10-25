package hat.auth.utils.ui

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.google.zxing.integration.android.IntentIntegrator
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.journeyapps.barcodescanner.CompoundBarcodeView
import hat.auth.activities.main.barcodeView
import hat.auth.activities.main.captureManager
import hat.auth.utils.Log
import hat.auth.utils.currentTimeMills

var lastScanTimestamp = 0L

@Composable
fun ComposeActivity.QRCodeScanner(
    modifier: Modifier = Modifier,
    status: String? = null,
    callback: BarcodeResult.() -> Unit
) {
    barcodeView = CompoundBarcodeView(this).apply {
        status?.let { setStatusText(status) }
        val intent = IntentIntegrator(this@QRCodeScanner).apply {
            setDesiredBarcodeFormats(IntentIntegrator.QR_CODE)
        }.createScanIntent()
        captureManager = CaptureManager(this@QRCodeScanner,this).apply {
            initializeFromIntent(intent,null)
            onResume()
            decode()
        }
        decodeContinuous { result ->
            if (currentTimeMills - lastScanTimestamp < 1000) return@decodeContinuous
            lastScanTimestamp = currentTimeMills
            Log.d("Scanner",result.text)
            result.run(callback)
        }
    }
    AndroidView(
        modifier = modifier,
        factory = { barcodeView!! }
    )
}
