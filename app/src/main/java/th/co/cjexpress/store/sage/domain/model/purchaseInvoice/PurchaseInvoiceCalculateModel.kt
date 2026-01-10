package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceCalculateModel(
    val code: String,
    val ref: String,
    val message: String,
    val data: PurchaseInvoiceCalculateDataModel,
    val timestamp: String,
)

data class PurchaseInvoiceCalculateDataModel(
    val piNo: String,
    val poNo: String,
    val supplierCode: String,
    val itemCount: Int,
    val receivedItemCount: Int,
    val orderedTotalAmount: Double,
    val receivedTotalAmount: Double,
    val totalReceivedQty: Int,
    val totalOrderedQty: Int,
)
