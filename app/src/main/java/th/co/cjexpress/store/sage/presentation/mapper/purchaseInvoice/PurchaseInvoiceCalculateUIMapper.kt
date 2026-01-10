package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateDataModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceCalculateUIModel

class PurchaseInvoiceCalculateUIMapper {
    fun mapUIModel(domainModel: PurchaseInvoiceCalculateDataModel): PurchaseInvoiceCalculateUIModel =
        PurchaseInvoiceCalculateUIModel(
            itemCount = domainModel.itemCount,
            orderedTotalAmount = domainModel.orderedTotalAmount,
            piNo = domainModel.piNo,
            poNo = domainModel.poNo,
            receivedItemCount = domainModel.receivedItemCount,
            receivedTotalAmount = domainModel.receivedTotalAmount,
            supplierCode = domainModel.supplierCode,
            totalOrderedQty = domainModel.totalOrderedQty,
            totalReceivedQty = domainModel.totalReceivedQty,
        )
}
