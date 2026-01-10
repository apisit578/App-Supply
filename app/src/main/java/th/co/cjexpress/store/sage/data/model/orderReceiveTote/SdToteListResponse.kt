package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class SdToteListResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: List<SdToteResponseData>?,
) : BaseSageBffResponse

data class SdToteResponseData(
    @SerializedName("sdNo")
    val sdNo: String,
    @SerializedName("toteCode")
    val toteCode: String?,
    @SerializedName("createdDate")
    val createdDate: Date,
)
