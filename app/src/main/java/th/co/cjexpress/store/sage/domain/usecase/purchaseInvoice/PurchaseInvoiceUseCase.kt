package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceLiveData
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel

class PurchaseInvoiceUseCase(
    private val purchaseInvoiceLiveData: PurchaseInvoiceLiveData,
) {
    val purchaseInvoiceSupplierLiveData = purchaseInvoiceLiveData.purchaseInvoiceSupplierLiveData

    fun setPurchaseInvoiceSupplier(data: PurchaseInvoiceSupplierUIModel) {
        purchaseInvoiceLiveData.setPurchaseInvoiceSupplier(data)
    }

    fun resetPurchaseInvoiceSupplier() {
        purchaseInvoiceLiveData.resetPurchaseInvoiceSupplier()
    }

    fun getInvoiceSupplierCode(): String = purchaseInvoiceLiveData.getInvoiceSupplierCode()

    fun getPurchaseInvoiceSupplier(): PurchaseInvoiceSupplierDetailUIModel = purchaseInvoiceLiveData.getPurchaseInvoiceSupplier()

    fun isRefPo(): Boolean = purchaseInvoiceLiveData.getPurchaseInvoiceSupplier().isRefPo
}
