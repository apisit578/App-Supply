package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceSupplierListModel(
    val code: String,
    val data: List<PurchaseInvoiceSupplierDetailModel>,
    val message: String,
    val ref: String,
    val timestamp: String,
)
