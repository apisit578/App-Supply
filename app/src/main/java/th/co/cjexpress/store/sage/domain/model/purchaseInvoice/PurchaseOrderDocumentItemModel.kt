package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseOrderDocumentItemModel(
    val docNo: String,
    val shipmentDate: String,
    val grandTotalAmount: Double
)
