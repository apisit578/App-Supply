package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteListResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteResponseData
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteListMappingModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat

class SdToteListMapper {
    fun map(apiResponse: SdToteListResponse): SdToteListMappingModel =
        SdToteListMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResponse.data?.map { it.toSdToteDataModel() } ?: emptyList(),
        )

    private fun SdToteResponseData.toSdToteDataModel(): SdToteDataModel =
        SdToteDataModel(
            documentNo = this.sdNo,
            documentDate = this.createdDate.toStringDDMMYYYYFormat(),
            toteCode = this.toteCode ?: "",
        )
}
