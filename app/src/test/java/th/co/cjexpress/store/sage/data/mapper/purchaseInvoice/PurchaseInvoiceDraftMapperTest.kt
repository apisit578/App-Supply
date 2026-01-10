package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftDataDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceDraftItemDTOResponse

class PurchaseInvoiceDraftMapperTest {
    private val mapper = PurchaseInvoiceDraftMapper()

    @Test
    fun `map should correctly convert PurchaseInvoiceDraftDTOResponse to PurchaseInvoiceDraftModel`() {
        // Arrange
        val response =
            PurchaseInvoiceDraftDTOResponse(
                code = "200",
                ref = "ref-456",
                message = "OK",
                timestamp = "2025-06-17T12:00:00Z",
                data =
                    PurchaseInvoiceDraftDataDTOResponse(
                        piNo = "PI123",
                        items =
                            listOf(
                                PurchaseInvoiceDraftItemDTOResponse(
                                    actualQty = 1,
                                    barcode = "1234567890123",
                                    productName = "Test Product",
                                    qty = 10,
                                    unitFactor = 1,
                                    unitName = "pcs",
                                    hhTimestamp = 0L,
                                ),
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
        assertEquals("PI123", result.data.piNo)

        val item = result.data.items[0]
        assertEquals("1234567890123", item.barcode)
        assertEquals(1, item.actualQty)
    }
}
