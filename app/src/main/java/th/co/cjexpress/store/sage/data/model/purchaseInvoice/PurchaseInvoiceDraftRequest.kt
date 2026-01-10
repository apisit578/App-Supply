package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName

data class PurchaseInvoiceDraftRequest(
    @SerializedName("piNo")
    val piNo: String,
    @SerializedName("docNo")
    val docNo: String,
    @SerializedName("flagPO")
    val flagPO: Int,
    @SerializedName("supplierCode")
    val supplierCode: String,
    @SerializedName("billNo")
    val billNo: String,
    @SerializedName("items")
    val items: List<PurchaseInvoiceDraftItemRequest>,
)

data class PurchaseInvoiceDraftItemRequest(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("actualQty")
    val actualQty: Int,
)
