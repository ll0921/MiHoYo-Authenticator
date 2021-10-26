package hat.auth.utils

import android.content.Context
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.JsonObject
import hat.auth.Application.Companion.context
import hat.auth.data.IAccount
import hat.auth.data.MiAccount
import hat.auth.data.TapAccount
import hat.auth.security.asEncryptedFile
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.nio.file.Files
import java.nio.file.attribute.BasicFileAttributes

private val dataDir by lazy {
    context.getDir("accounts",Context.MODE_PRIVATE)
}

val accountList = mutableStateListOf<IAccount>()

fun loadAccountList() = ioScope.launch {
    migrate()
}

fun File.getCreationTime() =
    Files.readAttributes(toPath(),BasicFileAttributes::class.java).creationTime().toMillis()

private fun File.g() = with(asEncryptedFile().readText()) {
    when (this@g.nameWithoutExtension[0]) {
        'm' -> {
            gson.fromJson(this,MiAccount::class.java)
        }
        't' -> {
            gson.fromJson(this,TapAccount::class.java)
        }
        else -> throw IllegalArgumentException()
    }
}

private fun IAccount.getFile() = run {
    when (this) {
        is MiAccount  -> File(dataDir,"m_$uid")
        is TapAccount -> File(dataDir,"t_$uid")
        else -> throw IllegalArgumentException()
    }
}

fun IAccount.exists() = getFile().exists()

infix fun IAccount.addTo(list: SnapshotStateList<IAccount>) = gson.toJson(this).let {
    getFile().asEncryptedFile().writeText(it)
    list.add(this)
}

fun <T: IAccount> T.update() = apply {
    getFile().asEncryptedFile().writeText(gson.toJson(this))
}

infix fun IAccount.removeFrom(list: SnapshotStateList<IAccount>) = getFile().delete().apply {
    if (this) list.remove(this@removeFrom)
}

suspend fun decryptAll() = withContext(Dispatchers.IO) {
    dataDir.listFiles()?.forEach {
        File(dataDir,"d_${it.name}").run {
            createNewFile()
            writeText(it.asEncryptedFile().readText())
        }
    }
}

fun migrate() {
    val files = dataDir.listFiles()?.also {
        it.sortBy { f -> f.getCreationTime() }
    }
    if (files?.getOrNull(0)?.nameWithoutExtension?.toIntOrNull() != null) {
        files.forEach {
            val old = it.asEncryptedFile().readText()
            val a = gson.fromJson(old,JsonObject::class.java)
            val b = a.getAsJsonObject("tokens")
            val nA = MiAccount(
                uid = a["uid"].asString,
                guid = a["guid"].asString,
                name = a["name"].asString,
                ticket = a["ticket"].asString,
                lToken = b["lToken"].asString,
                sToken = b["sToken"].asString,
                avatar = a["aUrl"].asString
            )
            Log.i("Migrate", a["uid"].asString)
            nA addTo accountList
            it.delete()
        }
    } else {
        files?.forEach {
            accountList.add(it.g())
        }
    }
}
