package th.co.cjexpress.store.sage.presentation.model.orderReceiveTote

import androidx.compose.runtime.snapshots.SnapshotStateList
import java.util.Date

data class SdToteDocsDetailUIModel(
    val sdNo: String,
    val toteCode: String,
    val receivedDate: Date,
    val items: SnapshotStateList<SdToteDocsDetailBarcodeUIModel>,
)

data class SdToteDocsDetailBarcodeUIModel(
    override val barcode: String,
    override val skuCode: String,
    override val discountType: String,
    override val noOfExp: Int,
    var actualQty: Int,
    val barFactor: Int,
    val productName: String,
    val qty: Int,
    val unitName: String,
    var expList: SnapshotStateList<ISdToteExpiryDateModel>,
    var qrCodePrint: Int = 0,
) : ISdToteReceivingBarcodeModel

data class SdToteDocsDetailBarcodeExpiryUIModel(
    override val expDate: Date?,
    override var actualQty: Int,
    override var qty: Int,
    override var lotNo: String,
) : ISdToteExpiryDateModel(expDate, actualQty, qty, lotNo)
