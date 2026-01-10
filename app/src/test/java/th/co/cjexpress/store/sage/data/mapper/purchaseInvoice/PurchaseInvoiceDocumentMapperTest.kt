package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentItemDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDocumentListDTOResponse
import java.util.Date

class PurchaseInvoiceDocumentMapperTest {
    private val mapper = PurchaseInvoiceDocumentMapper()

    @Test
    fun `map should correctly convert PurchaseInvoiceDocumentListResponse to PurchaseInvoiceDocumentListModel`() {
        // Arrange
        val createdDate = Date()
        val docDate = Date()
        val dueDate = Date()
        val shipmentDate = Date()
        val response =
            PurchaseInvoiceDocumentListDTOResponse(
                code = "200",
                ref = "ref-123",
                message = "Success",
                timestamp = "2025-06-17T10:00:00Z",
                data =
                    listOf(
                        PurchaseInvoiceDocumentItemDTOResponse(
                            piNo = "PI001",
                            poNo = "PO001",
                            billNo = "BILL001",
                            createdDate = createdDate,
                            docDate = docDate,
                            dueDate = dueDate,
                            shipmentDate = shipmentDate,
                        ),
                    ),
            )

        // Act
        val result = mapper.map(response)

        // Assert
        assertEquals("200", result.code)
        assertEquals("ref-123", result.ref)
        assertEquals("Success", result.message)
        assertEquals("2025-06-17T10:00:00Z", result.timestamp)
        assertEquals(1, result.data.size)

        val item = result.data[0]
        assertEquals("PI001", item.piNo)
        assertEquals("PO001", item.poNo)
        assertEquals(createdDate, item.createdDate)
        assertEquals(docDate, item.docDate)
        assertEquals(dueDate, item.dueDate)
        assertEquals(shipmentDate, item.shipmentDate)
    }
}
