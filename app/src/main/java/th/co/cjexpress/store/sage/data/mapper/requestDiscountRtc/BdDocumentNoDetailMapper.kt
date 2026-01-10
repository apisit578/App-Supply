package th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc

import androidx.compose.runtime.toMutableStateList
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentNoDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentNoDetailDataResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentNoDetailProductItemResponse
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailProductModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat

class BdDocumentNoDetailMapper {
    fun map(apiResponse: BdDocumentNoDetailDTOResponse): BdDocumentNoDetailMappingModel =
        BdDocumentNoDetailMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
            ref = apiResponse.ref,
            data = apiResponse.data.toBdDetailData(),
        )

    private fun BdDocumentNoDetailDataResponse.toBdDetailData(): BdDocumentNoDetailModel =
        BdDocumentNoDetailModel(
            documentNumber = this.documentNumber,
            createdDate = this.createdDate.toStringDDMMYYYYFormat(),
            discountType = this.discountType,
            products = this.products.map { it.toBdDetailProduct() }.toMutableStateList(),
        )

    private fun BdDocumentNoDetailProductItemResponse.toBdDetailProduct(): BdDocumentNoDetailProductModel =
        BdDocumentNoDetailProductModel(
            barcode = this.barcode,
            productName = this.productName,
            expiredDate = this.expiredDate,
            numberOfDiscounted = this.numberOfDiscounted,
            barFactor = this.barFactor,
            unitFactor = this.unitFactor,
        )
}
