package th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc

import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentSubmitUIModel

class BdDocumentSubmitUIMapper {
    fun toUIModel(data: BdDocumentSubmitModel): BdDocumentSubmitUIModel =
        BdDocumentSubmitUIModel(
            documentNumber = data.documentNumber,
        )
}
