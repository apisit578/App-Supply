package th.co.cjexpress.store.sage.domain.model.orderReceive

data class ShipmentDocumentModel(
    val code: String,
    val message: String,
    val data: ShipmentDataModel,
)

data class ShipmentDataModel(
    val items: MutableList<ShipmentDataItemModel>,
    val createdDate: String,
    val sdNo: String,
    val ldNo: String,
    val count: Int,
    val sumOfActualQty: Int,
)

data class ShipmentDataItemModel(
    val actualQty: Int,
    val barcode: String,
    val isTote: Boolean,
    val toteCode: String,
    val productName: String,
    val qty: Int,
    val unitName: String,
    val unitFactor: Int,
)
