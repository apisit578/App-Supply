package th.co.cjexpress.store.sage.data.model.purchaseInvoice

import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import java.util.Date

data class PurchaseInvoiceCreateDTOResponse(
    @SerializedName("code", alternate = ["error_code"])
    override val code: String,
    @SerializedName("ref")
    override val ref: String,
    @SerializedName("message", alternate = ["error_message"])
    override val message: String,
    @SerializedName("data")
    val data: PurchaseInvoiceCreateResultDTO,
    @SerializedName("timestamp")
    override val timestamp: String,
) : BaseSageBffResponse

data class PurchaseInvoiceCreateResultDTO(
    @SerializedName("billNo")
    val billNo: String,
    @SerializedName("createdDate")
    val createdDate: Date,
    @SerializedName("docDate")
    val docDate: Date,
    @SerializedName("dueDate")
    val dueDate: Date,
    @SerializedName("items")
    val entries: List<PurchaseInvoiceCreateEntryDTO>,
    @SerializedName("piNo")
    val piNo: String,
    @SerializedName("piStatus")
    val piStatus: String,
    @SerializedName("poNo")
    val poNo: String,
    @SerializedName("roundAmount")
    val roundAmount: Double,
    @SerializedName("shipmentDate")
    val shipmentDate: Date,
    @SerializedName("supplierCode")
    val supplierCode: String,
    @SerializedName("supplierName")
    val supplierName: String,
    @SerializedName("totalAmount")
    val totalAmount: Double,
    @SerializedName("vat")
    val vat: Double,
    @SerializedName("vatRate")
    val vatRate: Double,
)

data class PurchaseInvoiceCreateEntryDTO(
    @SerializedName("actualQty")
    val actualQty: Int,
    @SerializedName("barcode")
    val barcode: String,
    @SerializedName("isCalVat")
    val isCalVat: Int,
    @SerializedName("productName")
    val productName: String,
    @SerializedName("qty")
    val qty: Int,
    @SerializedName("salePrice")
    val salePrice: Double,
    @SerializedName("setPrice")
    val setPrice: Double,
    @SerializedName("sumPrice")
    val sumPrice: Double,
    @SerializedName("unitFactor")
    val unitFactor: Int,
    @SerializedName("unitName")
    val unitName: String,
    @SerializedName("hhTimestamp")
    val hhTimestamp: Long,
)
