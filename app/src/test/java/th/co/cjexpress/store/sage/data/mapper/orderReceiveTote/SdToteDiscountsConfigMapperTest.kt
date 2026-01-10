package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteDiscountsConfigResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteDiscountsConfigResponseData
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class SdToteDiscountsConfigMapperTest : BaseUnitTest() {
    private lateinit var mapper: SdToteDiscountsConfigMapper

    @Before
    override fun setUp() {
        mapper = SdToteDiscountsConfigMapper()
    }

    @Test
    fun `should map valid response with data correctly`() {
        // Arrange
        val mockResponse =
            SdToteDiscountsConfigResponse(
                ref = "68303ab6ecd69927c901b382",
                code = "200",
                message = "SUCCESS",
                timestamp = "",
                data =
                    listOf(
                        SdToteDiscountsConfigResponseData(
                            barcode = "1234567890123",
                            discountType = "NORMAL",
                            noOfExp = 2,
                            skuName = "Test Product",
                            unitName = "Test Unit",
                            skuCode = "SKU1234567890",
                        ),
                    ),
            )

        // Act
        val result = mapper.map(mockResponse)

        // Assert
        assertEquals("200", result.code)
        assertEquals("SUCCESS", result.message)
        assertNotNull(result.data)
        assertEquals(1, result.data!!.size)

        val item = result.data[0]
        assertEquals("1234567890123", item.barcode)
        assertEquals("NORMAL", item.discountType)
        assertEquals(2, item.noOfExp)
    }

    @Test
    fun `should return empty list when data is null`() {
        // Arrange
        val mockResponse =
            SdToteDiscountsConfigResponse(
                ref = "ref-null",
                code = "204",
                message = "No Content",
                timestamp = "",
                data = emptyList(),
            )

        // Act
        val result = mapper.map(mockResponse)

        // Assert
        assertEquals("204", result.code)
        assertEquals("No Content", result.message)
        assertNotNull(result.data)
        assertTrue(result.data!!.isEmpty())
    }

    @Test
    fun `should return empty list when data is empty`() {
        // Arrange
        val mockResponse =
            SdToteDiscountsConfigResponse(
                ref = "ref-empty",
                code = "200",
                timestamp = "",
                message = "Empty",
                data = emptyList(),
            )

        // Act
        val result = mapper.map(mockResponse)

        // Assert
        assertEquals("200", result.code)
        assertEquals("Empty", result.message)
        assertNotNull(result.data)
        assertTrue(result.data!!.isEmpty())
    }
}
