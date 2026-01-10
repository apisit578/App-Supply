package th.co.cjexpress.store.sage.presentation.model.purchaseInvoice

data class PurchaseInvoiceSupplierListUIModel(
    var supplierList: List<PurchaseInvoiceSupplierListItemUIModel>,
)

data class PurchaseInvoiceSupplierListItemUIModel(
    var supplierCode: String,
    var supplierName: String,
)
