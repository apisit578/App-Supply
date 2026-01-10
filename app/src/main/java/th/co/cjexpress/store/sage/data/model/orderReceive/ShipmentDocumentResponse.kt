package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class ShipmentDocumentResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: ShipmentResponseData?,
) : BaseSageBffResponse

data class ShipmentResponseData(
    @SerializedName("items")
    val items: List<ShipmentResponseDataItem>?,
    @SerializedName("createdDate")
    val createdDate: String?,
    @SerializedName("sdNo")
    val sdNo: String?,
    @SerializedName("ldNo")
    val ldNo: String?,
    @SerializedName("count")
    val count: Int?,
    @SerializedName("sumOfActualQty")
    val sumOfActualQty: Int?,
)

data class ShipmentResponseDataItem(
    @SerializedName("actualQty")
    val actualQty: Int?,
    @SerializedName("barcode")
    val barcode: String?,
    @SerializedName("isTote")
    val isTote: Boolean?,
    @SerializedName("toteCode")
    val toteCode: String?,
    @SerializedName("productName")
    val productName: String?,
    @SerializedName("qty")
    val qty: Int?,
    @SerializedName("unitName")
    val unitName: String?,
    @SerializedName("unitFactor")
    val unitFactor: Int?,
)
