package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentItemDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentListDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel

class PurchaseInvoiceDocumentMapper {
    fun map(apiResponse: PurchaseInvoiceDocumentListDTOResponse): PurchaseInvoiceDocumentListModel =
        PurchaseInvoiceDocumentListModel(
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            data = apiResponse.data.map { it.toPurchaseInvoiceDocumentItemModel() },
            timestamp = apiResponse.timestamp,
        )

    private fun PurchaseInvoiceDocumentItemDTOResponse.toPurchaseInvoiceDocumentItemModel() =
        PurchaseInvoiceDocumentItemModel(
            piNo = piNo,
            poNo = poNo,
            billNo = billNo,
            createdDate = createdDate,
            docDate = docDate,
            dueDate = dueDate,
            shipmentDate = shipmentDate,
        )
}
