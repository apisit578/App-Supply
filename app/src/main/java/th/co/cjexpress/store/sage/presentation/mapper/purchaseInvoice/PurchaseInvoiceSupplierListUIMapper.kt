package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierListItemUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierListUIModel

class PurchaseInvoiceSupplierListUIMapper {
    fun toUIModel(result: PurchaseInvoiceSupplierListModel) =
        PurchaseInvoiceSupplierListUIModel(
            supplierList =
                result.data.map {
                    PurchaseInvoiceSupplierListItemUIModel(
                        supplierCode = it.supplierCode,
                        supplierName = it.supplierName,
                    )
                },
        )
}
