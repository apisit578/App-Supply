package th.co.cjexpress.store.sage.domain.model.orderReceiveTote

data class SdToteApproveMappingModel(
    val code: String,
    val message: String,
    val data: SdToteApproveModel?,
)

data class SdToteApproveModel(
    val sdNo: String,
)
