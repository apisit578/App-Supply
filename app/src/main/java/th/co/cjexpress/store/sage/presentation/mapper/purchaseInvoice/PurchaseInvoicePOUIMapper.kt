package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentItemModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierPOUIModel
import th.co.cjexpress.store.sage.utils.CurrencyUtils

class PurchaseInvoicePOUIMapper {
    fun mapPOUIModel(purchaseOrderDocumentItemModel: List<PurchaseOrderDocumentItemModel>): List<PurchaseInvoiceSupplierPOUIModel> {
        val purchaseInvoiceSupplierPOUIModel =
            purchaseOrderDocumentItemModel.map {
                PurchaseInvoiceSupplierPOUIModel(
                    docNo = it.docNo,
                    shipmentDate = it.shipmentDate,
                    grandTotalAmountText = CurrencyUtils.formatCurrency(number = it.grandTotalAmount),
                )
            }

        return purchaseInvoiceSupplierPOUIModel
    }
}
