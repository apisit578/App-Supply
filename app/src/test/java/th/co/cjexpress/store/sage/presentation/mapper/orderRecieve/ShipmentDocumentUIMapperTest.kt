package th.co.cjexpress.store.sage.presentation.mapper.orderRecieve

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDataItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDataModel
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.ShipmentDocumentUIMapper
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class ShipmentDocumentUIMapperTest : BaseUnitTest() {
    private lateinit var mapper: ShipmentDocumentUIMapper

    override fun setUp() {
        mapper = ShipmentDocumentUIMapper()
    }

    @Test
    fun `toUIModel should map all fields correctly`() {
        // Arrange
        val items =
            mutableListOf(
                ShipmentDataItemModel(
                    actualQty = 144,
                    barcode = "8859333800434",
                    isTote = false,
                    toteCode = "TDOA66923",
                    productName = "Shelf talker แลกแต้มแทนเงินสด(10/PAC)",
                    qty = 144,
                    unitName = "ชิ้น",
                    unitFactor = 1,
                ),
            )

        val data =
            ShipmentDataModel(
                items = items,
                createdDate = "2025-05-28T09:07:02.981Z",
                sdNo = "SD2505M04C-000013",
                ldNo = "LD20250517022100",
                count = 1,
                sumOfActualQty = 144,
            )

        // Act
        val result = mapper.toUIModel(data)

        // Assert
        assertEquals("SD2505M04C-000013", result.sdNo)
        assertEquals("LD20250517022100", result.ldNo)
        assertEquals("2025-05-28T09:07:02.981Z", result.createdDate)
        assertEquals(1, result.count)
        assertEquals(144, result.sumOfActualQty)
        assertEquals(1, result.items.size)

        val item = result.items.first()
        assertEquals("8859333800434", item.barcode)
        assertEquals("TDOA66923", item.toteCode)
        assertEquals("Shelf talker แลกแต้มแทนเงินสด(10/PAC)", item.productName)
        assertFalse(item.isTote)
        assertEquals(144, item.qty)
        assertEquals(144, item.actualQty)
        assertEquals("ชิ้น", item.unitName)
        assertEquals(1, item.unitFactor)
    }

    @Test
    fun `toUIModel should return empty items when input list is empty`() {
        // Arrange
        val data =
            ShipmentDataModel(
                items = mutableListOf(),
                createdDate = "30/01/2568",
                sdNo = "SD002",
                ldNo = "LD002",
                count = 0,
                sumOfActualQty = 0,
            )

        // Act
        val result = mapper.toUIModel(data)

        // Assert
        assertEquals(0, result.items.size)
        assertEquals("SD002", result.sdNo)
    }
}
