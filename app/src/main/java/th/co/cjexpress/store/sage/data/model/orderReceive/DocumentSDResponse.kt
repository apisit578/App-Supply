package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class DocumentSDResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: List<DocumentSDResponseData>?,
) : BaseSageBffResponse

data class DocumentSDResponseData(
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("ldNo")
    val ldNo: String,
    @SerializedName("sdNo")
    val sdNo: String,
)
