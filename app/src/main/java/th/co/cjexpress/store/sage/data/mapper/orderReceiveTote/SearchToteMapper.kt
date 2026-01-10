package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SearchSdToteResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SearchSdToteResponseDataItem
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SearchSdToteResponseDataItemExp
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailData
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItem
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItemExpiryDateLot
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import java.util.Date

class SearchToteMapper {
    fun map(apiResponse: SearchSdToteResponse): SdToteDetailMappingModel =
        SdToteDetailMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                SdToteDetailData(
                    items =
                        apiResponse.data
                            ?.items
                            ?.map { it.toSdToteDetailItem() }
                            ?.toMutableList() ?: mutableListOf(),
                    sdNo = apiResponse.data?.sdNo ?: "",
                    toteCode = apiResponse.data?.toteCode ?: "",
                    createdDate = apiResponse.data?.createdDate ?: Date(),
                    receivedDate = apiResponse.data?.receivedDate ?: Date(),
                ),
        )

    private fun SearchSdToteResponseDataItem.toSdToteDetailItem(): SdToteDetailItem =
        SdToteDetailItem(
            actualQty = this.actualQty,
            barcode = this.barcode,
            barFactor = this.barFactor,
            discountType = this.discountType,
            noOfExp = this.noOfExp,
            productName = this.productName,
            qty = this.qty,
            unitName = this.unitName,
            skuCode = this.skuCode ?: "",
            expList = this.expList.map { it.toSdToteDetailItemExp() }.toMutableList(),
        )

    private fun SearchSdToteResponseDataItemExp.toSdToteDetailItemExp(): SdToteDetailItemExpiryDateLot =
        SdToteDetailItemExpiryDateLot(
            expDate = this.expDate,
            actualQty = this.actualQty ?: 0,
            qty = this.qty ?: 0,
            lotNo = this.lotNo.orEmpty(),
        )
}
