package th.co.cjexpress.store.sage.domain.model

import java.util.Date

data class QRFreshDecodeDataModel(
    val lotNo: String,
    val barcode: String,
    val expDate: Date,
)
