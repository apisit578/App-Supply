package th.co.cjexpress.store.sage.data.mapper.orderReceive

import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentResponseDataItem
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDataItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel

class ShipmentDocumentMapper {
    fun map(apiResponse: ShipmentDocumentResponse): ShipmentDocumentModel =
        ShipmentDocumentModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data =
                ShipmentDataModel(
                    items =
                        apiResponse.data
                            ?.items
                            ?.map { it.toSdToteDetailItem() }
                            ?.toMutableList()
                            ?: mutableListOf(),
                    sdNo = apiResponse.data?.sdNo ?: "",
                    ldNo = apiResponse.data?.ldNo ?: "",
                    createdDate = apiResponse.data?.createdDate ?: "",
                    count = apiResponse.data?.count ?: 0,
                    sumOfActualQty = apiResponse.data?.sumOfActualQty ?: 0,
                ),
        )

    private fun ShipmentResponseDataItem.toSdToteDetailItem(): ShipmentDataItemModel =
        ShipmentDataItemModel(
            actualQty = this.actualQty ?: 0,
            barcode = this.barcode ?: "",
            productName = this.productName ?: "",
            qty = this.qty ?: 0,
            unitName = this.unitName ?: "",
            isTote = this.isTote ?: false,
            toteCode = this.toteCode ?: "",
            unitFactor = this.unitFactor ?: 0,
        )
}
