package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class SdToteLowestFactorResponse(
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: SdToteLowestFactorDataResponse?,
) : BaseSageBffResponse

data class SdToteLowestFactorDataResponse(
    @SerializedName("barcode")
    val barcode: String,
)
