package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceDraftModel(
    val code: String,
    val data: PurchaseInvoiceDraftDataModel,
    val message: String,
    val ref: String,
    val timestamp: String,
)

data class PurchaseInvoiceDraftDataModel(
    val piNo: String,
    val items: List<PurchaseInvoiceDraftItemModel>,
)

data class PurchaseInvoiceDraftItemModel(
    override var actualQty: Int,
    override var barcode: String,
    var productName: String,
    var qty: Int,
    var unitFactor: Int,
    var unitName: String,
    val hhTimestamp: Long,
) : IPurchaseInvoiceProductsModel
