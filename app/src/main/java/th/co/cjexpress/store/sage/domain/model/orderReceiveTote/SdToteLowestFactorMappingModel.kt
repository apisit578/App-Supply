package th.co.cjexpress.store.sage.domain.model.orderReceiveTote

data class SdToteLowestFactorMappingModel(
    val code: String,
    val message: String,
    val data: SdToteLowestFactorModel?,
)

data class SdToteLowestFactorModel(
    val barcode: String,
)
