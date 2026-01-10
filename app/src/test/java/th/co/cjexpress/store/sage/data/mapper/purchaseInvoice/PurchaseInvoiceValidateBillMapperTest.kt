package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseInvoiceValidateBillResultDTO

class PurchaseInvoiceValidateBillMapperTest {
    private val mapper = PurchaseInvoiceValidateBillMapper()

    @Test
    fun `map should convert PurchaseInvoiceValidateBillDTOResponse to PurchaseInvoiceValidateBillModel correctly`() {
        // Arrange
        val dtoResponse =
            PurchaseInvoiceValidateBillDTOResponse(
                code = "200",
                ref = "REF123",
                message = "Validation passed",
                timestamp = "2025-06-25T14:00:00Z",
                data =
                    PurchaseInvoiceValidateBillResultDTO(
                        isValid = true,
                    ),
            )

        // Act
        val model = mapper.map(dtoResponse)

        // Assert
        assertEquals(true, model.isValid)
        assertEquals("200", model.code)
        assertEquals("REF123", model.ref)
        assertEquals("Validation passed", model.message)
        assertEquals("2025-06-25T14:00:00Z", model.timestamp)
    }
}
