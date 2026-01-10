package th.co.cjexpress.store.sage.domain.model.orderReceiveTote

import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import java.util.Date

data class SdToteDetailMappingModel(
    val code: String,
    val message: String,
    val data: SdToteDetailData,
)

data class SdToteDetailData(
    var items: MutableList<SdToteDetailItem>,
    val sdNo: String,
    val toteCode: String,
    val createdDate: Date,
    val receivedDate: Date,
)

data class SdToteDetailItem(
    var actualQty: Int,
    val barcode: String,
    val barFactor: Int,
    val discountType: String,
    val noOfExp: Int,
    val productName: String,
    val qty: Int,
    val unitName: String,
    val skuCode: String,
    var expList: List<SdToteDetailItemExpiryDateLot>?,
)

data class SdToteDetailItemExpiryDateLot(
    override val expDate: Date?,
    override var actualQty: Int,
    override var qty: Int,
    override var lotNo: String,
) : ISdToteExpiryDateModel(expDate, actualQty, qty, lotNo)
