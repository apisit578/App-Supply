package th.co.cjexpress.store.sage.presentation.model.purchaseInvoice

data class PurchaseBarcodeFindUIModel(
    val barFactor: Int,
    val barcode: String,
    val isAllowBuy: Boolean,
    val isFreshLife: Boolean,
    val productName: String,
    val unitCode: String,
    val unitName: String,
)
