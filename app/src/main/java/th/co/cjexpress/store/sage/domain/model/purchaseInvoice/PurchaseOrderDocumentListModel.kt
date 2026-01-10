package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseOrderDocumentListModel(
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String,
    val data: List<PurchaseOrderDocumentItemModel>
)
