package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName

data class SdToteDiscountsConfigRequest(
    @SerializedName("barcodes")
    val barcodes: List<String>,
)
