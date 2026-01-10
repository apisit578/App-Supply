package th.co.cjexpress.store.sage.domain.model.orderReceive

data class SkuCheckModel(
    val code: String,
    val message: String,
    val data: SkuBarcodeDataModel,
)

data class SkuBarcodeDataModel(
    val skuName: String,
    val barcodes: List<BarcodeDataModel>,
)

data class BarcodeDataModel(
    val barcode: String,
    val barcodeFactor: Int,
    val unitCode: String,
    val unitName: String,
)
