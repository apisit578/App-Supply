package th.co.cjexpress.store.sage.domain.model.orderReceiveTote

import java.util.Date

data class SdToteSubmitAdjustModel(
    val ref: String,
    val code: String,
    val message: String,
    val data: SdToteSubmitAdjustSdModel?,
)

data class SdToteSubmitAdjustSdModel(
    val sdNo: String,
    val qrNo: String,
    val expList: List<SdToteSubmitAdjustExpItem>,
)

data class SdToteSubmitAdjustExpItem(
    val expDate: Date?,
    val mfgDate: Date?,
    val qty: Int,
    val actualQty: Int,
    val lotNo: String,
)
