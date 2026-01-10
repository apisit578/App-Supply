package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.ExpListItemResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustResponse
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAdjustExpItem
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAdjustModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAdjustSdModel

class SdToteSubmitAdjustMapper {
    fun map(apiResponse: SdToteSubmitAdjustResponse): SdToteSubmitAdjustModel =
        SdToteSubmitAdjustModel(
            ref = apiResponse.ref,
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                apiResponse.data?.let {
                    SdToteSubmitAdjustSdModel(
                        sdNo = it.sdNo,
                        qrNo = it.qrNo,
                        expList = it.expList.map { it.toSdToteSubmitAdjustExpItem() },
                    )
                },
        )

    fun ExpListItemResponse.toSdToteSubmitAdjustExpItem() =
        SdToteSubmitAdjustExpItem(
            expDate = this.expDate,
            mfgDate = this.mfgDate,
            qty = this.qty,
            actualQty = this.actualQty,
            lotNo = this.lotNo,
        )
}
