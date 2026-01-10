package th.co.cjexpress.store.sage.data.model.requestDiscountRtc

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.constants.RequestDiscountType
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class BdDocumentNoDetailDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: BdDocumentNoDetailDataResponse,
) : BaseSageBffResponse

data class BdDocumentNoDetailDataResponse(
    @SerializedName("documentNumber")
    val documentNumber: String,
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("discountType")
    val discountType: RequestDiscountType,
    @SerializedName("products")
    val products: List<BdDocumentNoDetailProductItemResponse>,
)

data class BdDocumentNoDetailProductItemResponse(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("expiredDate")
    val expiredDate: Date,
    @SerializedName("numberOfDiscounted")
    val numberOfDiscounted: Int,
    @SerializedName("barFactor")
    val barFactor: Int,
    @SerializedName("unitFactor")
    val unitFactor: String,
)
