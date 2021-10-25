package hat.auth.activities

import android.os.Bundle
import android.view.KeyEvent
import hat.auth.activities.tap.*
import hat.auth.utils.ui.ComposeActivity

class TapAuthActivity : ComposeActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init { UI() }
        initWebView()
        startLoad()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyWebView()
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        val w = getWebView()
        return if (w.canGoBack()) {
            w.goBack()
            true
        } else super.onKeyDown(keyCode, event)
    }

}
