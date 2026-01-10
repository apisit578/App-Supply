package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseInvoiceCalculateDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: PurchaseInvoiceCalculateDataDTOResponse,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseInvoiceCalculateDataDTOResponse(
    @SerializedName("piNo")
    val piNo: String,
    @SerializedName("poNo")
    val poNo: String,
    @SerializedName("supplierCode")
    val supplierCode: String,
    @SerializedName("itemCount")
    val itemCount: Int,
    @SerializedName("receivedItemCount")
    val receivedItemCount: Int,
    @SerializedName("orderedTotalAmount")
    val orderedTotalAmount: Double,
    @SerializedName("receivedTotalAmount")
    val receivedTotalAmount: Double,
    @SerializedName("totalReceivedQty")
    val totalReceivedQty: Int,
    @SerializedName("totalOrderedQty")
    val totalOrderedQty: Int,
)
