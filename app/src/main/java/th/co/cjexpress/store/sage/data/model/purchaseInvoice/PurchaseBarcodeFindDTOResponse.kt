package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseBarcodeFindDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: List<PurchaseBarcodeFindDataDTOResponse>,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseBarcodeFindDataDTOResponse(
    @SerializedName("barFactor")
    val barFactor: Int,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("isAllowBuy")
    val isAllowBuy: Boolean,
    @SerializedName("isFreshLife")
    val isFreshLife: Boolean,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("unitCode")
    val unitCode: String,
    @SerializedName("unitName")
    val unitName: String,
)
