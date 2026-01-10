package th.co.cjexpress.store.sage.domain.model.purchaseInvoice

data class PurchaseInvoiceValidateBillModel(
    val isValid: Boolean,
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String
)
