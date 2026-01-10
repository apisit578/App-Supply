package th.co.cjexpress.store.sage.data.mapper.requestDiscountRtc

import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentValidateDTOResponse
import th.co.cjexpress.store.sage.data.model.requestDiscountRtc.BdDocumentValidateDataResponse
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateMappingModel
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateModel

class BdValidateBarcodeMapper {
    fun map(apiResponse: BdDocumentValidateDTOResponse): BdDocumentValidateMappingModel =
        BdDocumentValidateMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
            ref = apiResponse.ref,
            data = apiResponse.data?.toValidateBarcodeForBd(),
        )

    private fun BdDocumentValidateDataResponse.toValidateBarcodeForBd(): BdDocumentValidateModel =
        BdDocumentValidateModel(
            barcode = this.barcode,
            productName = this.productName,
            barFactor = this.barFactor,
            unitName = this.unitName,
        )
}
