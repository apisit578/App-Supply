package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

import java.util.Date

data class PurchaseInvoiceDetailModel(
    val poNo: String,
    val docDate: Date,
    val supplierCode: String,
    val supplierName: String,
    val dueDate: Date,
    val shipmentDate: Date,
    val totalAmount: Double,
    val vat: Double,
    val vatRate: Double,
    val roundAmount: Int,
    val createdDate: Date,
    val piStatus: String,
    val piNo: String,
    val billNo: String,
    val entries: List<PurchaseInvoiceEntryModel>,
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String
)

data class PurchaseInvoiceEntryModel(
    val isCalVat: Boolean,
    val barcode: String,
    val productName: String,
    val unitName: String,
    val unitFactor: Int,
    val qty: Int,
    val salePrice: Double,
    val actualQty: Int
)