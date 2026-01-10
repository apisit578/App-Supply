package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateEntryDTO
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceCreateResultDTO
import java.util.Date

class PurchaseInvoiceCreateMapperTest {
    private val mapper = PurchaseInvoiceCreateMapper()

    @Test
    fun `map should convert PurchaseInvoiceCreateDTOResponse to PurchaseInvoiceCreateModel correctly`() {
        // Arrange
        val createdDate = Date()
        val docDate = Date()
        val dueDate = Date()
        val shipmentDate = Date()
        val dtoResponse =
            PurchaseInvoiceCreateDTOResponse(
                code = "SUCCESS",
                ref = "REF123",
                message = "Success",
                timestamp = "2025-06-25T14:00:00Z",
                data =
                    PurchaseInvoiceCreateResultDTO(
                        billNo = "BILL001",
                        createdDate = createdDate,
                        docDate = docDate,
                        dueDate = dueDate,
                        entries =
                            listOf(
                                PurchaseInvoiceCreateEntryDTO(
                                    actualQty = 5,
                                    barcode = "1234567890123",
                                    isCalVat = 1,
                                    productName = "น้ำดื่ม 500ml",
                                    qty = 5,
                                    salePrice = 10.0,
                                    setPrice = 0.0,
                                    sumPrice = 50.0,
                                    unitFactor = 1,
                                    unitName = "ขวด",
                                    hhTimestamp = 0L,
                                ),
                            ),
                        piNo = "PI987654",
                        piStatus = "DRAFT",
                        poNo = "PO2025-00001",
                        roundAmount = 0.0,
                        shipmentDate = shipmentDate,
                        supplierCode = "SUP001",
                        supplierName = "บริษัท ทดสอบ จำกัด",
                        totalAmount = 50.0,
                        vat = 3.5,
                        vatRate = 7.0,
                    ),
            )

        // Act
        val model = mapper.map(dtoResponse)

        // Assert
        assertEquals("SUCCESS", model.code)
        assertEquals("REF123", model.ref)
        assertEquals("Success", model.message)
        assertEquals("BILL001", model.billNo)
        assertEquals(createdDate, model.createdDate)
        assertEquals(docDate, model.docDate)
        assertEquals(dueDate, model.dueDate)
        assertEquals("PI987654", model.piNo)
        assertEquals("DRAFT", model.piStatus)
        assertEquals("PO2025-00001", model.poNo)
        assertEquals(0.0, model.roundAmount, 0.001)
        assertEquals(shipmentDate, model.shipmentDate)
        assertEquals("SUP001", model.supplierCode)
        assertEquals("บริษัท ทดสอบ จำกัด", model.supplierName)
        assertEquals(50.0, model.totalAmount, 0.001)
        assertEquals(3.5, model.vat, 0.001)
        assertEquals(7.0, model.vatRate, 0.001)
        assertEquals("2025-06-25T14:00:00Z", model.timestamp)

        assertEquals(1, model.entries.size)
        val entry = model.entries[0]
        assertEquals(5, entry.actualQty)
        assertEquals("1234567890123", entry.barcode)
        assertEquals(true, entry.isCalVat)
        assertEquals("น้ำดื่ม 500ml", entry.productName)
        assertEquals(5, entry.qty)
        assertEquals(10.0, entry.salePrice, 0.001)
        assertEquals(0.0, entry.setPrice, 0.001)
        assertEquals(50.0, entry.sumPrice, 0.001)
        assertEquals(1, entry.unitFactor)
        assertEquals("ขวด", entry.unitName)
    }
}
