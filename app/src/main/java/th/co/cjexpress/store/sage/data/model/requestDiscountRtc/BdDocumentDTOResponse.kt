package th.co.cjexpress.store.sage.data.model.requestDiscountRtc

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class BdDocumentDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: List<BdDocumentDataResponse>?,
) : BaseSageBffResponse

data class BdDocumentDataResponse(
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("discountType")
    val discountType: RequestDiscountType,
    @SerializedName("documentNumber")
    val documentNumber: String,
)
