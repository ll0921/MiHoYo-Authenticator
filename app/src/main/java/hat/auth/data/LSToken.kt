package hat.auth.data

import androidx.annotation.Keep

@Keep
data class LSToken(
    val lToken: String,
    val sToken: String
) {

    val pair get() = lToken to sToken

    companion object {

        fun parse(map: Map<String, String>) = LSToken(
            checkNotNull(map["ltoken"]),
            checkNotNull(map["stoken"])
        )
    }
}
