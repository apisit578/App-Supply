package th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc

import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentValidateModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.ValidateBarcodeForBdUIModel

class BdDocumentValidateUIMapper {
    fun toUIModel(data: BdDocumentValidateModel): ValidateBarcodeForBdUIModel =
        ValidateBarcodeForBdUIModel(
            barcode = data.barcode,
            productName = data.productName,
            barFactor = data.barFactor,
            unitName = data.unitName,
        )
}
