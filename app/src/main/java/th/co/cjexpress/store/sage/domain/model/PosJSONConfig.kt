package th.co.cjexpress.store.sage.domain.model

import java.io.Serializable

data class PosJSONConfig(
    val url: String,
    val globalPath: String,
    val configMaxRetry: Int,
    val configRetryDelay: Long,
    val configTimeout: Long,
) : Serializable
