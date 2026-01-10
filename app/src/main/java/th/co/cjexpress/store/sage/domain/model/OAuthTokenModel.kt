package th.co.cjexpress.store.sage.domain.model

import java.util.Date

data class OAuthTokenModel(
    val name: String = "",
    val token: String = "",
    val expired: Date?,
) {
    fun isExpired(): Boolean {
        return Date().after(expired)
    }
}
