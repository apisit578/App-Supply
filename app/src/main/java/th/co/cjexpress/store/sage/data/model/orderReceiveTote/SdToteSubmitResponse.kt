package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class SdToteSubmitResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: SdToteSubmitResponseData?,
) : BaseSageBffResponse

data class SdToteSubmitResponseData(
    @SerializedName("sdNo")
    val sdNo: String,
    @SerializedName("qrNo")
    val qrNo: String = "",
    @SerializedName("expList")
    val expList: List<SdToteSubmitResponseDataItemExp>,
)

data class SdToteSubmitResponseDataItemExp(
    @SerializedName("expDate")
    val expDate: Date?,
    @SerializedName("mfgDate")
    val mfgDate: Date?,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("lotNo")
    val lotNo: String,
)
