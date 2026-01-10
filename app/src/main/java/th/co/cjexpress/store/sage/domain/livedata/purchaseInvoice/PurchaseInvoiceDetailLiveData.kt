package th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice

import androidx.compose.runtime.toMutableStateList
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseInvoiceDetailLiveData
    @Inject
    constructor() {
        private val _purchaseInvoiceDetailUIModel = MutableLiveData<PurchaseInvoiceDetailUIModel?>()
        val purchaseInvoiceDetailUIModel: LiveData<PurchaseInvoiceDetailUIModel?> get() = _purchaseInvoiceDetailUIModel

        private val _purchaseInvoiceItemUIModel = MutableLiveData<PurchaseInvoiceEntryUIModel?>()
        val purchaseInvoiceItemUIModel: LiveData<PurchaseInvoiceEntryUIModel?> get() = _purchaseInvoiceItemUIModel

        private val _updatedTimestamp = MutableLiveData<Long>()
        val updatedTimestamp: LiveData<Long> get() = _updatedTimestamp

        fun setPurchaseInvoiceDetail(data: PurchaseInvoiceDetailUIModel) {
            _purchaseInvoiceDetailUIModel.value = data
        }

        fun resetPurchaseInvoiceDetail() {
            _purchaseInvoiceDetailUIModel.value = null
        }

        fun setPurchaseInvoiceItem(data: PurchaseInvoiceEntryUIModel) {
            // Create a new object to prevent reference the same object as entries.
            _purchaseInvoiceItemUIModel.value = data.copy()
        }

        fun resetPurchaseInvoiceItem() {
            _purchaseInvoiceItemUIModel.value = null
        }

        fun getPurchaseInvoiceOrder(barcode: String): IPurchaseInvoiceProductsModel? =
            _purchaseInvoiceDetailUIModel.value?.let { purchaseInvoiceDetailData ->
                purchaseInvoiceDetailData.entries.find { it.barcode == barcode }
            }

        fun updatePurchaseInvoiceProduct(productList: List<IPurchaseInvoiceProductsModel>) {
            _purchaseInvoiceDetailUIModel.value?.let { purchaseOrder ->
                val updatedEntries =
                    productList.mapNotNull { product ->
                        purchaseOrder.entries
                            .find { it.barcode == product.barcode }
                            ?.copy(actualQty = product.actualQty)
                    }

                purchaseOrder.entries.removeAll { entry -> productList.any { it.barcode == entry.barcode } }
                purchaseOrder.entries.addAll(0, updatedEntries)
            }
        }

        fun updatePurchaseInvoiceItem(model: IPurchaseInvoiceProductsModel) {
            _purchaseInvoiceDetailUIModel.value?.let { purchaseOrder ->
                purchaseOrder.entries.find { it.barcode == model.barcode }?.let { updateItem ->
                    purchaseOrder.entries.remove(updateItem)
                    purchaseOrder.entries.add(
                        0,
                        updateItem.copy(
                            actualQty = model.actualQty,
                        ),
                    )
                }
            }
        }

        fun addPurchaseInvoiceProduct(products: PurchaseInvoiceEntryUIModel) {
            _purchaseInvoiceDetailUIModel.value?.let { detail ->
                val existing = detail.entries.find { it.barcode == products.barcode }
                if (existing != null) {
                    detail.entries.remove(existing)
                    existing.actualQty += products.actualQty
                    detail.entries.add(0, existing)
                } else {
                    detail.entries.add(0, products)
                }
            }
        }

        fun removePurchaseInvoiceProductItem(barcode: String) {
            _purchaseInvoiceDetailUIModel.value?.let { detail ->
                val itemsToRemove = detail.entries.filter { it.barcode == barcode }
                detail.entries.removeAll(itemsToRemove)
                Timber.d("Remove all Item")
            }
        }

        fun updatePurchaseInvoiceItemActualQty(actualQty: Int) {
            _purchaseInvoiceItemUIModel.value?.let { it.actualQty = actualQty }
        }

        fun updatePurchaseInvoiceDetailBillNo(billNo: String) {
            _purchaseInvoiceDetailUIModel.value?.let { it.billNo = billNo }
        }

        fun setUpdatedTimestamp(timestamps: Long) {
            _updatedTimestamp.value = timestamps
        }

        fun getPurchaseInvoiceDetailBillNo(): String = _purchaseInvoiceDetailUIModel.value?.billNo.orEmpty()

        fun getPIDetail(): PurchaseInvoiceDetailUIModel =
            _purchaseInvoiceDetailUIModel.value ?: PurchaseInvoiceDetailUIModel(
                supplierCode = "",
                supplierName = "",
                poNo = "",
                piNo = "",
                billNo = "",
                entries = listOf<PurchaseInvoiceEntryUIModel>().toMutableStateList(),
            )
    }
