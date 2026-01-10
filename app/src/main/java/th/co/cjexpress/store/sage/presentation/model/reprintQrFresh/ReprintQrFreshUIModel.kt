package th.co.cjexpress.store.sage.presentation.model.reprintQrFresh

import androidx.compose.runtime.snapshots.SnapshotStateList

data class ReprintQrFreshUIModel(
    val items: SnapshotStateList<ReprintQrFreshItemUIModel>,
)

data class ReprintQrFreshItemUIModel(
    val barcode: String,
    val skuName: String,
    var expireDate: String,
    var qrPrintCount: Int,
)
