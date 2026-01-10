package th.co.cjexpress.store.sage.presentation.model.purchaseInvoice

data class PurchaseInvoiceSupplierUIModel(
    var supplier: PurchaseInvoiceSupplierDetailUIModel,
)

data class PurchaseInvoiceSupplierDetailUIModel(
    var supplierCode: String,
    var supplierName: String,
    var isRefPo: Boolean,
//    val items: List<PurchaseInvoiceSupplierDocumentPOUIModel> = emptyList(),
)

data class PurchaseInvoiceSupplierDocumentPOUIModel(
    val docNo: String = "",
    val shipmentDate: String = "",
    val grandTotalAmount: Double = 0.0,
    val grandTotalAmountText: String = "",
)
