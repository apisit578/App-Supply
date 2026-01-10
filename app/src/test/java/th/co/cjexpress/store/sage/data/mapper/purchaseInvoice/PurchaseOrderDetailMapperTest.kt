package th.co.cjexpress.store.sage.data.mapper.purchaseInvoice

import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDataDTO
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderDetailDTOResponse
import th.co.cjexpress.store.sage.data.model.purchaseInvoice.PurchaseOrderItemDTO

class PurchaseOrderDetailMapperTest {
    private val mapper = PurchaseOrderDetailMapper()

    @Test
    fun `map should correctly convert PurchaseOrderDetailDTOResponse to PurchaseOrderDetailModel`() {
        // Arrange
        val dtoResponse =
            PurchaseOrderDetailDTOResponse(
                code = "200",
                ref = "REF123",
                message = "Success",
                timestamp = "2025-06-25T14:00:00Z",
                data =
                    PurchaseOrderDataDTO(
                        docNo = "PO001",
                        supplierCode = "SUP001",
                        supplierName = "Supplier A",
                        items =
                            listOf(
                                PurchaseOrderItemDTO(
                                    barcode = "1234567890123",
                                    productName = "Product A",
                                    qty = 10.0,
                                    unitName = "Box",
                                    unitFactor = 1.0,
                                ),
                            ),
                    ),
            )

        // Act
        val model = mapper.map(dtoResponse)

        // Assert
        assertEquals("200", model.code)
        assertEquals("REF123", model.ref)
        assertEquals("Success", model.message)
        assertEquals("2025-06-25T14:00:00Z", model.timestamp)
    }
}
