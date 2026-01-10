package th.co.cjexpress.store.sage.presentation.model.orderReceiveTote

import androidx.compose.runtime.snapshots.SnapshotStateList
import th.co.cjexpress.store.sage.utils.DateUtils
import java.util.Date

data class SdToteSubmitUIModel(
    val sdNo: String,
    val toteCode: String,
    val receivedDate: Date,
    override var noOfExp: Int,
    override val barcode: String,
    override val skuCode: String,
    override var discountType: String,
    var actualQty: Int,
    var expList: SnapshotStateList<ISdToteExpiryDateModel>,
    var quantityAdjustDate: String? = null,
) : ISdToteReceivingBarcodeModel {
    var isAddLot: Boolean = false
    var isNewBarcode: Boolean = false
    var productName: String = ""
    var unitName: String = ""
    var qty: Int = 0
    var beforeQty: Map<String?, Int> = mapOf()

    fun getExpiryDate(): Date = DateUtils.adjustDateByDays(receivedDate, noOfExp)
}
