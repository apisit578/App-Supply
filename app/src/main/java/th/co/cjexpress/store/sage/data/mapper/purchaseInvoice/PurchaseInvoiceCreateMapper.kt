package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateEntryDTO
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateEntryModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel

class PurchaseInvoiceCreateMapper {
    fun map(apiResponse: PurchaseInvoiceCreateDTOResponse): PurchaseInvoiceCreateModel =
        PurchaseInvoiceCreateModel(
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            billNo = apiResponse.data.billNo,
            createdDate = apiResponse.data.createdDate,
            docDate = apiResponse.data.docDate,
            dueDate = apiResponse.data.dueDate,
            entries = apiResponse.data.entries.map { it.toPurchaseInvoiceCreateEntryModel() },
            piNo = apiResponse.data.piNo,
            piStatus = apiResponse.data.piStatus,
            poNo = apiResponse.data.poNo,
            roundAmount = apiResponse.data.roundAmount,
            shipmentDate = apiResponse.data.shipmentDate,
            supplierCode = apiResponse.data.supplierCode,
            supplierName = apiResponse.data.supplierName,
            totalAmount = apiResponse.data.totalAmount,
            vat = apiResponse.data.vat,
            vatRate = apiResponse.data.vatRate,
            timestamp = apiResponse.timestamp,
        )

    private fun PurchaseInvoiceCreateEntryDTO.toPurchaseInvoiceCreateEntryModel(): PurchaseInvoiceCreateEntryModel =
        PurchaseInvoiceCreateEntryModel(
            actualQty = actualQty,
            barcode = barcode,
            isCalVat = isCalVat == 1,
            productName = productName,
            qty = qty,
            salePrice = salePrice,
            setPrice = setPrice,
            sumPrice = sumPrice,
            unitFactor = unitFactor,
            unitName = unitName,
            hhTimestamp = hhTimestamp,
        )
}
