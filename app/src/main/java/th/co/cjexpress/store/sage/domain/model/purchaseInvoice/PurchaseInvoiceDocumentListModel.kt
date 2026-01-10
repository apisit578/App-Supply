package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceDocumentListModel(
    val code: String,
    val ref: String,
    val message: String,
    val data: List<PurchaseInvoiceDocumentItemModel>,
    val timestamp: String,
)
