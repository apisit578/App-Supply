package th.co.cjexpress.store.sage.presentation.model.requestDiscountRtc

data class ValidateBarcodeForBdUIModel(
    val barcode: String,
    val productName: String,
    val barFactor: Int,
    val unitName: String,
)
