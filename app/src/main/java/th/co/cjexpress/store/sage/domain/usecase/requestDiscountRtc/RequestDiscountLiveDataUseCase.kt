package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc.RequestDiscountLiveData
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel

class RequestDiscountLiveDataUseCase(
    private val requestDiscountLiveData: RequestDiscountLiveData,
) {
    val docsData = requestDiscountLiveData.docsData

    fun setIsNewProduct(isNew: Boolean) {
        requestDiscountLiveData.setIsNewProduct(isNew)
    }

    fun setEditingProduct(product: BdDetailProductUIModel) {
        requestDiscountLiveData.setEditingProduct(product = product)
    }

    fun removeProductFromBdDetail(barcode: String) {
        requestDiscountLiveData.removeProductFromBdDetail(barcode = barcode)
    }
}
