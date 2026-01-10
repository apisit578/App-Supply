package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName

data class PurchaseInvoiceCalculateDTORequest(
    @SerializedName("items")
    val items: List<PurchaseInvoiceCalculateItemDTORequest>,
    @SerializedName("piNo")
    val piNo: String,
    @SerializedName("poNo")
    val poNo: String,
    @SerializedName("supplierCode")
    val supplierCode: String,
)

data class PurchaseInvoiceCalculateItemDTORequest(
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("qty")
    val qty: Int,
)
