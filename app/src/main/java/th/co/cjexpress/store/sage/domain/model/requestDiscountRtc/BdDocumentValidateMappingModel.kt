package th.co.cjexpress.store.sage.domain.model.requestDiscountRtc

data class BdDocumentValidateMappingModel(
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String,
    val data: BdDocumentValidateModel?,
)

data class BdDocumentValidateModel(
    val barcode: String,
    val productName: String,
    val barFactor: Int,
    val unitName: String,
)
