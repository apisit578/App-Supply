package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName

data class PurchaseInvoiceCreateRequest(
    @SerializedName("poNo")
    val poNo: String,
    @SerializedName("billNo")
    val billNo: String,
    @SerializedName("supplierCode")
    val supplierCode: String
)
