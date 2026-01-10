package th.co.cjexpress.store.sage.data.model.requestDiscountRtc

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class BdDocumentValidateDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: BdDocumentValidateDataResponse?,
) : BaseSageBffResponse

data class BdDocumentValidateDataResponse(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("productName", alternate = ["barcodeName"])
    val productName: String,
    @SerializedName("barfactor")
    val barFactor: Int,
    @SerializedName("unitName")
    val unitName: String,
)
