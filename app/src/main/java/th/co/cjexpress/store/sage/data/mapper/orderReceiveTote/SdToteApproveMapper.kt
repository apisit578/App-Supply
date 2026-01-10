package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteApproveResponse
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteApproveMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteApproveModel

class SdToteApproveMapper {
    fun map(apiResponse: SdToteApproveResponse): SdToteApproveMappingModel =
        SdToteApproveMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                SdToteApproveModel(
                    sdNo = apiResponse.data?.sdNo ?: "",
                ),
        )
}
