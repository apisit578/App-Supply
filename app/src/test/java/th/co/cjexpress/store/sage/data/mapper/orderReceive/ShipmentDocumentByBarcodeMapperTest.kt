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
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcode
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeItem
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentDocumentByBarcodeResponse
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class ShipmentDocumentByBarcodeMapperTest : BaseUnitTest() {
    private lateinit var shipmentDocumentByBarcodeMapper: ShipmentDocumentByBarcodeMapper

    @Before
    override fun setUp() {
        shipmentDocumentByBarcodeMapper = ShipmentDocumentByBarcodeMapper()
    }

    @After
    override fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_shipment_by_barcode_data_mapper() {
        // given
        val respMock = mockk<ShipmentDocumentByBarcodeResponse>(relaxed = true)
        // when
        val result = shipmentDocumentByBarcodeMapper.map(respMock)
        // then
        MatcherAssert.assertThat(result, isA(ShipmentDocumentByBarcodeModel::class.java))
    }

    @Test
    fun test_shipment_by_barcode_data_empty_data_list() {
        // given
        val response =
            ShipmentDocumentByBarcodeResponse(
                ref = "123",
                code = "200",
                message = "No Content",
                timestamp = "",
                data =
                    ShipmentDocumentByBarcode(
                        count = 10,
                        sdNo = "123456",
                        sumOfActualQty = 19,
                        items = emptyList(),
                    ),
            )
        // when
        val result = shipmentDocumentByBarcodeMapper.map(response)
        // then
        MatcherAssert.assertThat(result, isA(ShipmentDocumentByBarcodeModel::class.java))
        assertEquals(response.code, result.code)
        assertEquals(response.message, result.message)
        assertTrue(result.data.items.isEmpty())
    }

    @Test
    fun test_shipment_by_barcode_with_null_data() {
        // given
        val response =
            ShipmentDocumentByBarcodeResponse(
                ref = "123",
                code = "200",
                message = "No Content",
                timestamp = "",
                data =
                    ShipmentDocumentByBarcode(
                        count = 10,
                        sdNo = "123456",
                        sumOfActualQty = 19,
                        items = emptyList(),
                    ),
            )
        // when
        val result = shipmentDocumentByBarcodeMapper.map(response)
        // then
        MatcherAssert.assertThat(result, isA(ShipmentDocumentByBarcodeModel::class.java))
        assertEquals(response.code, result.code)
        assertEquals(response.message, result.message)
        assertTrue(result.data.items.isEmpty())
    }

    @Test
    fun test_map_data_with_null_fields_and_item() {
        val response =
            ShipmentDocumentByBarcode(
                count = 0,
                sdNo = "",
                sumOfActualQty = 0,
                items = emptyList(),
            )

        val result = shipmentDocumentByBarcodeMapper.mapData(response)

        assertEquals(0, result.count)
        assertEquals("", result.sdNo)
        assertEquals(0, result.sumOfActualQty)
        assertTrue(result.items.isEmpty())
    }

    @Test
    fun test_shipment_by_barcode_mapper_with_data() {
        // given

        val response =
            ShipmentDocumentByBarcodeResponse(
                ref = "123",
                code = "200",
                message = "No Content",
                timestamp = "",
                data =
                    ShipmentDocumentByBarcode(
                        count = 10,
                        sdNo = "123456",
                        sumOfActualQty = 19,
                        items =
                            mutableListOf(
                                ShipmentDocumentByBarcodeItem(
                                    actualQty = 10,
                                    barcode = "123456",
                                    isTote = true,
                                    productName = "ProductTest",
                                    qty = 10,
                                    toteCode = "123456",
                                    unitFactor = 10,
                                    unitName = "UnitNameTest",
                                ),
                            ),
                    ),
            )
        // when
        val result = shipmentDocumentByBarcodeMapper.map(response)
        val resultData = shipmentDocumentByBarcodeMapper.mapData(response.data)
        // then
        MatcherAssert.assertThat(result, isA(ShipmentDocumentByBarcodeModel::class.java))
        assertEquals(response.code, result.code)
        assertEquals(response.message, result.message)
        assertEquals(1, result.data.items.size)
        assertEquals(response.data.sdNo, result.data.sdNo)
        assertEquals(response.data.count ?: 0, result.data.count)
        assertEquals(response.data.sumOfActualQty, result.data.sumOfActualQty)
        assertEquals(response.data.items[0].barcode, resultData.items[0].barcode)
        assertEquals(response.data.items[0].actualQty, resultData.items[0].actualQty)
        assertEquals(response.data.items[0].qty, resultData.items[0].qty)
        assertEquals(response.data.items[0].isTote, resultData.items[0].isTote)
    }
}
