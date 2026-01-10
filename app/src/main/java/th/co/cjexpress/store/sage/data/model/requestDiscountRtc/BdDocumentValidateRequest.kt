package th.co.cjexpress.store.sage.data.model.requestDiscountRtc

import com.google.gson.annotations.SerializedName

data class BdDocumentValidateRequest(
    @SerializedName("discountType")
    val discountType: String,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("expiryDate")
    val expiryDate: String? = null,
)
