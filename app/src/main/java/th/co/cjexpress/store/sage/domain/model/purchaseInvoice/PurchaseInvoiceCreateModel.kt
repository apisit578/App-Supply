package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

import java.util.Date

data class PurchaseInvoiceCreateModel(
    val code: String,
    val ref: String,
    val message: String,
    val billNo: String,
    val createdDate: Date,
    val docDate: Date,
    val dueDate: Date,
    val entries: List<PurchaseInvoiceCreateEntryModel>,
    val piNo: String,
    val piStatus: String,
    val poNo: String,
    val roundAmount: Double,
    val shipmentDate: Date,
    val supplierCode: String,
    val supplierName: String,
    val totalAmount: Double,
    val vat: Double,
    val vatRate: Double,
    val timestamp: String
)

data class PurchaseInvoiceCreateEntryModel(
    val actualQty: Int,
    val barcode: String,
    val isCalVat: Boolean,
    val productName: String,
    val qty: Int,
    val salePrice: Double,
    val setPrice: Double,
    val sumPrice: Double,
    val unitFactor: Int,
    val unitName: String,
    val hhTimestamp: Long
)
