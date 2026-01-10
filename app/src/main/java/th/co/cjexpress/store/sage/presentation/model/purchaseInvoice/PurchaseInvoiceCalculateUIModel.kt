package th.co.cjexpress.store.sage.presentation.model.purchaseInvoice

data class PurchaseInvoiceCalculateUIModel(
//    var billNo: String,
    var itemCount: Int,
    var orderedTotalAmount: Double,
    var piNo: String,
    val poNo: String,
    var receivedItemCount: Int,
    var receivedTotalAmount: Double,
    var supplierCode: String,
    var totalOrderedQty: Int,
    var totalReceivedQty: Int,
)
