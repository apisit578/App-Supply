package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseInvoiceSupplierDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override var code: String,
    @SerializedName("data")
    var data: PurchaseInvoiceSupplierDetailDTOResponse,
    @SerializedName("message", alternate = ["error_message"])
    override var message: String,
    @SerializedName("ref")
    override var ref: String,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseInvoiceSupplierDetailDTOResponse(
    @SerializedName("supplierCode") var supplierCode: String,
    @SerializedName("supplierName") var supplierName: String,
    @SerializedName("supplierIsRefPO") var supplierIsRefPO: Boolean?,
)
