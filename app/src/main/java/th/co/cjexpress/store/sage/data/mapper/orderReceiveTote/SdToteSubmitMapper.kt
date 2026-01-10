package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitResponseDataItemExp
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItemExpiryDateLot
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitModel

class SdToteSubmitMapper {
    fun map(apiResponse: SdToteSubmitResponse): SdToteSubmitMappingModel =
        SdToteSubmitMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                SdToteSubmitModel(
                    sdNo = apiResponse.data?.sdNo ?: "",
                    expList =
                        apiResponse.data
                            ?.expList
                            ?.map { it.toSdToteDetailItemExp() }
                            ?.toMutableList(),
                ),
        )

    private fun SdToteSubmitResponseDataItemExp.toSdToteDetailItemExp(): SdToteDetailItemExpiryDateLot =
        SdToteDetailItemExpiryDateLot(
            expDate = this.expDate,
            actualQty = this.actualQty,
            qty = this.qty,
            lotNo = this.lotNo,
        )
}
