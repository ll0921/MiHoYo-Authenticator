package hat.auth.data

import hat.auth.security.Base64
import hat.auth.security.cipher
import hat.auth.security.toPublicKey
import java.security.spec.X509EncodedKeySpec
import javax.crypto.Cipher

@JvmInline
value class EncryptedPassword(private val p: String) {

    fun get() = p.encrypt()

    private companion object {

        const val PublicKey = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDDvekdPMHN3AYhm/vktJT+YJr7cI5DcsNKqdsx5DZX0gDuWFuIjzdwButrIYPNmRJ1G8ybDIF7oDW2eEpm5sMbL9zs\n9ExXCdvqrn51qELbqj0XxtMTIpaCHFSI50PfPpTFV9Xt/hmyVwokoOXFlAEgCn+Q\nCgGs52bFoYMtyi+xEQIDAQAB\n"

        fun String.encrypt() = Base64.decode(PublicKey).let {
            cipher("RSA/NONE/PKCS1Padding") {
                init(Cipher.ENCRYPT_MODE, X509EncodedKeySpec(it).toPublicKey())
            }.doFinal(encodeToByteArray())
        }.let {
            Base64.encode(it)
        }
    }
}
