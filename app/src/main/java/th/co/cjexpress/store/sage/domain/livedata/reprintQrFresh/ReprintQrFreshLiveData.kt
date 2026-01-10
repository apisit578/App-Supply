package th.co.cjexpress.store.sage.domain.livedata.reprintQrFresh

import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshUIModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ReprintQrFreshLiveData
    @Inject
    constructor() {
        private val _reprintQrFreshLiveUIModel =
            MutableLiveData(ReprintQrFreshUIModel(items = SnapshotStateList()))
        val reprintQrFreshLiveUIModel: LiveData<ReprintQrFreshUIModel> get() = _reprintQrFreshLiveUIModel

        private val _reprintQrFreshItemUIModel = MutableLiveData<ReprintQrFreshItemUIModel?>()
        val reprintQrFreshItemUIModel: LiveData<ReprintQrFreshItemUIModel?> get() = _reprintQrFreshItemUIModel

        fun getProductItem(barcode: String): ReprintQrFreshItemUIModel? =
            _reprintQrFreshLiveUIModel.value?.let { product ->
                product.items.find { it.barcode == barcode }
            }

        fun addQrFreshProduct(data: ReprintQrFreshItemUIModel) {
            _reprintQrFreshLiveUIModel.value?.let {
                val items = it.items
                val existing =
                    items.find { item -> item.barcode == data.barcode && item.expireDate == data.expireDate }
                if (existing != null) {
                    items.remove(existing)
                    existing.expireDate = data.expireDate
                    existing.qrPrintCount += data.qrPrintCount
                    items.add(0, existing)
                } else {
                    items.add(0, data)
                }
            }
        }

        fun updateQrPrintCountByBarcode(
            barcode: String,
            expireDate: String,
            newCount: Int,
        ) {
            _reprintQrFreshLiveUIModel.value
                ?.let {
                    val items = it.items
                    val existing =
                        items.find { item -> item.barcode == barcode && item.expireDate == expireDate }
                    existing?.let { existingItem ->
                        items.remove(existingItem)
                        existingItem.qrPrintCount = newCount
                        items.add(0, existingItem)
                    }
                }
        }

        fun removeProductItem(barcode: String) {
            _reprintQrFreshLiveUIModel.value?.let {
                val item = getProductItem(barcode) ?: return
                it.items.remove(item)
            }
        }

        fun setQrFreshItem(data: ReprintQrFreshItemUIModel) {
            _reprintQrFreshItemUIModel.value = data
        }

        fun resetQrFreshItem() {
            _reprintQrFreshItemUIModel.value = null
        }

        fun updateQrFreshItemPrintCount(printCount: Int) {
            _reprintQrFreshItemUIModel.value?.let { it.qrPrintCount = printCount }
        }

        fun resetQrFreshItemList() {
            _reprintQrFreshLiveUIModel.value = ReprintQrFreshUIModel(items = SnapshotStateList())
        }
    }
