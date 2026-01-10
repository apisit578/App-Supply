package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class SdToteSubmitAdjustResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    val data: SdToteSubmitAdjustDataResponse?,
) : BaseSageBffResponse

data class SdToteSubmitAdjustDataResponse(
    @SerializedName("sdNo")
    val sdNo: String,
    @SerializedName("qrNo")
    val qrNo: String = "",
    @SerializedName("expList")
    val expList: List<ExpListItemResponse>,
)

data class ExpListItemResponse(
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
