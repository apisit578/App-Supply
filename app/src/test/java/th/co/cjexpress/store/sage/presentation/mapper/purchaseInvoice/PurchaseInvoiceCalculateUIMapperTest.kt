package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import junit.framework.TestCase.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateDataModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class PurchaseInvoiceCalculateUIMapperTest : BaseUnitTest() {
    private lateinit var mapper: PurchaseInvoiceCalculateUIMapper

    override fun setUp() {
        mapper = PurchaseInvoiceCalculateUIMapper()
    }

    @Test
    fun `mapUIModel should map domain model to UI model correctly`() {
        // Arrange
        val domainModel =
            PurchaseInvoiceCalculateDataModel(
                itemCount = 5,
                orderedTotalAmount = 10000.0,
                piNo = "PI20250722001",
                poNo = "PO20250722002",
                receivedItemCount = 4,
                receivedTotalAmount = 8000.0,
                supplierCode = "SUP-TEST001",
                totalOrderedQty = 50,
                totalReceivedQty = 40,
            )

        // Act
        val result = mapper.mapUIModel(domainModel)

        // Assert
        assertEquals(5, result.itemCount)
        assertEquals(10000.0, result.orderedTotalAmount, 0.001) // (floating-point precision errors 0.001)
        assertEquals("PI20250722001", result.piNo)
        assertEquals("PO20250722002", result.poNo)
        assertEquals(4, result.receivedItemCount)
        assertEquals(8000.0, result.receivedTotalAmount, 0.001) // (floating-point precision errors 0.001)
        assertEquals("SUP-TEST001", result.supplierCode)
        assertEquals(50, result.totalOrderedQty)
        assertEquals(40, result.totalReceivedQty)
    }
}
