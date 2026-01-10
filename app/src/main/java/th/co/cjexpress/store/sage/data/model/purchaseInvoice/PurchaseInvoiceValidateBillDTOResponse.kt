package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseInvoiceValidateBillDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: PurchaseInvoiceValidateBillResultDTO,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseInvoiceValidateBillResultDTO(
    @SerializedName("isValid")
    val isValid: Boolean,
)
