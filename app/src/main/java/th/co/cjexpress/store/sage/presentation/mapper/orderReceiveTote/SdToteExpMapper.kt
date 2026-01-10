package th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItemExpiryDateLot
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel

class SdToteExpMapper {
    fun map(uiModel: ISdToteExpiryDateModel): SdToteDetailItemExpiryDateLot =
        SdToteDetailItemExpiryDateLot(
            expDate = uiModel.expDate,
            // uiModel.mfgDate,
            actualQty = uiModel.actualQty,
            qty = uiModel.qty,
            lotNo = uiModel.lotNo,
        )

//    fun toItemProductSdToteExpUIModel(dataModel: SdToteDetailItemExpiryDateLot): SdToteDocsDetailBarcodeExpiryUIModel{
//        return SdToteDocsDetailBarcodeExpiryUIModel(
//            expDate = dataModel.expDate,
//            qty = dataModel.qty
//        )
//    }
}
