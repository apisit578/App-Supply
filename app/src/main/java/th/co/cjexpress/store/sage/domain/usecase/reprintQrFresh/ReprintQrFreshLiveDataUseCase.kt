package th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh

import th.co.cjexpress.store.sage.domain.livedata.reprintQrFresh.ReprintQrFreshLiveData
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel

class ReprintQrFreshLiveDataUseCase(
    private val reprintQrFreshLiveData: ReprintQrFreshLiveData,
) {
    val productQrFreshDetail = reprintQrFreshLiveData.reprintQrFreshLiveUIModel
    val productQrFreshItem = reprintQrFreshLiveData.reprintQrFreshItemUIModel

    fun getProductQrFreshItem(barcode: String): ReprintQrFreshItemUIModel? = reprintQrFreshLiveData.getProductItem(barcode)

    fun addQrFreshProduct(data: ReprintQrFreshItemUIModel) {
        reprintQrFreshLiveData.addQrFreshProduct(data)
    }

    fun updateQrPrintCount(
        barcode: String,
        expireDate: String,
        newCount: Int,
    ) {
        reprintQrFreshLiveData.updateQrPrintCountByBarcode(barcode, expireDate, newCount)
    }

    fun removeProductQrFreshItem(barcode: String) {
        reprintQrFreshLiveData.removeProductItem(barcode)
    }

    fun setQrFreshItem(data: ReprintQrFreshItemUIModel) {
        reprintQrFreshLiveData.setQrFreshItem(data)
    }

    fun resetQrFreshItem() {
        reprintQrFreshLiveData.resetQrFreshItem()
    }

    fun updateQrFreshItemPrintCount(printCount: Int) {
        reprintQrFreshLiveData.updateQrFreshItemPrintCount(printCount)
    }

    fun resetQrFreshItemList() {
        reprintQrFreshLiveData.resetQrFreshItemList()
    }
}
