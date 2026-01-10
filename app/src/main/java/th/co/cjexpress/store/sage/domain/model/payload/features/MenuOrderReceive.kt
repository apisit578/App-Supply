package th.co.cjexpress.store.sage.domain.model.payload.features

import java.io.Serializable

data class MenuOrderReceive(
    val enable: Boolean? = null,
    val allowExtraItems: Boolean? = null,
    val allowOverReceivingQty: Boolean? = null,
    val maxBatchQty: Int? = null,
    val enableAnalyticsTimeTracking: Boolean? = null,
) : Serializable
