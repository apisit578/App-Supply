package th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.toMutableStateList
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel

class BdDocumentNoDetailUIMapper {
    fun toUIModel(data: BdDocumentNoDetailMappingModel): BdDocumentNoDetailUIModel =
        BdDocumentNoDetailUIModel(
            documentNumber = data.data?.documentNumber ?: "",
            products =
                data.data
                    ?.products
                    ?.map { item ->
                        BdDetailProductUIModel(
                            barcode = item.barcode,
                            productName = item.productName,
                            expiredDate = item.expiredDate,
                            numberOfDiscounted = item.numberOfDiscounted,
                            barFactor = item.barFactor,
                            unitFactor = item.unitFactor,
                        )
                    }?.toMutableStateList()
                    ?: mutableStateListOf(),
            createdDate = data.data?.createdDate ?: "",
            discountType = data.data?.discountType ?: RequestDiscountType.NORMAL,
        )
}
