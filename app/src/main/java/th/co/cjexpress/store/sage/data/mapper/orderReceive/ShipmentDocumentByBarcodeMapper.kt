package th.co.cjexpress.store.sage.data.mapper.orderReceive

import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcode
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeItem
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeResponse
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentByBarcodeData
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentByBarcodeDataItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel

class ShipmentDocumentByBarcodeMapper {
    fun map(apiResponse: ShipmentDocumentByBarcodeResponse): ShipmentDocumentByBarcodeModel =
        ShipmentDocumentByBarcodeModel(
            code = apiResponse.code,
            message = apiResponse.message,
            ref = apiResponse.ref,
            timestamp = apiResponse.timestamp,
            data = mapData(apiResponse.data),
        )

    fun mapData(data: ShipmentDocumentByBarcode): ShipmentByBarcodeData =
        ShipmentByBarcodeData(
            count = data.count,
            sdNo = data.sdNo,
            sumOfActualQty = data.sumOfActualQty,
            items = data.items.map { it.toDataItemModel() },
        )

    private fun ShipmentDocumentByBarcodeItem.toDataItemModel(): ShipmentByBarcodeDataItemModel =
        ShipmentByBarcodeDataItemModel(
            actualQty = this.actualQty,
            barcode = this.barcode,
            isTote = this.isTote,
            productName = this.productName,
            qty = this.qty,
            toteCode = this.toteCode,
            unitFactor = this.unitFactor,
            unitName = this.unitName,
        )
}
