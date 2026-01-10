package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class OrderReceiveSummaryResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: OrderReceiveSummaryResponseData?,
) : BaseSageBffResponse

data class OrderReceiveSummaryResponseData(
    @SerializedName("createdDate")
    val createdDate: String,
    @SerializedName("items")
    val items: List<OrderReceiveSummaryItemResponse>?,
    @SerializedName("sdNo")
    val sdNo: String,
)

data class OrderReceiveSummaryItemResponse(
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("diffQty")
    val diffQty: Int,
    @SerializedName("isTote")
    val isTote: Boolean,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("toteCode")
    val toteCode: String,
    @SerializedName("unitFactor")
    val unitFactor: Int,
    @SerializedName("unitName")
    val unitName: String,
)
