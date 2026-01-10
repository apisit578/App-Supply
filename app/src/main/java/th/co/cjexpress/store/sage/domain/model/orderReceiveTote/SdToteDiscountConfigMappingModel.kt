package th.co.cjexpress.store.sage.domain.model.orderReceiveTote

data class SdToteDiscountsConfigMappingModel(
    val code: String,
    val message: String,
    val data: List<SdToteDiscountsConfigModel>?,
)

data class SdToteDiscountsConfigModel(
    val skuCode: String,
    val barcode: String,
    val skuName: String,
    val unitName: String,
    val discountType: String,
    val noOfExp: Int,
)
