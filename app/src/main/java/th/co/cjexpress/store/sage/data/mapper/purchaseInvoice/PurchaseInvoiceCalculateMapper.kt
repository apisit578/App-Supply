package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDataDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateModel

class PurchaseInvoiceCalculateMapper {
    fun map(apiResponse: PurchaseInvoiceCalculateDTOResponse): PurchaseInvoiceCalculateModel =
        PurchaseInvoiceCalculateModel(
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            data = apiResponse.data.toPurchaseInvoiceCalculateItemModel(),
            timestamp = apiResponse.timestamp,
        )

    private fun PurchaseInvoiceCalculateDataDTOResponse.toPurchaseInvoiceCalculateItemModel() =
        PurchaseInvoiceCalculateDataModel(
            piNo = piNo,
            poNo = poNo,
            supplierCode = supplierCode,
            itemCount = itemCount,
            receivedItemCount = receivedItemCount,
            orderedTotalAmount = orderedTotalAmount,
            receivedTotalAmount = receivedTotalAmount,
            totalReceivedQty = totalReceivedQty,
            totalOrderedQty = totalOrderedQty,
        )
}
