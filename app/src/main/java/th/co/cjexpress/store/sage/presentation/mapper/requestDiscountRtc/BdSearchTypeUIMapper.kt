package th.co.cjexpress.store.sage.presentation.mapper.requestDiscountRtc

import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeItemModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdSearchTypeUIModel

class BdSearchTypeUIMapper {
    fun toUIModel(data: List<BdSearchTypeItemModel>): List<BdSearchTypeUIModel> =
        data.map { item ->
            BdSearchTypeUIModel(
                code = item.code,
                name = item.name,
                name2 = item.name2,
            )
        }
}
