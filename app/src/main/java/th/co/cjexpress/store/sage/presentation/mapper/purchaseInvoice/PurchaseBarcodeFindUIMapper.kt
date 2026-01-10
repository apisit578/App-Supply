package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindItemModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel

class PurchaseBarcodeFindUIMapper {
    fun mapPurchaseInvoiceCreateModelToUIModel(
        data: PurchaseBarcodeFindItemModel,
        actualQty: Int,
    ): PurchaseInvoiceEntryUIModel =
        PurchaseInvoiceEntryUIModel(
            barcode = data.barcode,
            actualQty = actualQty,
            productName = data.productName,
            unitName = data.unitName,
            unitFactor = data.barFactor,
            qty = 0,
            salePrice = 0.0,
        )
}
