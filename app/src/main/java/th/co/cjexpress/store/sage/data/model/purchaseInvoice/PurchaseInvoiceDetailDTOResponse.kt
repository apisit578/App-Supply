package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class PurchaseInvoiceDetailDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: PurchaseInvoiceDetailDTO,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseInvoiceDetailDTO(
    @SerializedName("poNo")
    val poNo: String,
    @SerializedName("docDate")
    val docDate: Date,
    @SerializedName("supplierCode")
    val supplierCode: String,
    @SerializedName("supplierName")
    val supplierName: String,
    @SerializedName("dueDate")
    val dueDate: Date,
    @SerializedName("shipmentDate")
    val shipmentDate: Date,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("vat")
    val vat: Double,
    @SerializedName("vatRate")
    val vatRate: Double,
    @SerializedName("roundAmount")
    val roundAmount: Int,
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("piStatus")
    val piStatus: String,
    @SerializedName("piNo")
    val piNo: String,
    @SerializedName("billNo")
    val billNo: String,
    @SerializedName("entries")
    val entries: List<PurchaseInvoiceEntryDTO>,
)

data class PurchaseInvoiceEntryDTO(
    @SerializedName("isCalVat")
    val isCalVat: Int,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("unitName")
    val unitName: String,
    @SerializedName("unitFactor")
    val unitFactor: Int,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("salePrice")
    val salePrice: Double,
    @SerializedName("actualQty")
    val actualQty: Int,
)
