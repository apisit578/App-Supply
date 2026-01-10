package th.co.cjexpress.store.sage.domain.model.orderReceiveTote

import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel

data class SdToteSubmitMappingModel(
    val code: String,
    val message: String,
    val data: SdToteSubmitModel?,
)

data class SdToteSubmitModel(
    val sdNo: String,
    val expList: List<ISdToteExpiryDateModel>? = null,
)
