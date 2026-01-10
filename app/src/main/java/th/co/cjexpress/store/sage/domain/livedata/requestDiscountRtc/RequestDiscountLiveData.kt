package th.co.cjexpress.store.sage.domain.livedata.requestDiscountRtc

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDetailProductUIModel
import th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc.BdDocumentNoDetailUIModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDD_MM_YYYYFormat
import java.util.Date
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RequestDiscountLiveData
    @Inject
    constructor() {
        private val _docsData = MutableLiveData<BdDocumentNoDetailUIModel>()
        val docsData: LiveData<BdDocumentNoDetailUIModel> get() = _docsData

        private val _editingProduct = MutableLiveData<BdDetailProductUIModel?>(null)
        val editingProduct: LiveData<BdDetailProductUIModel?> get() = _editingProduct

        private val _isNewProduct = MutableLiveData(false)
        val isNewItem: LiveData<Boolean> get() = _isNewProduct

        fun resetDocsData() {
            _docsData.value =
                BdDocumentNoDetailUIModel(
                    documentNumber = "",
                    createdDate = Date().toStringDDMMYYYYFormat(),
                    discountType = RequestDiscountType.FRESHLIFE,
                    products = SnapshotStateList(),
                )

            _editingProduct.value = null
            _isNewProduct.value = false
        }

        fun getBdProductByBarcode(barcode: String): BdDetailProductUIModel? =
            docsData.value?.products?.firstOrNull { it.barcode == barcode }

        fun getBdProductByBarcodeAndExpiryDate(
            barcode: String,
            expiryDate: Date,
        ): BdDetailProductUIModel? =
            docsData.value?.products?.firstOrNull {
                it.barcode == barcode &&
                    it.expiredDate!!.toStringDD_MM_YYYYFormat() == expiryDate.toStringDD_MM_YYYYFormat()
            }

        fun getEditingProduct(): BdDetailProductUIModel? = _editingProduct.value

        fun setDocsData(data: BdDocumentNoDetailUIModel) {
            _docsData.value = data
        }

        fun setDiscountType(discountType: RequestDiscountType) {
            _docsData.value =
                _docsData.value!!.copy(
                    discountType = discountType,
                )
        }

        fun setEditingProduct(product: BdDetailProductUIModel?) {
            _editingProduct.value = product
        }

        fun setIsNewProduct(state: Boolean) {
            _isNewProduct.value = state
        }

        fun removeProductFromBdDetail(barcode: String) {
            _docsData.value?.let { bdDetailModel ->
                val removeProduct = bdDetailModel.products.find { it.barcode == barcode }
                if (removeProduct != null) {
                    bdDetailModel.products.remove(removeProduct)
                }
            }
        }

        fun updateDocsProducts(
            isNewItem: Boolean,
            addingProduct: BdDetailProductUIModel,
        ): Boolean {
            val existProduct =
                _docsData.value!!.products.firstOrNull {
                    it.barcode == addingProduct.barcode &&
                        it.expiredDate!!.toStringDD_MM_YYYYFormat() == addingProduct.expiredDate!!.toStringDD_MM_YYYYFormat()
                }

            if (existProduct == null) {
                _docsData.value!!.products.add(0, addingProduct)
            } else {
                _docsData.value!!
                    .products
                    .first {
                        it.barcode == addingProduct.barcode &&
                            it.expiredDate!!.toStringDD_MM_YYYYFormat() == addingProduct.expiredDate!!.toStringDD_MM_YYYYFormat()
                    }.apply {
                        this.expiredDate = addingProduct.expiredDate

                        if (isNewItem) {
                            var newQty = this.numberOfDiscounted!! + addingProduct.numberOfDiscounted!!
                            if (newQty > 999) {
                                newQty = 999
                            }

                            this.numberOfDiscounted = newQty
                        } else {
                            this.numberOfDiscounted = addingProduct.numberOfDiscounted!!
                        }
                    }
            }

            return true
        }
    }
