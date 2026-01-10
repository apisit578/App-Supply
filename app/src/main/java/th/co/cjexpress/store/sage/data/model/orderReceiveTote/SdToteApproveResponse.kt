package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class SdToteApproveResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: SdToteApproveResponseData?,
) : BaseSageBffResponse

data class SdToteApproveResponseData(
    @SerializedName("sdNo")
    val sdNo: String,
    @SerializedName("ldNo")
    val ldNo: String,
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("items")
    val items: List<SdToteItem>,
)

data class SdToteItem(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("toteCode")
    val toteCode: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("isTote")
    val isTote: Boolean,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("unitName")
    val unitName: String,
    @SerializedName("unitFactor")
    val unitFactor: Int,
)
