package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

import java.util.Date

data class PurchaseInvoiceDocumentItemModel(
    val piNo: String,
    val poNo: String,
    val billNo: String,
    val createdDate: Date,
    val docDate: Date,
    val dueDate: Date,
    val shipmentDate: Date
)
