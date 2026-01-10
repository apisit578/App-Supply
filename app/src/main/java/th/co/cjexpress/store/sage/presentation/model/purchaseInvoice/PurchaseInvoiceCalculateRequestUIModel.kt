package th.co.cjexpress.store.sage.presentation.model.purchaseInvoice

data class PurchaseInvoiceCalculateRequestUIModel(
    val items: List<PurchaseInvoiceCalculateItemRequestUIModel>,
    val piNo: String,
    val poNo: String,
    val supplierCode: String,
)

data class PurchaseInvoiceCalculateItemRequestUIModel(
    val actualQty: Int,
    val barcode: String,
    val qty: Int,
)
