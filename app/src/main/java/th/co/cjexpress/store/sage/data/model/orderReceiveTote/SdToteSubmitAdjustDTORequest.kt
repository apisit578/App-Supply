package th.co.cjexpress.store.sage.data.model.orderReceiveTote

import com.google.gson.annotations.JsonAdapter
import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.network.adapter.SpecificDateYYYYmmHHmmSSzTypeAdapter
import java.util.Date

class SdToteSubmitAdjustRequest(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("data")
    val data: List<SdToteSubmitAdjustItemDataRequest>,
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("sdNo")
    val sdNo: String,
    @SerializedName("toteCode")
    val toteCode: String,
)

data class SdToteSubmitAdjustItemDataRequest(
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("beforeQty")
    val beforeQty: Int,
    @SerializedName("expDate")
    @JsonAdapter(SpecificDateYYYYmmHHmmSSzTypeAdapter::class)
    val expDate: Date?,
    @SerializedName("listLot")
    val listLot: List<SdToteSubmitAdjustLotDataRequest>,
)

data class SdToteSubmitAdjustLotDataRequest(
    @SerializedName("lotNo")
    val lotNo: String,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("actualQty")
    val actualQty: Int,
)
