package th.co.cjexpress.store.sage.presentation.state.purchaseInvoice

import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceItemPIUIModel

sealed class PurchaseInvoicePIDisplayScreenState {
    data class PurchaseInvoicePIListItemScreen(
        val items: List<PurchaseInvoiceItemPIUIModel>,
    ) : PurchaseInvoicePIDisplayScreenState()

    data object PurchaseInvoicePIEmptyScreen : PurchaseInvoicePIDisplayScreenState()

    data object PurchaseInvoiceIsNavigate : PurchaseInvoicePIDisplayScreenState()
}
