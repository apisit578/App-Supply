package th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc

import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentSubmitDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentSubmitDataResponse
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentSubmitModel

class BdDocumentSubmitMapper {
    fun map(apiResponse: BdDocumentSubmitDTOResponse): BdDocumentSubmitMappingModel =
        BdDocumentSubmitMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
            ref = apiResponse.ref,
            data = apiResponse.data?.toUpsertBd(),
        )

    private fun BdDocumentSubmitDataResponse.toUpsertBd(): BdDocumentSubmitModel =
        BdDocumentSubmitModel(
            documentNumber = this.documentNumber,
        )
}
