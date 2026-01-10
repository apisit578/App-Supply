package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class SearchSdToteResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: SearchSdToteResponseData?,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class SearchSdToteResponseData(
    @SerializedName("items")
    val items: List<SearchSdToteResponseDataItem>?,
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("sdNo")
    val sdNo: String,
    @SerializedName("toteCode")
    val toteCode: String?,
    @SerializedName("receivedDate")
    val receivedDate: Date,
)

data class SearchSdToteResponseDataItem(
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("barFactor")
    val barFactor: Int,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("noOfExp")
    val noOfExp: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("unitName")
    val unitName: String,
    @SerializedName("skuCode")
    val skuCode: String?,
    @SerializedName("expList")
    val expList: List<SearchSdToteResponseDataItemExp>,
)

data class SearchSdToteResponseDataItemExp(
    @SerializedName("expDate")
    val expDate: Date?,
    @SerializedName("mfgDate")
    val mfgDate: Date?,
    @SerializedName("actualQty")
    val actualQty: Int?,
    @SerializedName("qty")
    val qty: Int?,
    @SerializedName("lotNo")
    val lotNo: String?,
)
