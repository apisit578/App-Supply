package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceItemModel(
    override val barcode: String,
    override var actualQty: Int,
) : IPurchaseInvoiceProductsModel
