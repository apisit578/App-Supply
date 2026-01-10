package th.co.cjexpress.store.sage.presentation.model.orderReceive

import androidx.compose.runtime.snapshots.SnapshotStateList

data class ShipmentDocumentUIModel(
    val items: SnapshotStateList<ShipmentDataItemUIModel>,
    val createdDate: String,
    val sdNo: String,
    val ldNo: String,
    val count: Int,
    var sumOfActualQty: Int,
)

data class ShipmentDataItemUIModel(
    val actualQty: Int,
    val barcode: String,
    val isTote: Boolean,
    val toteCode: String,
    val productName: String,
    val qty: Int,
    val unitName: String,
    val unitFactor: Int,
    var overQty: Int = 0,
)
