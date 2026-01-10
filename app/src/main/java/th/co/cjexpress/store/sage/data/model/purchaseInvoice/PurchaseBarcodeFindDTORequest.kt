package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName

data class PurchaseBarcodeFindDTORequest(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("supplierCode")
    val supplierCode: String,
)
