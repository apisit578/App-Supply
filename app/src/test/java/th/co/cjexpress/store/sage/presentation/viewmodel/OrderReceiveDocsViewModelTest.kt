package th.co.cjexpress.store.sage.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.livedata.orderReceive.OrderReceiveLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDataModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.GetSdDocListUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.GetShipmentDocumentBySdNoUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentOpenUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentListUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.presentation.state.auth.AuthState
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentListState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveDocsViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class OrderReceiveDocsViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var getSdDocListUseCase: GetSdDocListUseCase

    @RelaxedMockK
    lateinit var getShipmentDocumentBySDNoUseCase: GetShipmentDocumentBySdNoUseCase

    @RelaxedMockK
    lateinit var shipmentDocumentOpenUseCase: ShipmentDocumentOpenUseCase

    @RelaxedMockK
    lateinit var orderReceiveLiveData: OrderReceiveLiveData

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: OrderReceiveDocsViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            OrderReceiveDocsViewModel(
                getSdDocListUseCase = getSdDocListUseCase,
                getShipmentDocumentBySDNoUseCase = getShipmentDocumentBySDNoUseCase,
                shipmentDocumentOpenUseCase = shipmentDocumentOpenUseCase,
                orderReceiveLiveData = orderReceiveLiveData,
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_getSdDocsList_success_expect_update_ui_state_success() =
        runTest {
            // Given
            val mockDocumentSDModel = mockk<DocumentSDModel>(relaxed = true)
            val mockShipmentDocumentListUIModel = mockk<ShipmentDocumentListUIModel>(relaxed = true)

            coEvery { getSdDocListUseCase.execute() } returns
                flowOf(Result.Success(mockDocumentSDModel))

            every { uiModelMapper.shipmentDocumentListUIMapper.toUIModel(mockDocumentSDModel) } returns mockShipmentDocumentListUIModel

            // When
            viewModel.getSdDocsList()

            // Then
            viewModel.shipmentDocumentListState.test {
                assertEquals(
                    ShipmentDocumentListState(mockShipmentDocumentListUIModel),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSdDocsList_error_expect_update_ui_state_error() =
        runTest {
            // Given
            coEvery { getSdDocListUseCase.execute() } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )

            // When
            viewModel.getSdDocsList()

            // Then
            viewModel.shipmentDocumentListState.test {
                assertEquals(ShipmentDocumentListState(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSdDocsList_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            coEvery { getSdDocListUseCase.execute() } returns flow { throw Exception() }

            // When
            viewModel.getSdDocsList()

            // Then
            viewModel.shipmentDocumentListState.test {
                assertEquals(ShipmentDocumentListState(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_success_expect_update_ui_state_success() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            val mockShipmentDataModel = mockk<ShipmentDataModel>()
            val mockShipmentDocumentUIModel = mockk<ShipmentDocumentUIModel>()
            coEvery { getShipmentDocumentBySDNoUseCase.execute(sdNo) } returns
                flowOf(
                    Result.Success(
                        mockk(relaxed = true) {
                            every { data } returns mockShipmentDataModel
                        },
                    ),
                )
            every { uiModelMapper.shipmentDocumentUIMapper.toUIModel(mockShipmentDataModel) } returns mockShipmentDocumentUIModel

            // when
            viewModel.shipmentDocumentBySDNo(sdNo)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(mockShipmentDocumentUIModel),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_error_expect_update_ui_state_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            coEvery { getShipmentDocumentBySDNoUseCase.execute(sdNo) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )

            // when
            viewModel.shipmentDocumentBySDNo(sdNo)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            coEvery { getShipmentDocumentBySDNoUseCase.execute(sdNo) } returns flow { throw Exception() }

            // when
            viewModel.shipmentDocumentBySDNo(sdNo)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_shipmentDocumentOpen_success_expect_update_ui_state_success() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val mockShipmentDataModel = mockk<ShipmentDataModel>()
            val mockShipmentDocumentUIModel = mockk<ShipmentDocumentUIModel>()
            coEvery { shipmentDocumentOpenUseCase.execute(ldNo) } returns
                flowOf(
                    Result.Success(
                        mockk(relaxed = true) {
                            every { data } returns mockShipmentDataModel
                        },
                    ),
                )
            every { uiModelMapper.shipmentDocumentUIMapper.toUIModel(mockShipmentDataModel) } returns mockShipmentDocumentUIModel

            // when
            viewModel.shipmentDocumentOpen(ldNo)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(mockShipmentDocumentUIModel),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_shipmentDocumentOpen_error_expect_update_ui_state_error() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            coEvery { shipmentDocumentOpenUseCase.execute(ldNo) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )

            // when
            viewModel.shipmentDocumentOpen(ldNo)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_shipmentDocumentOpen_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            coEvery { shipmentDocumentOpenUseCase.execute(ldNo) } returns flow { throw Exception() }

            // when
            viewModel.shipmentDocumentOpen(ldNo)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setShowScanNewDocDialogState_expect_update_showScanNewDocDialogState() =
        runTest {
            // when
            viewModel.setShowScanNewDocDialogState(true)

            // then
            viewModel.showScanNewDocDialogState.test {
                assertTrue(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setShowScanNewDocDialogState_shouldUpdateState() =
        runTest {
            viewModel.setShowScanNewDocDialogState(true)

            viewModel.showScanNewDocDialogState.test {
                assertEquals(true, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setupShipmentSummaryLiveData_shouldCallLiveDataSetter() {
        val mockData = mockk<ShipmentDocumentUIModel>(relaxed = true)
        viewModel.setupShipmentSummaryLiveData(mockData)
        verify { orderReceiveLiveData.setShipmentSummaryUIModel(mockData) }
    }

    @Test
    fun test_resetShipmentSummaryLiveData_shouldCallLiveDataSetter() {
        viewModel.resetShipmentSummaryLiveData()
        verify { orderReceiveLiveData.resetShipmentSummaryUIModel() }
    }

    @Test
    fun `logEventScanNewDocDialog should logInfo when state is true`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_open_scan_ld_dialog) } returns "Open scan new document dialog"

            // when
            viewModel.logEventScanNewDocDialog(true)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.OPEN_SCAN_NEW_DOC_DIALOG,
                    message = "Open scan new document dialog",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventScanNewDocDialog should logInfo when state is false`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_close_scan_ld_dialog) } returns "Close scan new document dialog"

            // when
            viewModel.logEventScanNewDocDialog(false)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CANCEL_SCAN_NEW_DOC,
                    message = "Close scan new document dialog",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSubmitNewDoc should logInfo with correct action and message`() =
        runTest {
            // given
            val docNo = "DOC123"
            every { resourceUtils.getString(R.string.log_message_ld_bt_no) } returns "Document No: %s"

            // when
            viewModel.logEventSubmitNewDoc(docNo)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_SCAN_NEW_DOC,
                    message = "Document No: DOC123",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectDoc should logInfo with correct action and message`() =
        runTest {
            // given
            val docNo = "SD123"
            every { resourceUtils.getString(R.string.log_message_sd_no) } returns "SD No: %s"

            // when
            viewModel.logEventSelectDoc(docNo)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_SD_DOC,
                    message = "SD No: SD123",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `onPositiveClickState retry should call shipmentDocumentOpen`() =
        runTest {
            val spyVm = io.mockk.spyk(viewModel)
            val ldNo = "LD123"
            spyVm.handleShipmentDocumentOpenError(true, null, ldNo)
            verify(exactly = 1) { spyVm.shipmentDocumentOpen(ldNo) }
        }

    @Test
    fun `onPositiveClickState session timeout should wait unauthorized false then open dialog`() =
        runTest {
            AuthState.emitUnauthorizedEvent(true)

            val job =
                launch {
                    viewModel.showScanNewDocDialogState.drop(1).test {
                        viewModel.handleShipmentDocumentOpenError(
                            canRetry = false,
                            errorCode = ErrorCodes.SESSION_TIMEOUT,
                            ldNo = "LD123",
                        )
                        AuthState.emitUnauthorizedEvent(false)
                        advanceUntilIdle()

                        assert(awaitItem())
                        cancelAndIgnoreRemainingEvents()
                    }
                }

            job.join()
        }

    @Test
    fun `onPositiveClickState non-retry non-timeout should do nothing`() =
        runTest {
            val ldNo = "LD123"
            viewModel.handleShipmentDocumentOpenError(false, "OTHER_ERROR", ldNo)
            viewModel.showScanNewDocDialogState.drop(1).test {
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }
        }
}
