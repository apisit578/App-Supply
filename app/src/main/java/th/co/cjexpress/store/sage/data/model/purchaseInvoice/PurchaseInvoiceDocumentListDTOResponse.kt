package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class PurchaseInvoiceDocumentListDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: List<PurchaseInvoiceDocumentItemDTOResponse>,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseInvoiceDocumentItemDTOResponse(
    @SerializedName("piNo")
    val piNo: String,
    @SerializedName("poNo")
    val poNo: String,
    @SerializedName("billNo")
    val billNo: String,
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("docDate")
    val docDate: Date,
    @SerializedName("dueDate")
    val dueDate: Date,
    @SerializedName("shipmentDate")
    val shipmentDate: Date,
)
