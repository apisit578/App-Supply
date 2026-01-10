package th.co.cjexpress.store.sage.domain.model.orderReceive

data class ShipmentDocumentByBarcodeModel(
    val code: String,
    val data: ShipmentByBarcodeData,
    val message: String,
    val ref: String,
    val timestamp: String,
)

data class ShipmentByBarcodeData(
    val count: Int,
    val items: List<ShipmentByBarcodeDataItemModel>,
    val sdNo: String,
    val sumOfActualQty: Int,
)

data class ShipmentByBarcodeDataItemModel(
    val actualQty: Int,
    val barcode: String,
    val isTote: Boolean,
    val productName: String,
    val qty: Int,
    val toteCode: String,
    val unitFactor: Int,
    val unitName: String,
)
