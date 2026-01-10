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
import th.co.cjexpress.store.sage.constants.ErrorCodes.OrderReceiveErrorCodes
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class CheckItemQtyMoreRefQtyUseCaseTest {
    @RelaxedMockK
    lateinit var menuOrderReceiveUseCase: MenuOrderReceiveUseCase

    @RelaxedMockK
    lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils
    private lateinit var useCase: CheckItemQtyMoreRefQtyUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        useCase =
            CheckItemQtyMoreRefQtyUseCase(
                menuOrderReceiveUseCase = menuOrderReceiveUseCase,
                shipmentDocumentUseCase = shipmentDocumentUseCase,
                resourceUtils = resourceUtils,
            )
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `no summary Success`() {
        every { shipmentDocumentUseCase.searchShipmentData("X") } returns null
        val result = useCase.execute("X")
        assertTrue(result is Result.Error)
    }

    @Test
    fun `summary not over Success`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 5,
                qty = 10,
                isTote = false,
                barcode = "B",
                toteCode = "",
                productName = "P",
                unitName = "U",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("B") } returns summary

        val result = useCase.execute("B")
        assertTrue(result is Result.Success)
    }

    @Test
    fun `summary over and not tote Error CHECK_ALLOW_ITEM`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 11,
                qty = 10,
                isTote = false,
                barcode = "B",
                toteCode = "",
                productName = "P",
                unitName = "U",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("B") } returns summary
        every { menuOrderReceiveUseCase.allowOverReceivingQty() } returns false

        val result = useCase.execute("B")
        assertTrue(result is Result.Error)
        assertEquals(
            OrderReceiveErrorCodes.CHECK_ALLOW_ITEM.name,
            (result as Result.Error).cause.errorCode,
        )
        assertEquals(null, result.data)
    }

    @Test
    fun `summary over and allowOverReceivingQty and not tote Error CHECK_ALLOW_ITEM`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 11,
                qty = 10,
                isTote = false,
                barcode = "B",
                toteCode = "",
                productName = "P",
                unitName = "U",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("B") } returns summary
        every { menuOrderReceiveUseCase.allowOverReceivingQty() } returns true

        val result = useCase.execute("B")
        assertTrue(result is Result.Error)
        assertEquals(
            OrderReceiveErrorCodes.CHECK_ALLOW_ITEM.name,
            (result as Result.Error).cause.errorCode,
        )
        assertEquals(summary, result.data)
    }

    @Test
    fun `summary is tote with qty greater than zero - expect Success even if actual over`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 999,
                qty = 10,
                isTote = true,
                barcode = "1234",
                toteCode = "TOTE-1",
                productName = "ProductName",
                unitName = "UnitName",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("1234") } returns summary

        val result = useCase.execute("1234")
        assertTrue(result is Result.Success)
    }

    @Test
    fun `summary over when Mode_ADJUST and not allowOverReceivingQty - expect Error CHECK_ALLOW_ITEM`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 8,
                qty = 10,
                isTote = false,
                barcode = "BAR123",
                toteCode = "",
                productName = "ProductA",
                unitName = "UnitA",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("BAR123") } returns summary
        every { menuOrderReceiveUseCase.allowOverReceivingQty() } returns false
        every { resourceUtils.getString(any()) } returns "Over receive error"

        val result = useCase.execute("BAR123", receiveAdjust = 5, mode = CheckItemQtyMoreRefQtyUseCase.Mode.ADJUST)

        assertTrue(result is Result.Error)
        assertEquals(
            OrderReceiveErrorCodes.CHECK_ALLOW_ITEM.name,
            (result as Result.Error).cause.errorCode,
        )
        assertEquals(null, result.data)
    }

    @Test
    fun `summary over when Mode_ADJUST and allowOverReceivingQty true - expect Error CHECK_ALLOW_ITEM with summary data`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 8,
                qty = 10,
                isTote = false,
                barcode = "BAR456",
                toteCode = "",
                productName = "ProductB",
                unitName = "UnitB",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("BAR456") } returns summary
        every { menuOrderReceiveUseCase.allowOverReceivingQty() } returns true
        every { resourceUtils.getString(any()) } returns "Over receive warning"

        val result = useCase.execute("BAR456", receiveAdjust = 5, mode = CheckItemQtyMoreRefQtyUseCase.Mode.ADJUST)

        assertTrue(result is Result.Error)
        assertEquals(
            OrderReceiveErrorCodes.CHECK_ALLOW_ITEM.name,
            (result as Result.Error).cause.errorCode,
        )
        assertEquals(summary, result.data)
    }

    @Test
    fun `summary not over when Mode_ADJUST - expect Success`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 3,
                qty = 10,
                isTote = false,
                barcode = "BAR789",
                toteCode = "",
                productName = "ProductC",
                unitName = "UnitC",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("BAR789") } returns summary

        val result = useCase.execute("BAR789", receiveAdjust = 2, mode = CheckItemQtyMoreRefQtyUseCase.Mode.ADJUST)

        assertTrue(result is Result.Success)
        assertEquals(summary, (result as Result.Success).data)
    }

    @Test
    fun `summary is equal when Mode_ADJUST - expect Success`() {
        val summary =
            ShipmentDataItemUIModel(
                actualQty = 8,
                qty = 10,
                isTote = false,
                barcode = "BAR789",
                toteCode = "",
                productName = "ProductC",
                unitName = "UnitC",
                unitFactor = 1,
                overQty = 0,
            )
        every { shipmentDocumentUseCase.searchShipmentData("BAR789") } returns summary

        val result = useCase.execute("BAR789", receiveAdjust = 2, mode = CheckItemQtyMoreRefQtyUseCase.Mode.ADJUST)

        assertTrue(result is Result.Success)
        assertEquals(summary, (result as Result.Success).data)
    }
}
