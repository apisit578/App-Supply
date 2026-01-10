package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseOrderDocumentListDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("timestamp")
    override val timestamp: String,
    @SerializedName("data")
    val data: List<PurchaseOrderDocumentItemDTOResponse>,
) : BaseSageBffResponse

data class PurchaseOrderDocumentItemDTOResponse(
    @SerializedName("docNo")
    val docNo: String,
    @SerializedName("shipmentDate")
    val shipmentDate: String,
    @SerializedName("grandTotalAmount")
    val grandTotalAmount: Double,
)
