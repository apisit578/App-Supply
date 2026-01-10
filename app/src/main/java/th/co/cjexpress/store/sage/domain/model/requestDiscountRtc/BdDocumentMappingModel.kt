package th.co.cjexpress.store.sage.domain.model.requestDiscountRtc

import th.co.cjexpress.store.sage.constants.RequestDiscountType

data class BdDocumentMappingModel(
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String,
    val data: List<BdDocumentItemMappingModel>,
)

data class BdDocumentItemMappingModel(
    val createdDate: String,
    val discountType: RequestDiscountType,
    val documentNumber: String,
)
