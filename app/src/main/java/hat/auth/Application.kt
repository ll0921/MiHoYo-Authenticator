package hat.auth

import android.app.Application
import android.net.TrafficStats
import android.os.Build
import android.os.StrictMode
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.core.os.ExecutorCompat
import hat.auth.utils.buildThreadPolicy
import hat.auth.utils.buildVmPolicy
import java.util.concurrent.Executors

@Suppress("unused")
class Application : Application() {

    override fun onCreate() {
        super.onCreate()
        context = this
        if (BuildConfig.DEBUG) {
            buildVmPolicy {
                detectAll()
                penaltyLog()
             }.let {
                StrictMode.setVmPolicy(it)
            }
            buildThreadPolicy {
                detectAll()
                penaltyLog()
            }.let {
                StrictMode.setThreadPolicy(it)
            }
            StrictMode.noteSlowCall("SlowOperation")
            CrashHandler(this)
        }
    }

    companion object {
        lateinit var context: Application
    }

}