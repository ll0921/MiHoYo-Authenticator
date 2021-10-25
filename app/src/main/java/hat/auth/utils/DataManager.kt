package hat.auth.utils

import android.content.Context
import androidx.annotation.Keep
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import hat.auth.Application.Companion.context
import hat.auth.data.IAccount
import hat.auth.data.MiAccount
import hat.auth.data.TapAccount
import hat.auth.security.asEncryptedFile
import kotlinx.coroutines.launch
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

private fun File.g() = with(/*asEncryptedFile().*/readText()) {
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
    getFile()/*.asEncryptedFile()*/.writeText(it)
    list.add(this)
}

fun <T: IAccount> T.update() = apply {
    getFile()/*.asEncryptedFile()*/.writeText(gson.toJson(this))
}

infix fun IAccount.removeFrom(list: SnapshotStateList<IAccount>) = getFile().delete().apply {
    if (this) list.remove(this@removeFrom)
}

fun migrate() {
    val files = dataDir.listFiles()?.also {
        it.sortBy { f -> f.getCreationTime() }
    }
    if (files?.getOrNull(0)?.nameWithoutExtension?.toIntOrNull() != null) {
        files.map { it/*.asEncryptedFile()*/.readText() }.forEach {
            val a = gson.fromJson(it,Account::class.java)
            val (l,s) = a.lsToken
            val nA = MiAccount(
                uid = a.uid,
                guid = a.guid,
                name = a.name,
                ticket = a.ticket,
                lToken = l,
                sToken = s,
                avatar = a.avatar
            )
            Log.d("Migrate","#${a.uid} done.")
            nA addTo accountList
        }
    } else {
        files?.forEach {
            accountList.add(it.g())
        }
    }
}

@Keep
private data class Account(
    @SerializedName("uid")
    val uid: String = "0",
    @SerializedName("guid")
    val guid: String = "0",
    @SerializedName("name")
    val name: String = "null",
    @SerializedName("ticket")
    val ticket: String = "null",
    @SerializedName("tokens")
    val lsToken: LSToken = LSToken("null","null"),
    @SerializedName("aUrl")
    val avatar: String = "https://img-static.mihoyo.com/avatar/avatar1.png"
)

@Keep
private data class LSToken(
    val lToken: String,
    val sToken: String
)
