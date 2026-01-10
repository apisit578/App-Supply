package th.co.cjexpress.store.sage.domain.mapper

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustItemDataRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustLotDataRequest
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustRequest
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat

class SdToteSubmitAdjustDTOMapper {
    fun toRequest(model: SdToteSubmitUIModel): SdToteSubmitAdjustRequest =
        SdToteSubmitAdjustRequest(
            sdNo = model.sdNo,
            toteCode = model.toteCode,
            barcode = model.barcode,
            discountType = model.discountType,
            data = toSdToteSubmitAdjustRequest(model),
        )

    private fun toSdToteSubmitAdjustRequest(data: SdToteSubmitUIModel): List<SdToteSubmitAdjustItemDataRequest> =
        data.expList
            .groupBy { it.expDate }
            .map { (expDate, items) ->
                SdToteSubmitAdjustItemDataRequest(
                    actualQty = items.sumOf { it.actualQty },
                    beforeQty = data.beforeQty[expDate?.toStringDDMMYYYYFormat()] ?: 0,
                    expDate = expDate,
                    listLot =
                        items.map { item ->
                            SdToteSubmitAdjustLotDataRequest(
                                lotNo = item.lotNo,
                                qty = item.qty,
                                actualQty = item.actualQty,
                            )
                        },
                )
            }
}
