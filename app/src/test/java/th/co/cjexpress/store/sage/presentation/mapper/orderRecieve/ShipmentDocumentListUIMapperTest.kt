package th.co.cjexpress.store.sage.presentation.mapper.orderRecieve

import junit.framework.TestCase.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.presentation.mapper.orderReceive.ShipmentDocumentListUIMapper
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import java.util.Calendar

class ShipmentDocumentListUIMapperTest : BaseUnitTest() {
    private lateinit var mapper: ShipmentDocumentListUIMapper

    override fun setUp() {
        mapper = ShipmentDocumentListUIMapper()
    }

    @Test
    fun `toUIModel should map document list correctly with Date`() {
        // Arrange
        val calendar =
            Calendar.getInstance().apply {
                set(2025, Calendar.JULY, 2)
            }

        val createdDate = calendar.time

        val input =
            DocumentSDModel(
                code = "SUCCESS",
                message = "ok",
                data =
                    listOf(
                        DocumentSDDataModel(
                            createdDate = createdDate,
                            sdNo = "SDTESTLOVE1234",
                            ldNo = "LDTESTLOVE~!@#",
                        ),
                    ),
            )

        // Act
        val result = mapper.toUIModel(input)

        // Assert
        assertEquals("1", result.documentListSize)
        assertEquals(1, result.shipmentDocumentList.size)

        val item = result.shipmentDocumentList.first()
        assertEquals("02/07/2025", item.date)
        assertEquals("SDTESTLOVE1234", item.sdNo)
        assertEquals("LDTESTLOVE~!@#", item.ldNo)
    }
}
