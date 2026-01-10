package th.co.cjexpress.store.sage.domain.model.requestDiscountRtc

import androidx.compose.runtime.snapshots.SnapshotStateList
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import java.util.Date

data class BdDocumentNoDetailMappingModel(
    val code: String,
    val ref: String,
    val message: String,
    val timestamp: String,
    val data: BdDocumentNoDetailModel?,
)

data class BdDocumentNoDetailModel(
    val documentNumber: String,
    val createdDate: String,
    val discountType: RequestDiscountType,
    var products: SnapshotStateList<BdDocumentNoDetailProductModel>,
)

data class BdDocumentNoDetailProductModel(
    val barcode: String,
    val productName: String,
    var expiredDate: Date?,
    var numberOfDiscounted: Int?,
    val barFactor: Int,
    val unitFactor: String,
)
