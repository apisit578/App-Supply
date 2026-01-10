package th.co.cjexpress.store.sage.presentation.model.orderReceive

data class ShipmentDocumentListUIModel(
    val documentListSize: String,
    val shipmentDocumentList: List<ShipmentDocumentListItemUIModel>,
)

data class ShipmentDocumentListItemUIModel(
    val date: String,
    val sdNo: String,
    val ldNo: String,
)
