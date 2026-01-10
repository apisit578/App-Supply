package th.co.cjexpress.store.sage.presentation.model.purchaseInvoice

import androidx.compose.runtime.snapshots.SnapshotStateList
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel

data class PurchaseInvoiceDetailUIModel(
    val supplierCode: String,
    val supplierName: String,
    val poNo: String,
    val piNo: String,
    var billNo: String,
    val entries: SnapshotStateList<PurchaseInvoiceEntryUIModel>,
    val isEdit: Boolean = false,
)

data class PurchaseInvoiceEntryUIModel(
    override val barcode: String,
    override var actualQty: Int,
    val productName: String,
    val unitName: String,
    val unitFactor: Int,
    val qty: Int,
    val salePrice: Double,
) : IPurchaseInvoiceProductsModel
