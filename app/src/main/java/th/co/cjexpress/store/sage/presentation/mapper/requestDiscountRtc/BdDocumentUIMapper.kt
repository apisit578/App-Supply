package th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc

import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentItemUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentUIModel

class BdDocumentUIMapper {
    fun toUIModel(data: BdDocumentMappingModel): BdDocumentUIModel =
        BdDocumentUIModel(
            documentListSize = data.data.size,
            data =
                data.data.map { item ->
                    BdDocumentItemUIModel(
                        createdDate = item.createdDate,
                        discountType = item.discountType,
                        documentNumber = item.documentNumber,
                    )
                },
        )
}
