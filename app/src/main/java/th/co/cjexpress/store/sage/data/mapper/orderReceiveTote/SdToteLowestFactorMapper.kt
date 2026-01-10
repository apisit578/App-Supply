package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteLowestFactorResponse
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteLowestFactorMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteLowestFactorModel

class SdToteLowestFactorMapper {
    fun map(apiResponse: SdToteLowestFactorResponse): SdToteLowestFactorMappingModel =
        SdToteLowestFactorMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                SdToteLowestFactorModel(
                    barcode = apiResponse.data?.barcode ?: "",
                ),
        )
}
