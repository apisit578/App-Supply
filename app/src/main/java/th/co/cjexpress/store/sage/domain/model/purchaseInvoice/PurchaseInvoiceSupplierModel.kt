package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceSupplierModel(
    val code: String,
    val data: PurchaseInvoiceSupplierDetailModel,
    val message: String,
    val ref: String,
    val timestamp: String,
)
