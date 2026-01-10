package th.co.cjexpress.store.sage.presentation.mapper.orderReceiveTote

import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel

class SdToteDiscountsConfigUIMapperTest {
    private lateinit var mapper: SdToteDiscountsConfigUIMapper

    @Before
    fun setUp() {
        mapper = SdToteDiscountsConfigUIMapper()
    }

    @Test
    fun `toUIModel should map fields correctly`() {
        // given
        val model =
            SdToteDiscountsConfigModel(
                barcode = "1234567890123",
                discountType = "DISC10",
                noOfExp = 3,
                skuName = "Test Product",
                unitName = "Test Unit",
                skuCode = "9876543210",
            )

        // when
        val result = mapper.toUIModel(model)

        // then
        assertEquals("1234567890123", result.barcode)
        assertEquals("DISC10", result.discountType)
        assertEquals(3, result.noOfExp)
    }
}
