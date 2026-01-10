package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentItemDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentListDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel

class PurchaseOrderDocumentMapper {
    fun map(apiResponse: PurchaseOrderDocumentListDTOResponse): PurchaseOrderDocumentListModel =
        PurchaseOrderDocumentListModel(
            code = apiResponse.code,
            ref = apiResponse.ref,
            message = apiResponse.message,
            timestamp = apiResponse.timestamp,
            data = apiResponse.data.map { it.toPurchaseOrderDocumentItemModel() },
        )

    private fun PurchaseOrderDocumentItemDTOResponse.toPurchaseOrderDocumentItemModel(): PurchaseOrderDocumentItemModel =
        PurchaseOrderDocumentItemModel(
            docNo = docNo,
            shipmentDate = shipmentDate,
            grandTotalAmount = grandTotalAmount,
        )
}
