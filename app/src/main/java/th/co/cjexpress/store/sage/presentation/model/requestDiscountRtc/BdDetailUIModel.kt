package th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc

import androidx.compose.runtime.snapshots.SnapshotStateList
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import java.util.Date

data class BdDocumentNoDetailUIModel(
    val documentNumber: String,
    val createdDate: String,
    val discountType: RequestDiscountType,
    var products: SnapshotStateList<BdDetailProductUIModel>,
)

data class BdDetailProductUIModel(
    val barcode: String,
    val productName: String,
    var expiredDate: Date?,
    var numberOfDiscounted: Int?,
    val barFactor: Int,
    val unitFactor: String,
)
