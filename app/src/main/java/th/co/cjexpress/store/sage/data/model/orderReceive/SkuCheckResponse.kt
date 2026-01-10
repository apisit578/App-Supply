package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class SkuCheckResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: SkuBarcodeDataResponse?,
) : BaseSageBffResponse

data class SkuBarcodeDataResponse(
    @SerializedName("backLocationCode")
    val backLocationCode: String?,
    @SerializedName("backLocationName")
    val backLocationName: String?,
    @SerializedName("barcodes")
    val barcodes: List<BarcodeDataResponse>?,
    @SerializedName("daysUntilExpiry")
    val daysUntilExpiry: Int?,
    @SerializedName("defaultPurchaseQuantity")
    val defaultPurchaseQuantity: Int?,
    @SerializedName("inventoryQuantity")
    val inventoryQuantity: Int?,
    @SerializedName("maxStock")
    val maxStock: Int?,
    @SerializedName("minStock")
    val minStock: Int?,
    @SerializedName("scmStatus")
    val scmStatus: String?,
    @SerializedName("skuCode")
    val skuCode: String?,
    @SerializedName("skuName")
    val skuName: String?,
    @SerializedName("supplierReturnCode")
    val supplierReturnCode: String?,
    @SerializedName("tier")
    val tier: String?,
)

data class BarcodeDataResponse(
    @SerializedName("barcode")
    val barcode: String?,
    @SerializedName("barcodeFactor")
    val barcodeFactor: Int?,
    @SerializedName("frontLocationCode")
    val frontLocationCode: String?,
    @SerializedName("frontLocationName")
    val frontLocationName: String?,
    @SerializedName("frontStockQuantity")
    val frontStockQuantity: Int?,
    @SerializedName("maxFrontStock")
    val maxFrontStock: Int?,
    @SerializedName("minFrontStock")
    val minFrontStock: Int?,
    @SerializedName("retailPrice")
    val retailPrice: Int?,
    @SerializedName("tagName")
    val tagName: String?,
    @SerializedName("unitCode")
    val unitCode: String?,
    @SerializedName("unitName")
    val unitName: String?,
)
