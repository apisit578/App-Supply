package th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc

import th.co.cjexpress.store.sage.constants.RequestDiscountType

data class BdDocumentUIModel(
    val documentListSize: Int,
    val data: List<BdDocumentItemUIModel>,
)

data class BdDocumentItemUIModel(
    val createdDate: String,
    val discountType: RequestDiscountType,
    val documentNumber: String,
)
