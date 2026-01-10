package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName

data class SkuCheckRequest(
    @SerializedName("barcode")
    val barcode: String,
)
