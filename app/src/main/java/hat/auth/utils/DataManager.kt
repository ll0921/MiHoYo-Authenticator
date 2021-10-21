package hat.auth.utils

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import hat.auth.Application.Companion.context
import hat.auth.data.Account
import hat.auth.security.asEncryptedFile
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.io.File

private val accountDataDir by lazy {
    context.getDir("accounts",Context.MODE_PRIVATE)
}

val accountList = mutableStateListOf<Account>()

fun getAccountList() = ioScope.launch {
    accountDataDir.listFiles()?.also { f ->
        f.sortBy { it.lastModified() }
    }?.forEach {
        accountList.add(it.nameWithoutExtension.g())
    }
}

fun Account.exists() = File(accountDataDir,uid).exists()

infix fun Account.addTo(list: SnapshotStateList<Account>) = Gson().toJson(this).let {
    File(accountDataDir,uid).asEncryptedFile().writeText(it)
    list.add(this)
}

private fun String.g() = File(accountDataDir,this).asEncryptedFile().let {
    Gson().fromJson(it.readText(),Account::class.java)
}!!

@Suppress("unused")
fun update() {
    // TODO
}

infix fun Account.removeFrom(list: SnapshotStateList<Account>) = File(accountDataDir,uid).delete().apply {
    if (this) list.remove(this@removeFrom)
}
