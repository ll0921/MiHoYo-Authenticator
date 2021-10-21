package hat.auth.activities

import android.os.Bundle
import androidx.compose.material.ExperimentalMaterialApi
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.microsoft.appcenter.AppCenter
import com.microsoft.appcenter.analytics.Analytics
import com.microsoft.appcenter.crashes.Crashes
import hat.auth.utils.GT3
import hat.auth.utils.checkUpdate
import hat.auth.utils.getAccountList
import hat.auth.utils.ui.ComposeActivity

class MainActivity : ComposeActivity() {

    @OptIn(ExperimentalMaterialApi::class,ExperimentalPermissionsApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setMainTheme()
        getAccountList()
        init { UI() }
        GT3.init(this)
        AppCenter.start(
            application,"1c793f09-3bc5-4eb7-984c-b5f3d975601f",
            Analytics::class.java,
            Crashes::class.java
        )
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

}