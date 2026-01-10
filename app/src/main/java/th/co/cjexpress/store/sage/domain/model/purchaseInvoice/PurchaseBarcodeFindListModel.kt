package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseBarcodeFindListModel(
    val code: String,
    val data: List<PurchaseBarcodeFindItemModel>,
    val message: String,
    val ref: String,
    val timestamp: String,
)

data class PurchaseBarcodeFindItemModel(
    val barFactor: Int,
    val barcode: String,
    val isAllowBuy: Boolean,
    val isFreshLife: Boolean,
    val productName: String,
    val unitCode: String,
    val unitName: String,
)
