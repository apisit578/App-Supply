package th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc

import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentDataResponse
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentItemMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat

class BdDocumentMapper {
    fun map(apiResponse: BdDocumentDTOResponse): BdDocumentMappingModel =
        BdDocumentMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
            ref = apiResponse.ref,
            data = apiResponse.data?.map { it.toBdListModel() } ?: emptyList(),
        )

    private fun BdDocumentDataResponse.toBdListModel(): BdDocumentItemMappingModel =
        BdDocumentItemMappingModel(
            documentNumber = this.documentNumber,
            discountType = this.discountType,
            createdDate = this.createdDate.toStringDDMMYYYYFormat(),
        )
}
