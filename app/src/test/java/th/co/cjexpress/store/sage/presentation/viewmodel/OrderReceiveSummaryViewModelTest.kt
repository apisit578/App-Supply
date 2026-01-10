package th.co.cjexpress.store.sage.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType
import th.co.cjexpress.store.sage.analytics.usecase.TrackTimeEventUseCase
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryItemModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.OrderReceiveSummaryUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryCompleteUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.summary.OrderReceiveSummaryState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.summary.SummaryDisplayScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveSummaryViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class OrderReceiveSummaryViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var orderReceiveSummaryUseCase: OrderReceiveSummaryUseCase

    @RelaxedMockK
    lateinit var trackTimeEventUseCase: TrackTimeEventUseCase

    @RelaxedMockK
    lateinit var orderReceiveLiveData: OrderReceiveLiveData

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    // viewModel
    private lateinit var viewModel: OrderReceiveSummaryViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            OrderReceiveSummaryViewModel(
                orderReceiveSummaryUseCase = orderReceiveSummaryUseCase,
                trackTimeEventUseCase = trackTimeEventUseCase,
                orderReceiveLiveData = orderReceiveLiveData,
                uiModelMapper = uiModelMapper,
                dispatcherProvider = TestDispatchers(),
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun `should get summary list when success`() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"
            val mockOrderReceiveSummaryModel =
                OrderReceiveSummaryModel(
                    code = "SUCCESS",
                    data =
                        OrderReceiveSummaryDataModel(
                            createdDate = "",
                            items =
                                listOf(
                                    OrderReceiveSummaryItemModel(
                                        actualQty = 0,
                                        barcode = "SD2505M04C-000038",
                                        diffQty = 0,
                                        isTote = false,
                                        productName = "",
                                        qty = 0,
                                        toteCode = "",
                                        unitName = "test",
                                        unitFactor = 0,
                                    ),
                                ) as MutableList<OrderReceiveSummaryItemModel>,
                            sdNo = sdNumber,
                        ),
                    message = "Success",
                    ref = "",
                )
            val mockSummaryModelResult = flowOf(Result.Success(mockOrderReceiveSummaryModel))

            val mockOrderReceiveSummaryUIModel =
                OrderReceiveSummaryUIModel(
                    sdNo = "SD121-123-123",
                    displayScreenState = SummaryDisplayScreenState.SummaryListItemScreen,
                    items =
                        listOf(
                            OrderReceiveSummaryItemUIModel(
                                barcode = "18859361300567",
                                productName = "เลย์mede",
                                unitName = "0/2 ลัง",
                            ),
                            OrderReceiveSummaryItemUIModel(
                                barcode = "18859361300938",
                                productName = "มาม่า รสต้มยมกุ้ง",
                                unitName = "3/2 ลัง",
                            ),
                        ),
                )

            val expectedDataResult = OrderReceiveSummaryState(data = mockOrderReceiveSummaryUIModel)

            coEvery { orderReceiveLiveData.getSdNo() } answers {
                sdNumber
            }

            coEvery { orderReceiveSummaryUseCase.execute(sdNo = sdNumber) } answers {
                mockSummaryModelResult
            }

            coEvery {
                uiModelMapper.orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryUIModel(
                    orderReceiveSummaryModel = mockOrderReceiveSummaryModel,
                )
            } answers {
                mockOrderReceiveSummaryUIModel
            }

            // Act
            viewModel.loadSummary()

            // Assert
            viewModel.summaryState.test {
                assertEquals(expectedDataResult, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(null), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `should get summary list when success but empty item`() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"
            val mockOrderReceiveSummaryModel =
                OrderReceiveSummaryModel(
                    code = "SUCCESS",
                    data =
                        OrderReceiveSummaryDataModel(
                            createdDate = "",
                            items = mutableListOf(),
                            sdNo = sdNumber,
                        ),
                    message = "Success",
                    ref = "",
                )
            val mockSummaryModelResult = flowOf(Result.Success(mockOrderReceiveSummaryModel))

            val mockOrderReceiveSummaryUIModel =
                OrderReceiveSummaryUIModel(
                    sdNo = "SD121-123-123",
                    displayScreenState = SummaryDisplayScreenState.SummaryEmptyScreen,
                    items = listOf(),
                )

            val expectedDataResult = OrderReceiveSummaryState(data = mockOrderReceiveSummaryUIModel)

            coEvery { orderReceiveLiveData.getSdNo() } answers {
                sdNumber
            }

            coEvery { orderReceiveSummaryUseCase.execute(sdNo = sdNumber) } answers {
                mockSummaryModelResult
            }

            coEvery {
                uiModelMapper.orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryUIModel(
                    orderReceiveSummaryModel = mockOrderReceiveSummaryModel,
                )
            } answers {
                mockOrderReceiveSummaryUIModel
            }

            // Act
            viewModel.loadSummary()

            // Assert
            viewModel.summaryState.test {
                assertEquals(expectedDataResult, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(null), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_loadSummary_when_result_error() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"

            coEvery { orderReceiveLiveData.getSdNo() } answers {
                sdNumber
            }

            coEvery {
                orderReceiveSummaryUseCase.execute(sdNumber)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "X12345",
                                type = ErrorType.WARNING,
                                message = null,
                            ),
                        canRetry = true,
                    ),
                )

            // Act
            viewModel.loadSummary()

            // Assert
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assert(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    dialog.titleText,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_service_failed),
                    dialog.messageText,
                )
                assertEquals(
                    R.string.text_retry,
                    dialog.positiveTextRes,
                )
                dialog.onPositiveClick?.invoke()
                val resetState = awaitItem()
                assertEquals(BaseScreenState.Idle, resetState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_loadSummary_when_result_error_and_click_negative_button() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"

            coEvery { orderReceiveLiveData.getSdNo() } answers {
                sdNumber
            }

            coEvery {
                orderReceiveSummaryUseCase.execute(sdNumber)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "X12345",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        canRetry = true,
                    ),
                )

            // Act
            viewModel.loadSummary()

            // Assert
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assert(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    dialog.titleText,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_service_failed),
                    dialog.messageText,
                )
                assertEquals(
                    R.string.text_retry,
                    dialog.positiveTextRes,
                )
                dialog.onNegativeClick?.invoke()
                val resetState = awaitItem()
                assertEquals(BaseScreenState.Idle, resetState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_loadSummary_when_result_error_show_dialog_error() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"

            coEvery { orderReceiveLiveData.getSdNo() } answers {
                sdNumber
            }

            coEvery {
                orderReceiveSummaryUseCase.execute(sdNumber)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "X12345",
                                type = ErrorType.ERROR,
                                title = "text_title",
                                message = "text_message",
                            ),
                    ),
                )

            // Act
            viewModel.loadSummary()

            // Assert
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assert(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(dialog.showF1Message, true)
                assertEquals(
                    "text_title",
                    dialog.titleText,
                )
                assertEquals(
                    "text_message",
                    dialog.messageText,
                )
                assertEquals(
                    R.string.text_close,
                    dialog.positiveTextRes,
                )
                dialog.onPositiveClick?.invoke()
                val resetState = awaitItem()
                assertEquals(BaseScreenState.Idle, resetState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_loadSummary_when_exception_is_occur() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"

            coEvery { orderReceiveLiveData.getSdNo() } answers {
                sdNumber
            }

            coEvery {
                orderReceiveSummaryUseCase.execute(sdNumber)
            } returns flow { throw Exception() }

            // Act
            viewModel.loadSummary()

            // Assert
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assert(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    dialog.titleText,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_service_failed),
                    dialog.messageText,
                )
                dialog.onPositiveClick?.invoke()
                val resetState = awaitItem()
                assertEquals(BaseScreenState.Idle, resetState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_loadSummary_when_exception_is_occur_and_click_negative_button() =
        runTest {
            // Arrange
            val sdNumber = "SD2505M04C-000038"

            coEvery { orderReceiveLiveData.getSdNo() } answers {
                sdNumber
            }

            coEvery {
                orderReceiveSummaryUseCase.execute(sdNumber)
            } returns flow { throw Exception("Service error") }

            // Act
            viewModel.loadSummary()

            // Assert
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assert(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    dialog.titleText,
                )
                assertEquals(
                    "Service error",
                    dialog.messageText,
                )
                dialog.onNegativeClick?.invoke()
                val resetState = awaitItem()
                assertEquals(BaseScreenState.Idle, resetState)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `onButtonConfirmClick event test`() =
        runTest {
            // Arrange
            val mockSdNo = "test"
            val expectedResult =
                OrderReceiveSummaryCompleteUIModel(sdNo = mockSdNo, isComplete = false)

            // Act
            viewModel.onButtonConfirmClick(sdNo = mockSdNo)

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(data = expectedResult), awaitItem())
            }
        }

    @Test
    fun `onButtonDialogConfirmCancelClick event test`() =
        runTest {
            // Arrange
            val expectedResult = viewModel.summaryState.value

            // Act
            viewModel.onButtonDialogConfirmCancelClick()

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(data = expectedResult), awaitItem())
            }
        }

    @Test
    fun `onButtonDialogConfirmCompleteClick event test`() =
        runTest {
            // Arrange
            val sdNo = "test"
            val expectedResult =
                uiModelMapper.orderReceiveSummaryUIMapper.mapToOrderReceiveSummaryCompleteUIModel(
                    sdNo = sdNo,
                    isComplete = true,
                )

            // Act
            viewModel.onButtonDialogConfirmCompleteClick(sdNo = sdNo, isComplete = true)

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(data = expectedResult), awaitItem())
            }
        }

    @Test
    fun `logEventGetSummary should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = mockk<OrderReceiveSummaryUIModel>()

            // when
            viewModel.logEventGetSummary(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.GET_SUMMARY_DATA,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventCompleteOrder should logInfo with correct action and message when state is true`() =
        runTest {
            // when
            viewModel.logEventCompleteOrder(true)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_COMPLETED_SUMMARY_ORDER,
                    message = LogAction.CONFIRM_COMPLETED_SUMMARY_ORDER.name,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventCompleteOrder should logInfo with correct action and message when state is false`() =
        runTest {
            // when
            viewModel.logEventCompleteOrder(false)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CANCEL_COMPLETED_SUMMARY_ORDER,
                    message = LogAction.CANCEL_COMPLETED_SUMMARY_ORDER.name,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `trackTimeTracking should call logTimeEventUseCase track`() =
        runTest {
            // Arrange
            val type = ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS
            val status = ProcessStatusType.IN_PROCESS

            // Act
            viewModel.trackTimeTracking(type, status)

            // Assert
            coVerify { trackTimeEventUseCase.track(type = type, status = status) }
        }

    @Test
    fun `stopTimeTracking should call logTimeEventUseCase stop`() =
        runTest {
            // Arrange
            val type = ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS

            // Act
            viewModel.stopTimeTracking(type)

            // Assert
            coVerify { trackTimeEventUseCase.stop(type = type) }
        }
}
