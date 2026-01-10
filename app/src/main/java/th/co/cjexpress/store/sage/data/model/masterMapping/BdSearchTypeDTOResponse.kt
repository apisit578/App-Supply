package th.co.cjexpress.store.sage.data.model.masterMapping

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class BdSearchTypeDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: List<BdSearchTypeDataResponse>?,
) : BaseSageBffResponse

data class BdSearchTypeDataResponse(
    @SerializedName("code")
    val code: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("name2")
    val name2: String,
)
