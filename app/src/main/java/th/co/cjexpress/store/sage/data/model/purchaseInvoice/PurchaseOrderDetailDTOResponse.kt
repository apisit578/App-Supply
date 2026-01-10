package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseOrderDetailDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: PurchaseOrderDataDTO,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseOrderDataDTO(
    @SerializedName("docNo")
    val docNo: String,
    @SerializedName("supplierCode")
    val supplierCode: String,
    @SerializedName("supplierName")
    val supplierName: String,
    @SerializedName("items")
    val items: List<PurchaseOrderItemDTO>,
)

data class PurchaseOrderItemDTO(
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("qty")
    val qty: Double,
    @SerializedName("unitName")
    val unitName: String,
    @SerializedName("unitFactor")
    val unitFactor: Double,
)
