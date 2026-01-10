package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceSupplierListDTOResponse
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel

class PurchaseInvoiceSupplierMapper {
    fun mapList(apiResponse: PurchaseInvoiceSupplierListDTOResponse): PurchaseInvoiceSupplierListModel =
        PurchaseInvoiceSupplierListModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResponse.data.map { it.toSupplierDataModel() },
            ref = apiResponse.ref,
            timestamp = apiResponse.timestamp,
        )

    fun map(apiResponse: PurchaseInvoiceSupplierDTOResponse): PurchaseInvoiceSupplierModel =
        PurchaseInvoiceSupplierModel(
            code = apiResponse.code,
            message = apiResponse.message,
            data = apiResponse.data.toSupplierDataModel(),
            ref = apiResponse.ref,
            timestamp = apiResponse.timestamp,
        )

    private fun PurchaseInvoiceSupplierDetailDTOResponse.toSupplierDataModel(): PurchaseInvoiceSupplierDetailModel =
        PurchaseInvoiceSupplierDetailModel(
            supplierCode = this.supplierCode,
            supplierName = this.supplierName,
            supplierIsRefPO = this.supplierIsRefPO == true, // supplierIsRefPO null return false
        )
}
