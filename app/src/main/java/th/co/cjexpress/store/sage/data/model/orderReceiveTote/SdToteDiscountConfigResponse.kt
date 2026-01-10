package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class SdToteDiscountsConfigResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: List<SdToteDiscountsConfigResponseData>,
) : BaseSageBffResponse

data class SdToteDiscountsConfigResponseData(
    @SerializedName("skuCode")
    val skuCode: String,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("noOfExp")
    val noOfExp: Int,
    @SerializedName("skuName")
    val skuName: String,
    @SerializedName("unitName")
    val unitName: String,
)
