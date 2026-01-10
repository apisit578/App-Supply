package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel

class PurchaseInvoiceSupplierUIMapper {
    fun toUIModel(result: PurchaseInvoiceSupplierModel) =
        PurchaseInvoiceSupplierUIModel(
            supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = result.data.supplierCode,
                    supplierName = result.data.supplierName,
                    isRefPo = result.data.supplierIsRefPO,
                ),
        )
}
