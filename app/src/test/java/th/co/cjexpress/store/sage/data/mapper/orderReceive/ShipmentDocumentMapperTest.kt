package th.co.cjexpress.store.sage.data.mapper.orderReceive

import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers.isA
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentResponse
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentResponseData
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentResponseDataItem
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class ShipmentDocumentMapperTest : BaseUnitTest() {
    private lateinit var mapper: ShipmentDocumentMapper

    @Before
    override fun setUp() {
        super.setUp()
        mapper = ShipmentDocumentMapper()
    }

    @After
    override fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_document_sd_mapper() {
        // given
        val respMock = mockk<ShipmentDocumentResponse>(relaxed = true)
        // when
        val result = mapper.map(respMock)
        // then
        MatcherAssert.assertThat(result, isA(ShipmentDocumentModel::class.java))
    }

    @Test
    fun `map with null data should fallback to default values`() {
        val response =
            ShipmentDocumentResponse(
                ref = "ref0",
                code = "200",
                message = "No data",
                timestamp = "",
                data = null,
            )

        val result = mapper.map(response)

        assertEquals("", result.data.sdNo)
        assertEquals(0, result.data.count)
        assertTrue(result.data.items.isEmpty())
    }

    @Test
    fun `map with null items should return empty list`() {
        val response =
            ShipmentDocumentResponse(
                ref = "ref1",
                code = "200",
                message = "No items",
                timestamp = "",
                data =
                    ShipmentResponseData(
                        sdNo = "SD1",
                        ldNo = "LD1",
                        createdDate = "2025-07-03",
                        count = 1,
                        sumOfActualQty = 10,
                        items = null,
                    ),
            )

        val result = mapper.map(response)

        assertEquals("SD1", result.data.sdNo)
        assertTrue(result.data.items.isEmpty())
    }

    @Test
    fun `map with empty items list should return empty mapped list`() {
        val response =
            ShipmentDocumentResponse(
                ref = "ref2",
                code = "200",
                message = "Empty items",
                timestamp = "",
                data =
                    ShipmentResponseData(
                        sdNo = "SD2",
                        ldNo = "LD2",
                        createdDate = "2025-07-03",
                        count = 0,
                        sumOfActualQty = 0,
                        items = emptyList(),
                    ),
            )

        val result = mapper.map(response)

        assertEquals(0, result.data.count)
        assertTrue(result.data.items.isEmpty())
    }

    @Test
    fun `map with one item should work correctly`() {
        val item =
            ShipmentResponseDataItem(
                barcode = "A",
                qty = 1,
                productName = "P",
                unitName = "U",
                isTote = false,
                toteCode = "T",
                unitFactor = 1,
                actualQty = 1,
            )

        val response =
            ShipmentDocumentResponse(
                ref = "ref3",
                code = "200",
                message = "One item",
                timestamp = "",
                data =
                    ShipmentResponseData(
                        sdNo = "SD3",
                        ldNo = "LD3",
                        createdDate = "2025-07-03",
                        count = 1,
                        sumOfActualQty = 1,
                        items = listOf(item),
                    ),
            )

        val result = mapper.map(response)

        assertEquals(1, result.data.items.size)
        assertEquals(
            "A",
            result.data.items
                .first()
                .barcode,
        )
    }

    @Test
    fun `map with multiple items should call toMutableList`() {
        val item1 =
            ShipmentResponseDataItem(
                barcode = "B1",
                qty = 1,
                productName = "P",
                unitName = "U",
                isTote = false,
                toteCode = "T",
                unitFactor = 1,
                actualQty = 1,
            )
        val item2 =
            ShipmentResponseDataItem(
                barcode = "B2",
                qty = 2,
                productName = "P",
                unitName = "U",
                isTote = false,
                toteCode = "T",
                unitFactor = 1,
                actualQty = 1,
            )

        val response =
            ShipmentDocumentResponse(
                ref = "ref4",
                code = "200",
                message = "Multiple items",
                timestamp = "",
                data =
                    ShipmentResponseData(
                        sdNo = "SD4",
                        ldNo = "LD4",
                        createdDate = "2025-07-03",
                        count = 2,
                        sumOfActualQty = 3,
                        items = listOf(item1, item2),
                    ),
            )

        val result = mapper.map(response)

        assertEquals(2, result.data.items.size)
        assertEquals(
            "B2",
            result.data.items
                .last()
                .barcode,
        )
    }

    @Test
    fun `map with null fields inside item should fallback to default`() {
        val item =
            ShipmentResponseDataItem(
                barcode = null,
                qty = null,
                productName = null,
                unitName = null,
                isTote = null,
                toteCode = null,
                unitFactor = null,
                actualQty = null,
            )

        val response =
            ShipmentDocumentResponse(
                ref = "ref5",
                code = "200",
                message = "Null item fields",
                timestamp = "",
                data =
                    ShipmentResponseData(
                        sdNo = null,
                        ldNo = null,
                        createdDate = null,
                        count = null,
                        sumOfActualQty = null,
                        items = listOf(item),
                    ),
            )

        val result = mapper.map(response)

        assertEquals("", result.data.sdNo)
        val mapped = result.data.items.first()
        assertEquals("", mapped.barcode)
        assertEquals(0, mapped.qty)
    }

    @Test
    fun `map with mixed null and non-null fields should use defaults correctly`() {
        // given
        val response =
            ShipmentDocumentResponse(
                ref = "ref6",
                code = "200",
                message = "Mixed null fields",
                timestamp = "",
                data =
                    ShipmentResponseData(
                        sdNo = "SD6",
                        ldNo = null,
                        createdDate = "2025-07-03",
                        count = null,
                        sumOfActualQty = 50,
                        items = emptyList(),
                    ),
            )

        // when
        val result = mapper.map(response)

        // then
        assertEquals("SD6", result.data.sdNo)
        assertEquals("", result.data.ldNo)
        assertEquals("2025-07-03", result.data.createdDate)
        assertEquals(0, result.data.count)
        assertEquals(50, result.data.sumOfActualQty)
        assertTrue(result.data.items.isEmpty())
    }
}
