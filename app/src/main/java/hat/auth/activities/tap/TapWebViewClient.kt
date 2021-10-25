package hat.auth.activities.tap

import android.graphics.Bitmap
import android.webkit.*
import java.net.URL

class TapWebViewClient(
    private val onAuthFinished: (String) -> Unit
) : WebViewClient() {

    private var shouldStopLoading = false

    override fun onPageStarted(v: WebView, u: String, f: Bitmap?) {
        val p = URL(u).path
        if (p == "/auth/login" || p == "/auth/email/login" || p == "/terms" || p == "/privacy-policy") {
            v.evaluateJavascript(script) {}
        } else {
            shouldStopLoading = true
            v.stopLoading()
            onAuthFinished(CookieManager.getInstance().getCookie("https://www.taptap.com")!!)
        }
    }

    override fun shouldInterceptRequest(v: WebView, r: WebResourceRequest): WebResourceResponse? {
        return if (shouldStopLoading) null else super.shouldInterceptRequest(v, r)
    }

    private val script = """
        let ia = null
        const ib = document.location.pathname

        if (ib === "/auth/login" || ib === "/auth/email/login") {
            ia = window.setInterval(a,200)
        } else if (ib === "/terms" || ib === "/privacy-policy") {
            ia = window.setInterval(b,200)
        }

        function b() {
            try {
                $("body").css("padding-top",0)
                $(".left").css("display","none")
                $(".footer").css("display","none")
                $("#navbar").css("display","none")
                $("#topBanner").css("display","none")
                $(".doc-template").css("padding-top",0)
                window.clearInterval(ia)
            } catch (ignored) { }
        }

        function a() {
            try {
                $("body").css("overflow-y","hidden")
                $(".taptap__main").css("padding-top","10px")
                $(".footer").css("height",window.screen.height + "px")
                $(".taptap__main-header").remove()
                document.getElementsByClassName("third-account")[0].style.display = "none"
                document.getElementsByClassName("footer")[0].firstElementChild.style.display = "none"
                window.clearInterval(ia)
            } catch (ignored) { }
        }
        """.trimIndent()
}