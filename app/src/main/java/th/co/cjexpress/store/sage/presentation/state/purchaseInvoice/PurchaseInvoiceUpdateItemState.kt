package th.co.cjexpress.store.sage.presentation.state.purchaseInvoice

import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftDataModel

data class PurchaseInvoiceUpdateItemState(
    val data: PurchaseInvoiceDraftDataModel? = null,
)
