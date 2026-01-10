package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName

data class ShipmentDocumentOpenRequest(
    @SerializedName("shipmentNo")
    val shipmentNo: String,
)
