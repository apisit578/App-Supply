package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.every
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckLimitBatchUseCaseTest {
    private lateinit var menuOrderReceiveUseCase: MenuOrderReceiveUseCase
    private lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase
    private lateinit var resourceUtils: ResourceUtils
    private lateinit var useCase: CheckLimitBatchUseCase

    @Before
    fun setUp() {
        menuOrderReceiveUseCase = mockk()
        shipmentDocumentUseCase = mockk()
        resourceUtils = mockk()
        every { resourceUtils.getString(R.string.text_title_over_max_qty) } returns "ExtraItem"
        useCase =
            CheckLimitBatchUseCase(menuOrderReceiveUseCase, shipmentDocumentUseCase, resourceUtils)
    }

    @Test
    fun `execute returns Success when maxBatchQty is 0`() {
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 0
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 10

        val result = useCase.execute(barcode = barcode)

        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `execute returns Success when summaryBatchQty less than maxBatchQty`() {
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 10
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 5

        val result = useCase.execute(barcode = barcode)

        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `execute returns Error when summaryBatchQty equals maxBatchQty`() {
        // Given
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 10
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 10
        every { resourceUtils.getString(R.string.text_title_over_max_qty) } returns "ExtraItem"
        every {
            resourceUtils.getString(
                R.string.text_detail_over_max_qty,
                10,
            )
        } returns "10 Unit/Tote"

        // When
        val result = useCase.execute(barcode = barcode)

        // Then
        assertTrue(result is Result.Error)
    }

    @Test
    fun `execute returns Error when summaryBatchQty greater than maxBatchQty`() {
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 10
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 15
        every { resourceUtils.getString(R.string.text_title_over_max_qty) } returns "ExtraItem"
        every {
            resourceUtils.getString(
                R.string.text_detail_over_max_qty,
                10,
            )
        } returns "10 Unit/Tote"

        val result = useCase.execute(barcode = barcode)

        assertTrue(result is Result.Error)
    }

    @Test
    fun `execute returns Error when barcode is blank`() {
        val barcode = ""
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 10
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 5
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
        every { resourceUtils.getString(R.string.text_please_scan_barcode) } returns "Please scan barcode"

        val result = useCase.execute(barcode = barcode)

        assertTrue(result is Result.Error)
        val err = (result as Result.Error).cause as BusinessException
        assertEquals(ErrorType.WARNING, err.type)
        assertEquals("Unable to continue", err.title)
        assertEquals("Please scan barcode", err.message)
    }

    @Test
    fun `execute returns Success in SUMMARY_PLUS_ADJUST when total less than max`() {
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 10
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 5

        val result = useCase.execute(
            barcode = barcode,
            mode = CheckLimitBatchUseCase.Mode.ADJUST,
            receiveAdjust = 3
        )

        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `execute returns Success in SUMMARY_PLUS_ADJUST when total equals max`() {
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 10
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 7

        val result = useCase.execute(
            barcode = barcode,
            mode = CheckLimitBatchUseCase.Mode.ADJUST,
            receiveAdjust = 3
        )

        assertEquals(Result.Success(Unit), result)
    }

    @Test
    fun `execute returns Error in SUMMARY_PLUS_ADJUST when total greater than max`() {
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 10
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 9
        every { resourceUtils.getString(R.string.text_title_over_max_qty) } returns "ExtraItem"
        every {
            resourceUtils.getString(R.string.text_detail_over_max_qty, 10)
        } returns "10 Unit/Tote"

        val result = useCase.execute(
            barcode = barcode,
            mode = CheckLimitBatchUseCase.Mode.ADJUST,
            receiveAdjust = 5
        )

        assertTrue(result is Result.Error)
    }

    @Test
    fun `execute returns Success in SUMMARY_PLUS_ADJUST when maxBatchQty is 0`() {
        val barcode = "1234"
        every { menuOrderReceiveUseCase.maxBatchQty() } returns 0
        every { shipmentDocumentUseCase.getSummaryBatchQty() } returns 50

        val result = useCase.execute(
            barcode = barcode,
            mode = CheckLimitBatchUseCase.Mode.ADJUST,
            receiveAdjust = 999
        )

        assertEquals(Result.Success(Unit), result)
    }
}
