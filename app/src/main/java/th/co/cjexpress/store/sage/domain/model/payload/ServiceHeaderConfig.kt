package th.co.cjexpress.store.sage.domain.model.payload

import th.co.cjexpress.store.sage.constants.RestfulAuthType
import java.io.Serializable

data class ServiceHeaderConfig(
    val name: String,
    var url: String,
    val timeout: Int? = 15000,
    val retry: Int? = 0,
    val delay: Int? = 0,
    val authType: RestfulAuthType,
    val basic: ServiceAuthBasicConfig?,
    val oauth: String?,
) : Serializable
