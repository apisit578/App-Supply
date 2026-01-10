package th.co.cjexpress.store.sage.data.model.requestDiscountRtc

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class BdDocumentSubmitDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: BdDocumentSubmitDataResponse?,
) : BaseSageBffResponse

data class BdDocumentSubmitDataResponse(
    @SerializedName("documentNumber")
    val documentNumber: String,
)
