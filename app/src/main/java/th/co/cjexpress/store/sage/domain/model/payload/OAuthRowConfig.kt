package th.co.cjexpress.store.sage.domain.model.payload

import java.io.Serializable

data class OAuthRowConfig(
    val url: String,
    val grantType: String,
    val clientId: String?,
    val clientSecret: String?,
    val username: String?,
    val password: String?,
    val timeout: Long?,
) : Serializable
