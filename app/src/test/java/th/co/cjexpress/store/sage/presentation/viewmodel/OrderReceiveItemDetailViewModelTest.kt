package th.co.cjexpress.store.sage.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckItemQtyMoreRefQtyUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckLimitBatchUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentByBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemDetailViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class OrderReceiveItemDetailViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    @RelaxedMockK
    lateinit var shipmentDocumentByBarcodeUseCase: ShipmentDocumentByBarcodeUseCase

    @RelaxedMockK
    lateinit var checkLimitBatchUseCase: CheckLimitBatchUseCase

    @RelaxedMockK
    lateinit var checkItemQtyMoreRefQtyUseCase: CheckItemQtyMoreRefQtyUseCase

            @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    private lateinit var viewModel: OrderReceiveItemDetailViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            OrderReceiveItemDetailViewModel(
                resourceUtils = resourceUtils,
                shipmentDocumentUseCase = shipmentDocumentUseCase,
                shipmentDocumentByBarcodeUseCase = shipmentDocumentByBarcodeUseCase,
                checkLimitBatchUseCase = checkLimitBatchUseCase,
                checkItemQtyMoreRefQtyUseCase = checkItemQtyMoreRefQtyUseCase,
                uiModelMapper = uiModelMapper,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun `shipmentDocumentItemDetailLiveData getter should return item from usecase`() {
        val newItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
        every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns newItem
        assertEquals(newItem, viewModel.shipmentDocumentItemDetailLiveData)
    }

    @Test
    fun `promptBeforeBack should show QUESTION dialog with correct properties`() =
        runTest {
            every { resourceUtils.getString(R.string.text_confirm_exit) } returns "Confirm exit"
            every { resourceUtils.getString(R.string.text_confirm_leave_page) } returns "Leave page?"

            viewModel.promptBeforeBack()

            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(ErrorType.QUESTION, dialog.type)
                assertEquals("Confirm exit", dialog.titleText)
                assertEquals("Leave page?", dialog.messageText)
                assertEquals(R.string.text_yes, dialog.positiveTextRes)
                assertEquals(R.string.text_no, dialog.negativeTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeBack negative click should reset to Idle`() =
        runTest {
            every { resourceUtils.getString(any()) } returns ""

            viewModel.promptBeforeBack()

            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                dialog.onNegativeClick?.invoke()
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeBack positive click should remove detail and navigate`() =
        runTest {
            every { resourceUtils.getString(any()) } returns ""

            viewModel.promptBeforeBack()

            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                dialog.onPositiveClick?.invoke()

                // เรียก usecase ลบ detail
                verify(exactly = 1) { shipmentDocumentUseCase.removeShipmentDocumentItemDetail() }

                // ถัดไปต้องเป็น Success -> NavToItemsScreen
                val nav = awaitItem()
                assertTrue(nav is BaseScreenState.Success<*>)
                assertEquals(
                    OrderReceiveItemDetailViewModel.UIState.NavToItemsScreen,
                    (nav as BaseScreenState.Success<*>).data,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `setInputActualQtyState should update state`() {
        viewModel.setInputActualQtyState(12)
        assertEquals(12, viewModel.inputActualQtyState.value)
    }

    @Test
    fun `submitQrDocument should show error dialog when input actual QTY equal zero`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            viewModel.setInputActualQtyState(0)

            every { resourceUtils.getString(any()) } returns "fallback"

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(ErrorType.WARNING, dialog.type)
                assertEquals("fallback", dialog.titleText)
                dialog.onPositiveClick?.invoke()
                val idleState = awaitItem()
                assertTrue(idleState is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should show error dialog when limit check fails`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            val errorCause =
                BusinessException(
                    errorCode = "400",
                    type = ErrorType.WARNING,
                    message = null,
                )
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns
                Result.Error(
                    cause = errorCause,
                )
            every { resourceUtils.getString(any()) } returns "fallback"

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(ErrorType.WARNING, dialog.type)
                assertEquals("fallback", dialog.titleText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleCheckLimitBatchError should fallback title and message when both are null`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem

            every { resourceUtils.getString(any()) } returns "fallback"

            val errorCause =
                BusinessException(
                    errorCode = "400",
                    type = ErrorType.ERROR,
                    title = null,
                    message = null,
                )
            every {
                checkLimitBatchUseCase.execute(
                    any(),
                    any(),
                    any()
                )
            } returns Result.Error(cause = errorCause)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val state = awaitItem() as BaseScreenState.Error
                val dialog = state.errorData
                assertEquals("fallback", dialog.titleText)
                assertEquals("fallback", dialog.messageText)
                dialog.onPositiveClick?.invoke()
                assertTrue(awaitItem() is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should show error dialog when limit check fails and click positive button`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            val errorCause =
                BusinessException(
                    errorCode = "400",
                    type = ErrorType.ERROR,
                    title = "title",
                    message = "message",
                )
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns
                Result.Error(
                    cause = errorCause,
                )
            viewModel.setInputActualQtyState(1)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(ErrorType.ERROR, dialog.type)
                assertEquals("title", dialog.titleText)
                assertEquals("message", dialog.messageText)
                dialog.onPositiveClick?.invoke()
                val idleState = awaitItem()
                assertTrue(idleState is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should update summary and batch then navigate when success`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            val updatedModel = mockk<ShipmentDocumentUIModel>(relaxed = true)
            val resultModel = mockk<ShipmentDocumentByBarcodeModel>()
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                mockk {
                    every { sdNo } returns "sdNo"
                }
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(resultModel) } returns updatedModel
            every { checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any()) } returns
                    Result.Success(
                        mockk<ShipmentDataItemUIModel>()
                    )
            every { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    Result.Success(
                        resultModel,
                    ),
                )
            viewModel.setInputActualQtyState(1)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val success = awaitItem()
                assertTrue(success is BaseScreenState.Success<*>)
                assertEquals(
                    OrderReceiveItemDetailViewModel.UIState.NavToItemsScreen,
                    (success as BaseScreenState.Success<*>).data,
                )
                verify { shipmentDocumentUseCase.updateShipmentSummary(updatedModel) }
                verify {
                    shipmentDocumentUseCase.updateShipmentBatchFromReceiveAdjust(
                        "123",
                        any(),
                    )
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should show error dialog when usecase execute returns error`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                mockk {
                    every { sdNo } returns "sdNo"
                }
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            every { checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any()) } returns
                    Result.Success(
                        mockk<ShipmentDataItemUIModel>()
                    )
            val errorCause =
                BusinessException(
                    errorCode = "400",
                    type = ErrorType.ERROR,
                    message = null,
                )
            every { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    Result.Error(cause = errorCause, canRetry = true),
                )
            every { resourceUtils.getString(any()) } returns "fallback"
            viewModel.setInputActualQtyState(1)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val error = awaitItem()
                assertTrue(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData
                assertEquals(ErrorType.ERROR, dialog.type)
                assertEquals("fallback", dialog.titleText)
                assertEquals(R.string.text_retry, dialog.positiveTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should show warning dialog when usecase execute returns error`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                mockk {
                    every { sdNo } returns "sdNo"
                }
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            every { checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any()) } returns
                    Result.Success(
                        mockk<ShipmentDataItemUIModel>()
                    )
            val errorCause =
                BusinessException(
                    errorCode = "400",
                    type = ErrorType.WARNING,
                    message = null,
                )
            every { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    Result.Error(cause = errorCause),
                )
            every { resourceUtils.getString(any()) } returns "fallback"

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val error = awaitItem()
                assertTrue(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData
                assertEquals(ErrorType.WARNING, dialog.type)
                assertFalse(dialog.showF1Message)
                assertEquals("fallback", dialog.titleText)
                assertEquals(R.string.text_close, dialog.positiveTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should show error dialog when usecase execute returns error and then click negative button`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                mockk {
                    every { sdNo } returns "sdNo"
                }
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            every { checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any()) } returns
                    Result.Success(
                        mockk<ShipmentDataItemUIModel>()
                    )
            val errorCause =
                BusinessException(
                    errorCode = "400",
                    type = ErrorType.ERROR,
                    title = "title",
                    message = "message",
                )
            every { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    Result.Error(cause = errorCause, canRetry = true),
                )
            every { resourceUtils.getString(any()) } returns "fallback"
            viewModel.setInputActualQtyState(1)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val error = awaitItem()
                assertTrue(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData
                assertEquals(ErrorType.ERROR, dialog.type)
                assertEquals("title", dialog.titleText)
                assertEquals("message", dialog.messageText)
                assertTrue(dialog.showF1Message)
                dialog.onNegativeClick?.invoke()
                val idleState = awaitItem()
                assertTrue(idleState is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should return immediately when searchShipmentData returns null`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns null

            viewModel.submitQrDocument()

            // ไม่มี state ใหม่ออกมา
            viewModel.baseScreenState.test {
                val error = awaitItem()
                assertTrue(error is BaseScreenState.Idle)
            }
        }

    @Test
    fun `submitQrDocument should show error dialog when exception thrown`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                mockk {
                    every { sdNo } returns "sdNo"
                }
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            every { checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any()) } returns
                    Result.Success(
                        mockk<ShipmentDataItemUIModel>()
                    )
            every { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flow {
                    throw Exception()
                }
            every { resourceUtils.getString(any()) } returns "fallback"
            viewModel.setInputActualQtyState(1)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val error = awaitItem()
                assertTrue(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData
                assertEquals(ErrorType.ERROR, dialog.type)
                assertEquals("fallback", dialog.titleText)
                assertTrue(dialog.showF1Message)
                dialog.onPositiveClick?.invoke()
                val idleState = awaitItem()
                assertTrue(idleState is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should show error dialog and message form error when exception thrown`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                mockk {
                    every { sdNo } returns "sdNo"
                }
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            every { checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any()) } returns
                    Result.Success(
                        mockk<ShipmentDataItemUIModel>()
                    )
            every { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flow {
                    throw Exception("Service error")
                }
            every { resourceUtils.getString(any()) } returns "fallback"
            viewModel.setInputActualQtyState(1)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val error = awaitItem()
                assertTrue(error is BaseScreenState.Error)
                val dialog = (error as BaseScreenState.Error).errorData
                assertEquals(ErrorType.ERROR, dialog.type)
                assertEquals("fallback", dialog.titleText)
                assertEquals("Service error", dialog.messageText)
                assertTrue(dialog.showF1Message)
                dialog.onPositiveClick?.invoke()
                val idleState = awaitItem()
                assertTrue(idleState is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should retry when error dialog positive clicked and canRetry is true`() =
        runTest {
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "123"
            every { currentItem.actualQty } returns 5
            val resultModel = mockk<ShipmentDocumentByBarcodeModel>()
            val updatedModel = mockk<ShipmentDocumentUIModel>(relaxed = true)
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                mockk {
                    every { sdNo } returns "sdNo"
                }
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)
            every { checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any()) } returns
                    Result.Success(
                        mockk<ShipmentDataItemUIModel>()
                    )
            every { resourceUtils.getString(any()) } returns "fallback"

            val errorCause =
                BusinessException(
                    errorCode = "400",
                    type = ErrorType.ERROR,
                    message = null,
                )
            every { shipmentDocumentByBarcodeUseCase.execute(any()) } returnsMany
                listOf(
                    flowOf(Result.Error(cause = errorCause, canRetry = true)),
                    flowOf(Result.Success(resultModel)),
                )
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(resultModel) } returns updatedModel
            viewModel.setInputActualQtyState(1)

            viewModel.submitQrDocument()

            viewModel.baseScreenState.test {
                val error = awaitItem() as BaseScreenState.Error
                error.errorData.onPositiveClick?.invoke()

                val idleState = awaitItem()
                assertTrue(idleState is BaseScreenState.Idle)
                val loadState = awaitItem()
                assertTrue(loadState is BaseScreenState.Loading)
                val success = awaitItem()
                assertTrue(success is BaseScreenState.Success<*>)
                assertEquals(
                    OrderReceiveItemDetailViewModel.UIState.NavToItemsScreen,
                    (success as BaseScreenState.Success<*>).data,
                )
                cancelAndIgnoreRemainingEvents()
            }

            verify { shipmentDocumentUseCase.updateShipmentSummary(updatedModel) }
            verify { shipmentDocumentUseCase.updateShipmentBatchFromReceiveAdjust("123", any()) }
        }

    @Test
    fun `submitQrDocument should show error dialog when allowOverReceiveResult is Result_Error`() =
        runTest {
            // arrange
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "ABC123"
            every { currentItem.actualQty } returns 10
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("ABC123") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)

            val errorCause = BusinessException(
                errorCode = "400",
                type = ErrorType.WARNING,
                title = "Over receive not allowed",
                message = "You cannot receive more than reference quantity."
            )
            every {
                checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any())
            } returns Result.Error(
                cause = errorCause,
                data = mockk<ShipmentDataItemUIModel>(relaxed = true)
            )

            every { resourceUtils.getString(any()) } returns "fallback"
            viewModel.setInputActualQtyState(12)

            // act
            viewModel.submitQrDocument()

            // assert
            viewModel.baseScreenState.test {
                val errorState = awaitItem()
                assertTrue(errorState is BaseScreenState.Error)
                val dialog = (errorState as BaseScreenState.Error).errorData
                assertEquals(ErrorType.WARNING, dialog.type)
                assertEquals("Over receive not allowed", dialog.titleText)
                assertEquals(
                    "You cannot receive more than reference quantity.",
                    dialog.messageText
                )
                dialog.onPositiveClick?.invoke()
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument should reset state when allowOverReceiveResult Error has null data`() =
        runTest {
            // arrange
            val currentItem = mockk<ShipmentDataItemUIModel>(relaxed = true)
            every { currentItem.barcode } returns "XYZ987"
            every { currentItem.actualQty } returns 7
            every { shipmentDocumentUseCase.getShipmentDocumentItemDetail() } returns currentItem
            every { shipmentDocumentUseCase.searchShipmentData("XYZ987") } returns currentItem
            every { checkLimitBatchUseCase.execute(any(), any(), any()) } returns Result.Success(Unit)

            val errorCause = BusinessException(
                errorCode = "401",
                type = ErrorType.ERROR,
                title = null,
                message = null
            )
            every {
                checkItemQtyMoreRefQtyUseCase.execute(any(), any(), any())
            } returns Result.Error(
                cause = errorCause,
                data = null
            )

            every { resourceUtils.getString(any()) } returns "fallback"
            viewModel.setInputActualQtyState(8)

            // act
            viewModel.submitQrDocument()

            // assert
            viewModel.baseScreenState.test {
                val errorState = awaitItem()
                assertTrue(errorState is BaseScreenState.Error)
                val dialog = (errorState as BaseScreenState.Error).errorData
                assertEquals(ErrorType.ERROR, dialog.type)
                assertEquals("fallback", dialog.titleText)
                assertEquals("fallback", dialog.messageText)
                dialog.onPositiveClick?.invoke()
                val idleState = awaitItem()
                assertTrue(idleState is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
