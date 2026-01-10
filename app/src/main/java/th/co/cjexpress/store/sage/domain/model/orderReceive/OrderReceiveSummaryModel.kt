package th.co.cjexpress.store.sage.domain.model.orderReceive

data class OrderReceiveSummaryModel(
    val code: String,
    val data: OrderReceiveSummaryDataModel,
    val message: String,
    val ref: String,
)

data class OrderReceiveSummaryDataModel(
    val createdDate: String,
    val items: MutableList<OrderReceiveSummaryItemModel>,
    val sdNo: String,
)

data class OrderReceiveSummaryItemModel(
    val actualQty: Int,
    val barcode: String,
    val diffQty: Int,
    val isTote: Boolean,
    val productName: String,
    val qty: Int,
    val toteCode: String,
    val unitName: String,
    val unitFactor: Int,
)
