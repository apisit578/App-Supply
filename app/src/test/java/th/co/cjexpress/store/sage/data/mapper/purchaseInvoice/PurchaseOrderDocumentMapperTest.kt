package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentItemDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDocumentListDTOResponse

class PurchaseOrderDocumentMapperTest {
    private val mapper = PurchaseOrderDocumentMapper()

    @Test
    fun `map should correctly convert PurchaseOrderDocumentListResponse to PurchaseOrderDocumentListModel`() {
        // Arrange
        val response =
            PurchaseOrderDocumentListDTOResponse(
                code = "200",
                ref = "ref-456",
                message = "OK",
                timestamp = "2025-06-17T12:00:00Z",
                data =
                    listOf(
                        PurchaseOrderDocumentItemDTOResponse(
                            docNo = "PO123",
                            shipmentDate = "2025-06-20",
                            grandTotalAmount = 2500.75,
                        ),
                    ),
            )

        // Act
        val result = mapper.map(response)

        // Assert
        assertEquals("200", result.code)
        assertEquals("ref-456", result.ref)
        assertEquals("OK", result.message)
        assertEquals("2025-06-17T12:00:00Z", result.timestamp)
        assertEquals(1, result.data.size)

        val item = result.data[0]
        assertEquals("PO123", item.docNo)
        assertEquals("2025-06-20", item.shipmentDate)
        assertEquals(2500.75, item.grandTotalAmount, 0.001)
    }
}
