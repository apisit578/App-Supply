package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.network.adapter.SpecificDateYYYYmmHHmmSSzTypeAdapter
import java.util.Date

data class SdToteSubmitRequest(
    @SerializedName("sdNo")
    val sdNo: String,
    @SerializedName("toteCode")
    val toteCode: String,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("skuCode")
    val skuCode: String,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("data")
    val data: List<SdToteSubmitExpRequest>,
)

data class SdToteSubmitExpRequest(
    @SerializedName("expDate")
    @JsonAdapter(SpecificDateYYYYmmHHmmSSzTypeAdapter::class)
    val expDate: Date?,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("lotNo")
    val lotNo: String,
)
