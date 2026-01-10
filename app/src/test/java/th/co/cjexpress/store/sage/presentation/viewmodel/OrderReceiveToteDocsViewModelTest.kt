package th.co.cjexpress.store.sage.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.livedata.orderReceiveTote.OrderReceiveToteLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailData
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.GetSdToteListUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.OpenToteUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SearchToteByDocNoUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteDocsViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class OrderReceiveToteDocsViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var getSdToteListUseCase: GetSdToteListUseCase

    @RelaxedMockK
    lateinit var openToteUseCase: OpenToteUseCase

    @RelaxedMockK
    lateinit var searchToteByDocNoUseCase: SearchToteByDocNoUseCase

    @RelaxedMockK
    lateinit var orderReceiveToteLiveData: OrderReceiveToteLiveData

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: OrderReceiveToteDocsViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            OrderReceiveToteDocsViewModel(
                getSdToteListUseCase = getSdToteListUseCase,
                openToteUseCase = openToteUseCase,
                searchToteByDocNoUseCase = searchToteByDocNoUseCase,
                orderReceiveToteLiveData = orderReceiveToteLiveData,
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_getSdToteDocsList_success_expect_update_ui_state_success() =
        runTest {
            // given
            val mockSdToteDataList = listOf(mockk<SdToteDataModel>(relaxed = true))
            coEvery { getSdToteListUseCase.execute() } returns
                flowOf(
                    Result.Success(
                        mockk(relaxed = true) {
                            every { data } returns mockSdToteDataList
                        },
                    ),
                )

            // when
            viewModel.getSdToteDocsList()

            // then
            viewModel.sdToteDocsList.test {
                assertEquals(mockSdToteDataList, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(null), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSdToteDocsList_error_expect_update_ui_state_error() =
        runTest {
            // given
            coEvery { getSdToteListUseCase.execute() } returns
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
            viewModel.getSdToteDocsList()

            // then
            viewModel.sdToteDocsList.test {
                assertEquals(emptyList<SdToteDataModel>(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSdToteDocsList_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            coEvery { getSdToteListUseCase.execute() } returns flow { throw Exception() }

            // when
            viewModel.getSdToteDocsList()

            // then
            viewModel.sdToteDocsList.test {
                assertEquals(emptyList<SdToteDataModel>(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_openTote_success_expect_update_ui_state_success() =
        runTest {
            // given
            val toteCode = "TOTE123"
            val mockSdToteDocDetailDataModel = mockk<SdToteDetailData>()
            val mockSdToteDocsDetailUIModel = mockk<SdToteDocsDetailUIModel>()
            coEvery { openToteUseCase.execute(toteCode) } returns
                flowOf(
                    Result.Success(
                        mockk(relaxed = true) {
                            every { data } returns mockSdToteDocDetailDataModel
                        },
                    ),
                )
            every { uiModelMapper.sdToteDocDetailUIMapper.toUIModel(mockSdToteDocDetailDataModel) } returns mockSdToteDocsDetailUIModel

            // when
            viewModel.openTote(toteCode)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(mockSdToteDocsDetailUIModel),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_openTote_error_expect_update_ui_state_error() =
        runTest {
            // given
            val toteCode = "TOTE123"
            coEvery { openToteUseCase.execute(toteCode) } returns
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
            viewModel.openTote(toteCode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_openTote_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val toteCode = "TOTE123"
            coEvery { openToteUseCase.execute(toteCode) } returns flow { throw Exception() }

            // when
            viewModel.openTote(toteCode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_searchByDocNo_success_expect_update_ui_state_success() =
        runTest {
            // given
            val docNo = "DOC123"
            val mockSdToteDocDetailDataModel = mockk<SdToteDetailData>()
            val mockSdToteDocsDetailUIModel = mockk<SdToteDocsDetailUIModel>()
            coEvery { searchToteByDocNoUseCase.execute(docNo) } returns
                flowOf(
                    Result.Success(
                        mockk(relaxed = true) {
                            every { data } returns mockSdToteDocDetailDataModel
                        },
                    ),
                )
            every { uiModelMapper.sdToteDocDetailUIMapper.toUIModel(mockSdToteDocDetailDataModel) } returns mockSdToteDocsDetailUIModel

            // when
            viewModel.searchByDocNo(docNo)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(mockSdToteDocsDetailUIModel),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_searchByDocNo_error_expect_update_ui_state_error() =
        runTest {
            // given
            val docNo = "DOC123"
            coEvery { searchToteByDocNoUseCase.execute(docNo) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "UNKNOWN_ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        data = mockk(relaxed = true),
                    ),
                )

            // when
            viewModel.searchByDocNo(docNo)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_searchByDocNo_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val docNo = "DOC123"
            coEvery { searchToteByDocNoUseCase.execute(docNo) } returns flow { throw Exception() }

            // when
            viewModel.searchByDocNo(docNo)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setShowScanNewToteDialogState_expect_update_showScanNewToteDialogState() =
        runTest {
            // when
            viewModel.setShowScanNewToteDialogState(true)

            // then
            viewModel.showScanNewToteDialogState.test {
                assertTrue(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setSdToteDetailDataLD_expect_setSdToteDocsDetailUIModel_was_call() =
        runTest {
            // given
            val mockData = mockk<SdToteDocsDetailUIModel>()

            // when
            viewModel.setSdToteDetailDataLD(mockData)

            // then
            verify { orderReceiveToteLiveData.setSdToteDocsDetailUIModel(mockData) }
        }

    @Test
    fun test_resetSdToteLD_expect_orderReceiveToteLiveData_was_call() =
        runTest {
            // when
            viewModel.resetSdToteLD()

            // then
            verify { orderReceiveToteLiveData.resetSdToteDocsDetailUIModel() }
            verify { orderReceiveToteLiveData.resetSdToteSubmitUIModel() }
        }

    @Test
    fun test_setShowScanNewToteDialogState_shouldUpdateState() =
        runTest {
            viewModel.setShowScanNewToteDialogState(true)

            viewModel.showScanNewToteDialogState.test {
                assertEquals(true, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setSdToteDetailDataLD_shouldCallLiveDataSetter() {
        val mockData = mockk<SdToteDocsDetailUIModel>(relaxed = true)
        viewModel.setSdToteDetailDataLD(mockData)
        verify { orderReceiveToteLiveData.setSdToteDocsDetailUIModel(mockData) }
    }

    @Test
    fun test_resetSdToteLD_shouldCallLiveDataReset() {
        viewModel.resetSdToteLD()
        verify { orderReceiveToteLiveData.resetSdToteDocsDetailUIModel() }
        verify { orderReceiveToteLiveData.resetSdToteSubmitUIModel() }
    }

//    @Test
//    fun test_hideKeyboard_shouldCallToggleSoftInput() {
//        val context = mockk<Context>(relaxed = true)
//        val inputMethodManager = mockk<InputMethodManager>(relaxed = true)
//        every { context.getSystemService(Context.INPUT_METHOD_SERVICE) } returns inputMethodManager
//
//        viewModel.hideKeyboard(context)
//
//        verify { inputMethodManager.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0) }
//    }

    @Test
    fun `logEventScanNewToteDialog when open dialog should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_open_scan_tote_dialog) } returns "Open scan new tote dialog"

            // when
            viewModel.logEventScanNewToteDialog(true)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.OPEN_SCAN_NEW_TOTE_DIALOG,
                    message = "Open scan new tote dialog",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventScanNewToteDialog when close dialog should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_close_scan_tote_dialog) } returns "Close dialog scan new tote"

            // when
            viewModel.logEventScanNewToteDialog(false)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CANCEL_SCAN_NEW_TOTE,
                    message = "Close dialog scan new tote",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSubmitNewToteCode should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_tote_code) } returns "Tote code: "

            // when
            viewModel.logEventSubmitNewToteCode("TOTE123")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_OPEN_TOTE,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectToteDoc should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_doc_no) } returns "Doc No.: "

            // when
            viewModel.logEventSelectToteDoc("SD1234")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_TOTE_DOC,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
