package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTO
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceEntryDTO
import java.util.Date

class PurchaseInvoiceDetailMapperTest {
    private val mapper = PurchaseInvoiceDetailMapper()

    @Test
    fun `map should convert PurchaseInvoiceDetailDTOResponse to PurchaseInvoiceDetailModel correctly`() {
        // Arrange
        val createdDate = Date()
        val docDate = Date()
        val dueDate = Date()
        val shipmentDate = Date()
        val entryDto1 =
            PurchaseInvoiceEntryDTO(
                isCalVat = 1,
                barcode = "barcode1",
                productName = "Product 1",
                unitName = "Unit 1",
                unitFactor = 2,
                qty = 10,
                salePrice = 99.99,
                actualQty = 9,
            )
        val entryDto2 =
            PurchaseInvoiceEntryDTO(
                isCalVat = 0,
                barcode = "barcode2",
                productName = "Product 2",
                unitName = "Unit 2",
                unitFactor = 1,
                qty = 5,
                salePrice = 49.99,
                actualQty = 5,
            )
        val dtoResponse =
            PurchaseInvoiceDetailDTOResponse(
                code = "200",
                ref = "REF123",
                message = "Success",
                timestamp = "2025-06-25T14:00:00Z",
                data =
                    PurchaseInvoiceDetailDTO(
                        poNo = "PO12345",
                        docDate = docDate,
                        supplierCode = "SUPP001",
                        supplierName = "Supplier Ltd.",
                        dueDate = dueDate,
                        shipmentDate = shipmentDate,
                        totalAmount = 1000.0,
                        vat = 70.0,
                        vatRate = 7.0,
                        roundAmount = 0,
                        createdDate = createdDate,
                        piStatus = "Completed",
                        piNo = "PI12345",
                        billNo = "BILL789",
                        entries = listOf(entryDto1, entryDto2),
                    ),
            )

        // Act
        val model = mapper.map(dtoResponse)

        // Assert
        assertEquals(dtoResponse.code, model.code)
        assertEquals(dtoResponse.ref, model.ref)
        assertEquals(dtoResponse.message, model.message)
        assertEquals(dtoResponse.timestamp, model.timestamp)

        val data = dtoResponse.data
        assertEquals(data.poNo, model.poNo)
        assertEquals(data.docDate, model.docDate)
        assertEquals(data.supplierCode, model.supplierCode)
        assertEquals(data.supplierName, model.supplierName)
        assertEquals(data.dueDate, model.dueDate)
        assertEquals(data.shipmentDate, model.shipmentDate)
        assertEquals(data.totalAmount, model.totalAmount, 0.0)
        assertEquals(data.vat, model.vat, 0.0)
        assertEquals(data.vatRate, model.vatRate, 0.0)
        assertEquals(data.roundAmount, model.roundAmount)
        assertEquals(data.createdDate, model.createdDate)
        assertEquals(data.piStatus, model.piStatus)
        assertEquals(data.piNo, model.piNo)
        assertEquals(data.billNo, model.billNo)

        assertEquals(data.entries.size, model.entries.size)

        data.entries.forEachIndexed { index, entryDto ->
            val entryModel = model.entries[index]
            assertEquals(entryDto.isCalVat == 1, entryModel.isCalVat)
            assertEquals(entryDto.barcode, entryModel.barcode)
            assertEquals(entryDto.productName, entryModel.productName)
            assertEquals(entryDto.unitName, entryModel.unitName)
            assertEquals(entryDto.unitFactor, entryModel.unitFactor)
            assertEquals(entryDto.qty, entryModel.qty)
            assertEquals(entryDto.salePrice, entryModel.salePrice, 0.0)
            assertEquals(entryDto.actualQty, entryModel.actualQty)
        }
    }
}
