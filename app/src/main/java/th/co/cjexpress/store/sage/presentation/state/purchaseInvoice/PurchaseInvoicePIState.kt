package th.co.cjexpress.store.sage.presentation.state.purchaseInvoice

import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoicePIUIModel

data class PurchaseInvoicePIState(
    val data: PurchaseInvoicePIUIModel = PurchaseInvoicePIUIModel(),
    val isNavToPOListPage: Boolean = false,
)
