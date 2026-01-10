package th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDiscountsConfigUIModel

class SdToteDiscountsConfigUIMapper {
    fun toUIModel(data: SdToteDiscountsConfigModel): SdToteDiscountsConfigUIModel =
        SdToteDiscountsConfigUIModel(
            barcode = data.barcode,
            discountType = data.discountType,
            noOfExp = data.noOfExp,
            skuName = data.skuName,
            unitName = data.unitName,
            skuCode = data.skuCode,
        )
}
