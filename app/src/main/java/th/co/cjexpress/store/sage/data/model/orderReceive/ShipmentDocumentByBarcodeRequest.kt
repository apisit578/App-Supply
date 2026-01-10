package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName

data class ShipmentDocumentByBarcodeRequest(
    @SerializedName("items")
    val items: List<ShipmentItemRequest>,
)

data class ShipmentItemRequest(
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("code")
    val barcode: String,
)
