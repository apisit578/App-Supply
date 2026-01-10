package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteDiscountsConfigResponse
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel

class SdToteDiscountsConfigMapper {
    fun map(apiResponse: SdToteDiscountsConfigResponse): SdToteDiscountsConfigMappingModel =
        SdToteDiscountsConfigMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                apiResponse.data.map {
                    SdToteDiscountsConfigModel(
                        skuCode = it.skuCode,
                        barcode = it.barcode,
                        discountType = it.discountType,
                        noOfExp = it.noOfExp,
                        skuName = it.skuName,
                        unitName = it.unitName,
                    )
                },
        )
}
