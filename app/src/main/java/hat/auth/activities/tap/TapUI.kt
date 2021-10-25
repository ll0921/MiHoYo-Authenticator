package hat.auth.activities.tap

import android.annotation.SuppressLint
import android.content.Intent
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import hat.auth.Application
import hat.auth.BuildConfig
import hat.auth.activities.TapAuthActivity

@SuppressLint("StaticFieldLeak")
private lateinit var cWebView: WebView

@Composable
fun TapAuthActivity.UI() = run {
    WebView(Modifier.fillMaxSize())
}

fun TapAuthActivity.getWebView() = run { cWebView }

fun TapAuthActivity.startLoad() = run {
    CookieManager.getInstance().removeAllCookies(null)
    cWebView.loadUrl("https://www.taptap.com/auth/login")
}

fun TapAuthActivity.initWebView() {
    cWebView = WebView(Application.context).apply {
        enableJavaScript()
        importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO
        if (BuildConfig.DEBUG) {
            WebView.setWebContentsDebuggingEnabled(true)
        }
        webViewClient = TapWebViewClient {
            setResult(1001,Intent().putExtra("s",it))
            finishAfterTransition()
        }
    }
}

fun TapAuthActivity.destroyWebView() = run { cWebView.destroy() }

@Composable
private fun WebView(
    modifier: Modifier = Modifier
) = AndroidView(
    factory = { cWebView },
    modifier = modifier
)

@SuppressLint("SetJavaScriptEnabled")
private fun WebView.enableJavaScript() = apply { settings.javaScriptEnabled = true }
