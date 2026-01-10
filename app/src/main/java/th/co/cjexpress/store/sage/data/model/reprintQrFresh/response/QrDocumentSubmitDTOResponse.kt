package th.co.cjexpress.store.sage.data.model.reprintQrFresh.response

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class QrDocumentSubmitDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: QrDocumentSubmitResponseData,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class QrDocumentSubmitResponseData(
    @SerializedName("documentNumber")
    val documentNumber: String,
)
