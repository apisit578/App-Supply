package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseInvoiceSupplierListDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override var code: String,
    @SerializedName("data")
    var data: List<PurchaseInvoiceSupplierDetailDTOResponse>,
    @SerializedName("message", alternate = ["error_message"])
    override var message: String,
    @SerializedName("ref")
    override var ref: String,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse
