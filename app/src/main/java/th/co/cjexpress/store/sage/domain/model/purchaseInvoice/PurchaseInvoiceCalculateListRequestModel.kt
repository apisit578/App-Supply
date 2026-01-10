package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceCalculateListRequestModel(
    val items: List<PurchaseInvoiceCalculateItemRequestModel>,
    val piNo: String,
    val poNo: String,
    val supplierCode: String,
)

data class PurchaseInvoiceCalculateItemRequestModel(
    val actualQty: Int,
    val barcode: String,
    val qty: Int,
)
