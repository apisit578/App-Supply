package th.co.cjexpress.store.sage.data.model.requestDiscountRtc

data class UpsertBdRequest(
    val documentNumber: String,
    val discountType: String,
    val deviceName: String,
    var items: List<UpsertBdProductRequest>,
)

data class UpsertBdProductRequest(
    val barcode: String,
    var expiryDate: String,
    var requestQty: Int,
)
