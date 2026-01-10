package th.co.cjexpress.store.sage.presentation.mapper.orderRecieve

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentByBarcodeData
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentByBarcodeDataItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.ShipmentDocumentByBarcodeUIMapper
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class ShipmentDocumentByBarcodeUIMapperTest : BaseUnitTest() {
    private lateinit var mapper: ShipmentDocumentByBarcodeUIMapper

    override fun setUp() {
        mapper = ShipmentDocumentByBarcodeUIMapper()
    }

    @Test
    fun `toUIModel should map all fields correctly from ShipmentDocumentByBarcodeModel`() {
        // Arrange
        val mockData =
            ShipmentDocumentByBarcodeModel(
                ref = "bddadbd3cb3690055d87879ae2167acc",
                code = "SUCCESS",
                message = "SUCCESS",
                data =
                    ShipmentByBarcodeData(
                        sdNo = "SD2505M04C-000013",
                        count = 1,
                        sumOfActualQty = 2,
                        items =
                            listOf(
                                ShipmentByBarcodeDataItemModel(
                                    barcode = "8850092310509",
                                    toteCode = "8850092310509",
                                    productName = "ไฮยีนปรับผ้าเข้มข้นดิลิเชียสชมพู480ml Piece",
                                    isTote = false,
                                    qty = 0,
                                    actualQty = 2,
                                    unitName = "ชิ้น",
                                    unitFactor = 1,
                                ),
                            ),
                    ),
                timestamp = "",
            )

        // Act
        val result = mapper.toUIModel(mockData)

        // Assert
        assertEquals("SD2505M04C-000013", result.sdNo)
        assertEquals("", result.ldNo)
        assertEquals("", result.createdDate)
        assertEquals(1, result.count)
        assertEquals(2, result.sumOfActualQty)

        assertEquals(1, result.items.size)
        val item = result.items.first()
        assertEquals(2, item.actualQty)
        assertEquals("8850092310509", item.barcode)
        assertFalse(item.isTote)
        assertEquals("ไฮยีนปรับผ้าเข้มข้นดิลิเชียสชมพู480ml Piece", item.productName)
        assertEquals(0, item.qty)
        assertEquals("8850092310509", item.toteCode)
        assertEquals(1, item.unitFactor)
        assertEquals("ชิ้น", item.unitName)
    }

    @Test
    fun `toUIModel should handle empty item list`() {
        // Arrange
        val dataModel =
            ShipmentDocumentByBarcodeModel(
                code = "ERROR",
                message = "No Items kub",
                ref = "REF0000",
                data =
                    ShipmentByBarcodeData(
                        sdNo = "SD_EMPTY",
                        count = 0,
                        sumOfActualQty = 0,
                        items = emptyList(),
                    ),
                timestamp = "",
            )

        // Act
        val result = mapper.toUIModel(dataModel)

        // Assert
        assertEquals("SD_EMPTY", result.sdNo)
        assertEquals(0, result.items.size)
        assertEquals(0, result.count)
        assertEquals(0, result.sumOfActualQty)
    }
}
