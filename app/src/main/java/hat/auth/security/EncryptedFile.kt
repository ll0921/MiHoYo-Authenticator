package hat.auth.security

import androidx.security.crypto.EncryptedFile
import hat.auth.Application.Companion.context
import java.io.File

class EncryptedFile(
    private val file: File
) {

    fun readText() = readBytes().decodeToString()

    @Suppress("MemberVisibilityCanBePrivate")
    fun readBytes() = file.asEncryptedFile.openFileInput().use {
        it.readBytes()
    }

    fun writeText(text: String) = writeBytes(text.encodeToByteArray())

    @Suppress("MemberVisibilityCanBePrivate")
    fun writeBytes(b: ByteArray) = file.asEncryptedFile.openFileOutput().use {
        it.write(b)
        it.flush()
    }
}

fun File.asEncryptedFile() = EncryptedFile(this)

private val File.asEncryptedFile get() = EncryptedFile(context,this,masterKey)
