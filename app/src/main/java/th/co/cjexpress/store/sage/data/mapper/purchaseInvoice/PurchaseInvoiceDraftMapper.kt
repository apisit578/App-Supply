package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDataDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftItemDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel

class PurchaseInvoiceDraftMapper {
    fun map(apiResponse: PurchaseInvoiceDraftDTOResponse): PurchaseInvoiceDraftModel =
        PurchaseInvoiceDraftModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResponse.data?.toDataModel()
                ?: PurchaseInvoiceDraftDataModel(piNo = "", items = emptyList()),
            ref = apiResponse.ref,
            timestamp = apiResponse.timestamp,
        )

    private fun PurchaseInvoiceDraftDataDTOResponse.toDataModel(): PurchaseInvoiceDraftDataModel =
        PurchaseInvoiceDraftDataModel(
            piNo = this.piNo,
            items = this.items.map { it.toItemsModel() },
        )

    private fun PurchaseInvoiceDraftItemDTOResponse.toItemsModel(): PurchaseInvoiceDraftItemModel =
        PurchaseInvoiceDraftItemModel(
            actualQty = this.actualQty,
            qty = this.qty,
            barcode = this.barcode,
            productName = this.productName,
            unitFactor = this.unitFactor,
            unitName = this.unitName,
            hhTimestamp = this.hhTimestamp
        )
}
