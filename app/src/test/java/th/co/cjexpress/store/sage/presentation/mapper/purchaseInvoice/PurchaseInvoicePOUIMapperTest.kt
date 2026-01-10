package th.co.cjexpress.store.sage.presentation.mapper.purchaseInvoice

import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentItemModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierPOUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class PurchaseInvoicePOUIMapperTest : BaseUnitTest() {
    private lateinit var purchaseInvoicePOUIMapper: PurchaseInvoicePOUIMapper

    override fun setUp() {
        purchaseInvoicePOUIMapper = PurchaseInvoicePOUIMapper()
    }

    @Test
    fun `mapPOUIModel mapping test`() =
        runTest {
            // Arrange
            val result: List<PurchaseInvoiceSupplierPOUIModel>
            val mockPurchaseOrderDocumentItemList =
                listOf(
                    PurchaseOrderDocumentItemModel(
                        docNo = "2224082904",
                        shipmentDate = "27/08/2024",
                        grandTotalAmount = 61229.07,
                    ),
                    PurchaseOrderDocumentItemModel(
                        docNo = "4002005766",
                        shipmentDate = "11/11/2024",
                        grandTotalAmount = 0.0,
                    ),
                    PurchaseOrderDocumentItemModel(
                        docNo = "1111011102",
                        shipmentDate = "26/03/2025",
                        grandTotalAmount = 61229.07,
                    ),
                )

            val expectedResult =
                listOf(
                    PurchaseInvoiceSupplierPOUIModel(
                        docNo = "2224082904",
                        shipmentDate = "27/08/2024",
                        grandTotalAmountText = "61,229.07",
                    ),
                    PurchaseInvoiceSupplierPOUIModel(
                        docNo = "4002005766",
                        shipmentDate = "11/11/2024",
                        grandTotalAmountText = "0.00",
                    ),
                    PurchaseInvoiceSupplierPOUIModel(
                        docNo = "1111011102",
                        shipmentDate = "26/03/2025",
                        grandTotalAmountText = "61,229.07",
                    ),
                )

            // Act
            result =
                purchaseInvoicePOUIMapper.mapPOUIModel(
                    purchaseOrderDocumentItemModel = mockPurchaseOrderDocumentItemList,
                )

            // Assert
            Assert.assertEquals(expectedResult, result)
        }
}
