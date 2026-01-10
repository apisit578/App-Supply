package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceEntryDTO
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceEntryModel

class PurchaseInvoiceDetailMapper {
    fun map(apiResponse: PurchaseInvoiceDetailDTOResponse): PurchaseInvoiceDetailModel =
        PurchaseInvoiceDetailModel(
            poNo = apiResponse.data.poNo,
            docDate = apiResponse.data.docDate,
            supplierCode = apiResponse.data.supplierCode,
            supplierName = apiResponse.data.supplierName,
            dueDate = apiResponse.data.dueDate,
            shipmentDate = apiResponse.data.shipmentDate,
            totalAmount = apiResponse.data.totalAmount,
            vat = apiResponse.data.vat,
            vatRate = apiResponse.data.vatRate,
            roundAmount = apiResponse.data.roundAmount,
            createdDate = apiResponse.data.createdDate,
            piStatus = apiResponse.data.piStatus,
            piNo = apiResponse.data.piNo,
            billNo = apiResponse.data.billNo,
            entries = apiResponse.data.entries.map { it.toPurchaseInvoiceEntryModel() },
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
        )

    private fun PurchaseInvoiceEntryDTO.toPurchaseInvoiceEntryModel(): PurchaseInvoiceEntryModel =
        PurchaseInvoiceEntryModel(
            isCalVat = isCalVat == 1,
            barcode = barcode,
            productName = productName,
            unitName = unitName,
            unitFactor = unitFactor,
            qty = qty,
            salePrice = salePrice,
            actualQty = actualQty,
        )
}
