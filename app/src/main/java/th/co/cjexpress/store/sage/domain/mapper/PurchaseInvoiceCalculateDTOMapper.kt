package th.co.cjexpress.store.sage.domain.mapper

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateDTORequest
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCalculateItemDTORequest
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel

class PurchaseInvoiceCalculateDTOMapper {
    fun toDTO(domainModel: PurchaseInvoiceDetailUIModel) =
        PurchaseInvoiceCalculateDTORequest(
            items =
                domainModel.entries.map {
                    PurchaseInvoiceCalculateItemDTORequest(
                        actualQty = it.actualQty,
                        barcode = it.barcode,
                        qty = it.qty,
                    )
                },
            piNo = domainModel.piNo,
            poNo = domainModel.poNo,
            supplierCode = domainModel.supplierCode,
        )

    fun toDomain(dtoModel: PurchaseInvoiceCalculateModel) =
        PurchaseInvoiceCalculateModel(
            code = dtoModel.code,
            ref = dtoModel.ref,
            message = dtoModel.message,
            data =
                PurchaseInvoiceCalculateDataModel(
                    piNo = dtoModel.data.piNo,
                    poNo = dtoModel.data.poNo,
                    supplierCode = dtoModel.data.supplierCode,
                    itemCount = dtoModel.data.itemCount,
                    receivedItemCount = dtoModel.data.receivedItemCount,
                    orderedTotalAmount = dtoModel.data.orderedTotalAmount,
                    receivedTotalAmount = dtoModel.data.receivedTotalAmount,
                    totalReceivedQty = dtoModel.data.totalReceivedQty,
                    totalOrderedQty = dtoModel.data.totalOrderedQty,
                ),
            timestamp = dtoModel.timestamp,
        )
}
