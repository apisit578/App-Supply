package th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class SdToteExpMapperTest {
    private lateinit var mapper: SdToteExpMapper
    private val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)

    @Before
    fun setUp() {
        mapper = SdToteExpMapper()
    }

    @Test
    fun `map should return correct SdToteDetailItemExpiryDateLot from ISdToteExpiryDateModel`() {
        // given
        val testExpiryDateModel =
            TestExpiryDateModel(
                expDate = sdf.parse("2025-12-31"),
                actualQty = 2,
                qty = 5,
                lotNo = "LOT999",
            )

        // when
        val result = mapper.map(testExpiryDateModel)

        // then
        assertEquals(sdf.parse("2025-12-31"), result.expDate)
        assertEquals(2, result.actualQty)
        assertEquals(5, result.qty)
        assertEquals("LOT999", result.lotNo)
    }
}

class TestExpiryDateModel(
    override val expDate: Date?,
    override var actualQty: Int,
    override var qty: Int,
    override var lotNo: String,
) : ISdToteExpiryDateModel(expDate, actualQty, qty, lotNo)
