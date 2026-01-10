package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertTrue
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckProductInDocumentUseCaseTest {
    @RelaxedMockK
    lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils
    private lateinit var useCase: CheckProductInDocumentUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase = CheckProductInDocumentUseCase(shipmentDocumentUseCase, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `found in summary and qty is not 0 should Success`() {
        // given
        every { shipmentDocumentUseCase.searchShipmentData("B") } returns dummyItem()

        // when
        val result = useCase.execute("B")

        // then
        assertTrue(result is Result.Success)
    }

    @Test
    fun `found in summary and qty is 0 should Error`() {
        // given
        every { shipmentDocumentUseCase.searchShipmentData("B") } returns dummyItem().copy(qty = 0)

        // when
        val result = useCase.execute("B")

        // then
        assertTrue(result is Result.Error)
    }

    @Test
    fun `not found  Error with title text_not_found_data`() {
        // given
        every { shipmentDocumentUseCase.searchShipmentData("X") } returns null
        every { resourceUtils.getString(R.string.text_not_found_data) } returns "NotFound"

        // when
        val result = useCase.execute("X")

        // then
        assertTrue(result is Result.Error)
        assertEquals("NotFound", (result as Result.Error).cause.title)
    }

    private fun dummyItem() =
        ShipmentDataItemUIModel(
            barcode = "B",
            actualQty = 0,
            isTote = false,
            productName = "P",
            qty = 1,
            toteCode = "",
            unitFactor = 1,
            unitName = "U",
            overQty = 0,
        )
}
