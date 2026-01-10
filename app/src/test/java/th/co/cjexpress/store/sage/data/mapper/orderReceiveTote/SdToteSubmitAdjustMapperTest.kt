package th.co.cjexpress.store.sage.data.mapper.orderReceiveTote

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.ExpListItemResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustDataResponse
import th.co.cjexpress.store.sage.data.model.orderReceiveTote.SdToteSubmitAdjustResponse
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import java.util.Date

class SdToteSubmitAdjustMapperTest : BaseUnitTest() {
    private lateinit var mapper: SdToteSubmitAdjustMapper

    @Before
    override fun setUp() {
        mapper = SdToteSubmitAdjustMapper()
    }

    @Test
    fun `should map valid response with expList correctly`() {
        // Arrange
        val d1 = Date(1735689600000L) // 2025-01-01 00:00:00 GMT
        val d2 = Date(1735776000000L) // 2025-01-02 00:00:00 GMT

        val response =
            SdToteSubmitAdjustResponse(
                code = "200",
                message = "SUCCESS",
                ref = "",
                timestamp = "",
                data =
                    SdToteSubmitAdjustDataResponse(
                        sdNo = "SD123",
                        qrNo = "QR999",
                        expList =
                            listOf(
                                ExpListItemResponse(
                                    expDate = d1,
                                    mfgDate = d2,
                                    qty = 10,
                                    actualQty = 8,
                                    lotNo = "L111",
                                ),
                                ExpListItemResponse(
                                    expDate = d2,
                                    mfgDate = d1,
                                    qty = 5,
                                    actualQty = 5,
                                    lotNo = "L222",
                                ),
                            ),
                    ),
            )

        // Act
        val result = mapper.map(response)

        // Assert
        assertEquals("200", result.code)
        assertEquals("SUCCESS", result.message)
        assertNotNull(result.data)

        val data = result.data
        assertEquals("SD123", data?.sdNo)
        assertEquals("QR999", data?.qrNo)
        assertEquals(2, data?.expList?.size)

        // item[0]
        data?.expList[0].let { item ->
            assertEquals(d1, item?.expDate)
            assertEquals(d2, item?.mfgDate)
            assertEquals(10, item?.qty)
            assertEquals(8, item?.actualQty)
            assertEquals("L111", item?.lotNo)
        }

        // item[1]
        data?.expList[1].let { item ->
            assertEquals(d2, item?.expDate)
            assertEquals(d1, item?.mfgDate)
            assertEquals(5, item?.qty)
            assertEquals(5, item?.actualQty)
            assertEquals("L222", item?.lotNo)
        }
    }

    @Test
    fun `should map response with empty expList`() {
        // Arrange
        val response =
            SdToteSubmitAdjustResponse(
                code = "200",
                message = "SUCCESS",
                ref = "",
                timestamp = "",
                data =
                    SdToteSubmitAdjustDataResponse(
                        sdNo = "SD123",
                        qrNo = "QR000",
                        expList = emptyList(),
                    ),
            )

        // Act
        val result = mapper.map(response)

        // Assert
        assertEquals("200", result.code)
        assertEquals("SUCCESS", result.message)
        assertNotNull(result.data)
        assertEquals("SD123", result.data?.sdNo)
        assertEquals("QR000", result.data?.qrNo)
    }
}
