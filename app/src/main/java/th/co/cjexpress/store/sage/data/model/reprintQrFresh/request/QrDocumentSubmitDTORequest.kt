package th.co.cjexpress.store.sage.data.model.reprintQrFresh.request

data class QrDocumentSubmitDTORequest(
    val lotNumber: String,
    val data: List<QrDocumentSubmitData>,
)

data class QrDocumentSubmitData(
    val barcode: String,
    val expireDate: String,
    val qty: Int,
)
