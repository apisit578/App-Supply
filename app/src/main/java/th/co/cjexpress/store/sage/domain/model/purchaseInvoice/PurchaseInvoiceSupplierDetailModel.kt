package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceSupplierDetailModel(
    val supplierCode: String,
    val supplierName: String,
    val supplierIsRefPO: Boolean,
)
