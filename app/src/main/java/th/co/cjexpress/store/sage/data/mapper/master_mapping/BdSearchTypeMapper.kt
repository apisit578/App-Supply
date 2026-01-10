package th.co.cjexpress.store.sage.data.mapper.master_mapping

import th.co.cjexpress.store.sage.data.model.masterMapping.BdSearchTypeDTOResponse
import th.co.cjexpress.store.sage.data.model.masterMapping.BdSearchTypeDataResponse
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeItemModel
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeMappingModel

class BdSearchTypeMapper {
    fun map(apiResponse: BdSearchTypeDTOResponse): BdSearchTypeMappingModel =
        BdSearchTypeMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
            ref = apiResponse.ref,
            types = apiResponse.data?.map { it.toCampaignDiscountTypeItemModel() } ?: emptyList(),
        )

    private fun BdSearchTypeDataResponse.toCampaignDiscountTypeItemModel(): BdSearchTypeItemModel =
        BdSearchTypeItemModel(
            code = this.code,
            name = this.name,
            name2 = this.name2,
        )
}
