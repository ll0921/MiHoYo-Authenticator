package hat.auth.security

import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import androidx.security.crypto.MasterKey
import hat.auth.Application.Companion.context
import java.security.KeyFactory
import java.security.KeyStore
import java.security.PublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey

private const val KEY_NAME = "mAuth_k_ed"
private const val KEY_PROVIDER = "AndroidKeyStore"

private val keyStore by lazy {
    KeyStore.getInstance(KEY_PROVIDER).apply {
        load(null)
    }
}

val masterKey by lazy {
    MasterKey(context)
}

val secretKey: SecretKey
    get() = if (keyStore.containsAlias(KEY_NAME)) {
        (keyStore.getEntry(KEY_NAME,null) as KeyStore.SecretKeyEntry).secretKey
    } else {
        generateSecretKey(
            keystoreAlias = KEY_NAME,
            purposes = KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT
        ) {
            setBlockModes(KeyProperties.BLOCK_MODE_CBC)
            setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_PKCS7)
        }
    }

@Suppress("SameParameterValue")
private fun generateSecretKey(
    keystoreAlias: String,
    purposes: Int,
    parameterBuilder: KeyGenParameterSpec.Builder.() -> Unit
) = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,KEY_PROVIDER).apply {
    init(KeyGenParameterSpec.Builder(keystoreAlias,purposes).apply(parameterBuilder).build())
}.generateKey()

fun cipher(
    transformation: String = "AES/CBC/PKCS7Padding",
    provider: String? = null,
    func: Cipher.() -> Unit
): Cipher = if (provider == null) {
    Cipher.getInstance(transformation).apply(func)
} else {
    Cipher.getInstance(transformation,provider).apply(func)
}

fun X509EncodedKeySpec.toPublicKey(): PublicKey =
    KeyFactory.getInstance("RSA").generatePublic(this)
