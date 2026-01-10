package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName

data class PurchaseInvoiceValidateBillRequest(
    @SerializedName("billNo")
    val billNo: String,
    @SerializedName("piNo")
    val piNo: String,
    @SerializedName("supplierCode")
    val supplierCode: String,
)
