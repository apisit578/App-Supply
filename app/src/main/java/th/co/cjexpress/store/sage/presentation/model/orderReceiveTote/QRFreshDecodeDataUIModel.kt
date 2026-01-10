package th.co.cjexpress.store.sage.presentation.model.orderReceiveTote

import java.util.Date

data class QRFreshDecodeDataUIModel(
    val lotNo: String,
    val barcode: String,
    val expDate: Date,
)
