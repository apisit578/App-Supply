package th.co.cjexpress.store.sage.domain.model.reprintQrFresh

data class QrDocumentSubmitModel(
    val lotNumber: String,
    val data: List<QrDocumentSubmitItemModel>,
)

data class QrDocumentSubmitItemModel(
    val barcode: String,
    var expireDate: String,
    val qty: Int,
)
