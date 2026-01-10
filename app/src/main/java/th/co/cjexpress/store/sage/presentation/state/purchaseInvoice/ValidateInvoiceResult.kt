package th.co.cjexpress.store.sage.presentation.state.purchaseInvoice

data class ValidateInvoiceResult(
    val isValid: Boolean,
    val billNo: String,
)
