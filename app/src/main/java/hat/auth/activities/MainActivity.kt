package hat.auth.activities

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.ExperimentalMaterialApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import hat.auth.activities.main.*
import hat.auth.data.TapAccount
import hat.auth.utils.*
import hat.auth.utils.GT3.initGeetest
import hat.auth.utils.ui.ComposeActivity

class MainActivity : ComposeActivity() {

    lateinit var launcher: ActivityResultLauncher<Intent>

    @OptIn(ExperimentalMaterialApi::class,ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainTheme()
        loadAccountList()
        init { UI() }
        initGeetest()
        startAnalytics()
        registerScanCallback()
        registerLauncher()
    }

    override fun onResume() {
        super.onResume()
        captureManager?.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager?.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        GT3.onDestroy()
        captureManager?.onDestroy()
    }

    private fun registerLauncher() {
        launcher = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) callback@{
            if (it.resultCode == 1001) {
                val s = checkNotNull(it.data?.getStringExtra("s"))
                onCookieReceived(s)
            }
        }
    }

}