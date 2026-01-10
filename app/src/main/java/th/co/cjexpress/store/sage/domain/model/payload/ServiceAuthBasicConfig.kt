package th.co.cjexpress.store.sage.domain.model.payload

import java.io.Serializable

data class ServiceAuthBasicConfig(
    val username: String,
    val password: String,
) : Serializable
