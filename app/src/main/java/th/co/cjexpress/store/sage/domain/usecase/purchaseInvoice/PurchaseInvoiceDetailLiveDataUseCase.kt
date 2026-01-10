package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceDetailLiveData
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel

class PurchaseInvoiceDetailLiveDataUseCase(
    private val purchaseInvoiceDetailLiveData: PurchaseInvoiceDetailLiveData,
) {
    val purchaseInvoiceDetail = purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel
    val purchaseInvoiceItem = purchaseInvoiceDetailLiveData.purchaseInvoiceItemUIModel
    val updatedTimestamp = purchaseInvoiceDetailLiveData.updatedTimestamp

    fun setPurchaseInvoiceDetail(data: PurchaseInvoiceDetailUIModel) {
        purchaseInvoiceDetailLiveData.setPurchaseInvoiceDetail(data)
    }

    fun resetPurchaseInvoiceDetail() {
        purchaseInvoiceDetailLiveData.resetPurchaseInvoiceDetail()
    }

    fun setPurchaseInvoiceItem(data: PurchaseInvoiceEntryUIModel) {
        purchaseInvoiceDetailLiveData.setPurchaseInvoiceItem(data)
    }

    fun resetPurchaseInvoiceItem() {
        purchaseInvoiceDetailLiveData.resetPurchaseInvoiceItem()
    }

    fun getPurchaseInvoiceOrder(barcode: String): IPurchaseInvoiceProductsModel? =
        purchaseInvoiceDetailLiveData.getPurchaseInvoiceOrder(barcode)

    fun updatePurchaseInvoiceProduct(products: List<IPurchaseInvoiceProductsModel>) {
        purchaseInvoiceDetailLiveData.updatePurchaseInvoiceProduct(products)
    }

    fun updatePurchaseInvoiceItem(item: IPurchaseInvoiceProductsModel) {
        purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItem(item)
    }

    fun updatePurchaseInvoiceItemActualQty(actualQty: Int) {
        purchaseInvoiceDetailLiveData.updatePurchaseInvoiceItemActualQty(actualQty)
    }

    fun updatePurchaseInvoiceDetailBillNo(billNo: String) {
        purchaseInvoiceDetailLiveData.updatePurchaseInvoiceDetailBillNo(billNo)
    }

    fun addPurchaseInvoiceProduct(products: PurchaseInvoiceEntryUIModel) {
        purchaseInvoiceDetailLiveData.addPurchaseInvoiceProduct(
            products = products,
        )
    }

    fun removePurchaseInvoiceProductItem(barcode: String) {
        purchaseInvoiceDetailLiveData.removePurchaseInvoiceProductItem(barcode)
    }

    fun setUpdatedTimestamp(timestamps: Long) {
        purchaseInvoiceDetailLiveData.setUpdatedTimestamp(timestamps)
    }

    fun getPurchaseInvoiceDetailBillNo(): String = purchaseInvoiceDetailLiveData.getPurchaseInvoiceDetailBillNo()

    fun getPIDetail(): PurchaseInvoiceDetailUIModel = purchaseInvoiceDetailLiveData.getPIDetail()
}
