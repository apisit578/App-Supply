package th.co.cjexpress.store.sage.domain.model.requestDiscountRtc

data class BdDocumentSubmitMappingModel(
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String,
    val data: BdDocumentSubmitModel?,
)

data class BdDocumentSubmitModel(
    val documentNumber: String,
)
