package th.co.cjexpress.store.sage.presentation.model.purchaseInvoice

import th.co.cjexpress.store.sage.presentation.state.purchaseInvoice.PurchaseInvoicePIDisplayScreenState

data class PurchaseInvoicePIUIModel(
    val itemsSize: Int = 0,
    val displayScreenState: PurchaseInvoicePIDisplayScreenState = PurchaseInvoicePIDisplayScreenState.PurchaseInvoicePIEmptyScreen,
)

data class PurchaseInvoiceItemPIUIModel(
    val piNo: String = "",
    val poNo: String = "",
    val createdDate: String = "",
)
