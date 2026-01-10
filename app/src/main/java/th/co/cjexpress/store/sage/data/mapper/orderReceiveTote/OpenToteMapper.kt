package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import th.co.cjexpress.store.sage.data.model.orderReceiveTote.OpenToteResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.OpenToteResponseDataItem
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.OpenToteResponseDataItemExp
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailData
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItem
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItemExpiryDateLot
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import java.util.Date

class OpenToteMapper {
    fun map(apiResponse: OpenToteResponse): SdToteDetailMappingModel =
        SdToteDetailMappingModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                SdToteDetailData(
                    items =
                        apiResponse.data
                            ?.items
                            ?.map { it.toSdToteDetailItem() }
                            ?.toMutableList()
                            ?: mutableListOf(),
                    sdNo = apiResponse.data?.sdNo ?: "",
                    toteCode = apiResponse.data?.toteCode ?: "",
                    createdDate = apiResponse.data?.createdDate ?: Date(),
                    receivedDate = apiResponse.data?.receivedDate ?: Date(),
                ),
        )

    private fun OpenToteResponseDataItem.toSdToteDetailItem(): SdToteDetailItem =
        SdToteDetailItem(
            actualQty = this.actualQty.toInt(),
            barcode = this.barcode,
            barFactor = this.barFactor.toInt(),
            discountType = this.discountType,
            noOfExp = this.noOfExp,
            productName = this.productName,
            qty = this.qty.toInt(),
            unitName = this.unitName,
            skuCode = this.skuCode ?: "",
            expList = this.expList.map { it.toSdToteDetailItemExp() }.toMutableList(),
        )

    private fun OpenToteResponseDataItemExp.toSdToteDetailItemExp(): SdToteDetailItemExpiryDateLot =
        SdToteDetailItemExpiryDateLot(
            expDate = this.expDate,
            actualQty = this.actualQty,
            qty = this.qty,
            lotNo = this.lotNo,
        )
}
