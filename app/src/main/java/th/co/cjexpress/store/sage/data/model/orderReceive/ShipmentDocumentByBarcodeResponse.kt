package th.co.cjexpress.store.sage.data.model.orderReceive

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse

data class ShipmentDocumentByBarcodeResponse(
    @SerializedName("code", alternate = ["error_code"])
    override var code: String,
    @SerializedName("data")
    var data: ShipmentDocumentByBarcode,
    @SerializedName("message", alternate = ["error_message"])
    override var message: String,
    @SerializedName("ref")
    override var ref: String,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class ShipmentDocumentByBarcode(
    @SerializedName("count")
    var count: Int,
    @SerializedName("items")
    var items: List<ShipmentDocumentByBarcodeItem>,
    @SerializedName("sdNo")
    var sdNo: String,
    @SerializedName("sumOfActualQty")
    var sumOfActualQty: Int,
)

data class ShipmentDocumentByBarcodeItem(
    @SerializedName("actualQty")
    var actualQty: Int,
    @SerializedName("barcode")
    var barcode: String,
    @SerializedName("isTote")
    var isTote: Boolean,
    @SerializedName("productName")
    var productName: String,
    @SerializedName("qty")
    var qty: Int,
    @SerializedName("toteCode")
    var toteCode: String,
    @SerializedName("unitFactor")
    var unitFactor: Int,
    @SerializedName("unitName")
    var unitName: String,
)
