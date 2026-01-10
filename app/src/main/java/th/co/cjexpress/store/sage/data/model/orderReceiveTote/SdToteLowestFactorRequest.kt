package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.SerializedName

data class SdToteLowestFactorRequest(
    @SerializedName("barcode")
    val barcode: String
)
