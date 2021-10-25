package hat.auth.utils

import android.util.Log
import hat.auth.BuildConfig
import okhttp3.logging.HttpLoggingInterceptor
import java.io.Serializable

@Suppress("unused")
object Log {

    private val debug = BuildConfig.DEBUG

    val okLogger = HttpLoggingInterceptor.Logger {
        if (debug) Log.d("OkLogger",it)
    }

    fun d(tag: String, msg: Serializable) {
        if (debug) Log.d(tag, msg.toString())
    }

    fun i(tag: String,msg: String) = Log.i(tag, msg)

    fun w(tag: String,msg: String) = Log.w(tag, msg)

    fun e(tag: String,msg: String) = Log.e(tag, msg)

    fun d(tag: String = "ExceptionLog",msg: String = "",tr: Throwable) {
        if (debug) Log.d(tag, msg, tr)
    }

    fun i(tag: String = "ExceptionLog",msg: String = "",tr: Throwable) = Log.i(tag, msg, tr)

    fun w(tag: String = "ExceptionLog",msg: String = "",tr: Throwable) = Log.w(tag, msg, tr)

    fun e(tag: String = "ExceptionLog",msg: String = "",tr: Throwable) = Log.e(tag, msg, tr)

}
