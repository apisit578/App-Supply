package th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PurchaseInvoiceLiveData
    @Inject
    constructor() {
        private val _purchaseInvoiceSupplierLiveData =
            MutableLiveData<PurchaseInvoiceSupplierUIModel?>()
        val purchaseInvoiceSupplierLiveData: LiveData<PurchaseInvoiceSupplierUIModel?> get() = _purchaseInvoiceSupplierLiveData

        fun setPurchaseInvoiceSupplier(data: PurchaseInvoiceSupplierUIModel) {
            _purchaseInvoiceSupplierLiveData.value = data
        }

        fun resetPurchaseInvoiceSupplier() {
            _purchaseInvoiceSupplierLiveData.value = null
        }

        fun getInvoiceSupplierCode(): String =
            if (purchaseInvoiceSupplierLiveData.value != null) {
                _purchaseInvoiceSupplierLiveData.value!!.supplier.supplierCode
            } else {
                ""
            }

        fun getPurchaseInvoiceSupplier(): PurchaseInvoiceSupplierDetailUIModel =
            if (purchaseInvoiceSupplierLiveData.value != null) {
                purchaseInvoiceSupplierLiveData.value!!.supplier
            } else {
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "",
                    supplierName = "",
                    isRefPo = false,
                )
            }

        fun isRefPo(): Boolean =
            if (purchaseInvoiceSupplierLiveData.value != null) {
                _purchaseInvoiceSupplierLiveData.value!!.supplier.isRefPo
            } else {
                false
            }
    }
