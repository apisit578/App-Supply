package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class PurchaseInvoiceDraftDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override var code: String,
    @SerializedName("data")
    var data: PurchaseInvoiceDraftDataDTOResponse?,
    @SerializedName("message", alternate = ["error_message"])
    override var message: String,
    @SerializedName("ref")
    override var ref: String,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseInvoiceDraftDataDTOResponse(
    @SerializedName("piNo")
    var piNo: String,
    @SerializedName("items")
    var items: List<PurchaseInvoiceDraftItemDTOResponse>,
)

data class PurchaseInvoiceDraftItemDTOResponse(
    @SerializedName("actualQty")
    var actualQty: Int,
    @SerializedName("barcode")
    var barcode: String,
    @SerializedName("productName")
    var productName: String,
    @SerializedName("qty")
    var qty: Int,
    @SerializedName("unitFactor")
    var unitFactor: Int,
    @SerializedName("unitName")
    var unitName: String,
    @SerializedName("hhTimestamp")
    val hhTimestamp: Long,
)
