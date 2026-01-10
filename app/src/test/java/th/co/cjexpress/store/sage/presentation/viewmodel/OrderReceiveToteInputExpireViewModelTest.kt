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
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItem
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAndGetLatestBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteReceivingBarcodeModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteInputExpiryDateSubmitState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteInputExpireViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers
import java.util.Date

class OrderReceiveToteInputExpireViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var sdToteSubmitUseCase: SdToteSubmitUseCase

    @RelaxedMockK
    lateinit var sdToteSubmitAndGetLatestBarcodeUseCase: SdToteSubmitAndGetLatestBarcodeUseCase

    @RelaxedMockK
    lateinit var sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: OrderReceiveToteInputExpireViewModel

    override fun setUp() {
        super.setUp()
        every { sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value } returns mockk(relaxed = true)
        viewModel =
            OrderReceiveToteInputExpireViewModel(
                sdToteSubmitUseCase = sdToteSubmitUseCase,
                sdToteSubmitAndGetLatestBarcodeUseCase = sdToteSubmitAndGetLatestBarcodeUseCase,
                sdToteReceivingBarcodeUseCase = sdToteReceivingBarcodeUseCase,
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
                logsUtils = logUtils,
            )
    }

    @Test
    fun test_submitBarcode_success_expect_update_ui_state_success() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                flowOf(Result.Success(mockk(relaxed = true)))

            // when
            viewModel.submitBarcode(mockModel)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(SdToteInputExpiryDateSubmitState(mockModel)),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            verify { sdToteReceivingBarcodeUseCase.updateReceivingBarcode(any(), any(), any()) }
        }

    @Test
    fun test_submitBarcode_error_expect_update_ui_state_error() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
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
            viewModel.submitBarcode(mockModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_submitBarcode_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns flow { throw Exception() }

            // when
            viewModel.submitBarcode(mockModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_submitAndGetLatestBarcode_success_expect_update_ui_state_success() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            val mockResponse = mockk<SdToteDetailItem>(relaxed = true)
            val mockUIModel = mockk<SdToteDocsDetailBarcodeUIModel>()

            coEvery {
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
                flowOf(
                    Result.Success(
                        mockk(relaxed = true) {
                            every { latestBarcode } returns mockResponse
                        },
                    ),
                )

            every { uiModelMapper.sdToteDocDetailUIMapper.toUIModel(mockResponse) } returns mockUIModel

            // when
            viewModel.submitAndGetLatestBarcode(mockModel)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(SdToteInputExpiryDateSubmitState(mockModel)),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            verify { sdToteReceivingBarcodeUseCase.addNewReceivingBarcode(any()) }
        }

    @Test
    fun test_submitAndGetLatestBarcode_error_expect_update_ui_state_error() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns
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
            viewModel.submitAndGetLatestBarcode(mockModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_submitAndGetLatestBarcode_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                    any(),
                )
            } returns flow { throw Exception() }

            // when
            viewModel.submitAndGetLatestBarcode(mockModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setInputExpiryDateState_should_update_date_state() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.setInputExpiryDateState(date)

            // then
            viewModel.inputExpiryDateState.test {
                assertEquals(date, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_resetSubmitBarcodeLD_should_call_resetSdToteSubmitUIModel() =
        runTest {
            // when
            viewModel.resetSubmitBarcodeLD()

            // then
            verify { sdToteReceivingBarcodeUseCase.resetSdToteSubmitUIModel() }
        }

    @Test
    fun test_updateOrAddSubmitExpiryDateLD_should_call_updateOrAddSubmitExpiryDate() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.updateOrAddSubmitExpiryDateLD(date, true)

            // then
            verify { sdToteReceivingBarcodeUseCase.updateOrAddSubmitExpiryDate(date, true) }
        }

    @Test
    fun test_moveExpiryDateLotToFirstLD_should_call_moveExpiryDateLotToFirst() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.moveExpiryDateLotToFirstLD(date)

            // then
            verify { sdToteReceivingBarcodeUseCase.moveExpiryDateLotToFirst(date) }
        }

    @Test
    fun test_createSubmitBarcodeLD_expect_correct_return_from_usecase() =
        runTest {
            // given
            val isNewBarcode = true
            val barcodeData = mockk<ISdToteReceivingBarcodeModel>()
            val actualQty = 10
            val expList = mutableListOf<ISdToteExpiryDateModel>(mockk(), mockk())
            val expectedResult = mockk<SdToteSubmitUIModel>()

            every {
                sdToteReceivingBarcodeUseCase.createSubmitBarcode(
                    isNewBarcode = isNewBarcode,
                    receivingBarcode = barcodeData,
                    actualQty = actualQty,
                    expList = expList,
                )
            } returns expectedResult

            // when
            val result =
                viewModel.createSubmitBarcodeLD(
                    isNewBarcode = isNewBarcode,
                    barcodeData = barcodeData,
                    actualQty = actualQty,
                    expList = expList,
                )

            // then
            assertEquals(expectedResult, result)
        }

    @Test
    fun test_getExpiryDateActualQtyLD_expect_correct_return_from_usecase() =
        runTest {
            // given
            val date = Date()
            val expectedResult = 1

            every {
                sdToteReceivingBarcodeUseCase.getExpiryDateActualQty(date)
            } returns expectedResult

            // when
            val result =
                viewModel.getExpiryDateActualQtyLD(date)

            // then
            assertEquals(expectedResult, result)
        }

    @Test
    fun `logEventDefaultExpiryDate should logInfo with correct action and message`() =
        runTest {
            // given
            val barcode = "BARCODE123"
            val expiryDate = "2023-12-31"
            every { resourceUtils.getString(R.string.log_message_default_expired_date) } returns "Default expiry date: %s, %s"

            // when
            viewModel.logEventDefaultExpiryDate(barcode, expiryDate)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.DEFAULT_EXPIRED_DATE,
                    message = "Default expiry date: BARCODE123, 2023-12-31",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventAdjustExpiryDate should logInfo with correct action and message`() =
        runTest {
            // given
            val expiryDate = "2023-12-31"

            // when
            viewModel.logEventAdjustExpiryDate(expiryDate)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ADJUST_EXPIRED_DATE,
                    message = "2023-12-31",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventConfirmAdjustExpiryDate should logInfo with correct action and message`() =
        runTest {
            // given
            val expiryDate = "2023-12-31"
            val qrPrint = true
            val barcode = "BARCODE123"
            every { viewModel.submitBarcodeFlowLD.value.barcode } returns barcode
            every { resourceUtils.getString(R.string.log_message_confirm_expired_date) } returns "Confirm expiry date: %s, %s, %s"
            every { resourceUtils.getString(R.string.text_checkbox_qr_expire) } returns "QR Expire"

            // when
            viewModel.logEventConfirmAdjustExpiryDate(expiryDate, qrPrint)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_ADJUST_EXPIRED_DATE,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventConfirmAdjustExpiryDate when qrPrint false should logInfo with correct action and message`() =
        runTest {
            // given
            val expiryDate = "2023-12-31"
            val qrPrint = false
            val barcode = "BARCODE123"
            every { viewModel.submitBarcodeFlowLD.value.barcode } returns barcode
            every { resourceUtils.getString(R.string.log_message_confirm_expired_date) } returns "Confirm expiry date: %s, %s, %s"
            every { resourceUtils.getString(R.string.log_message_unchecked_qr_print) } returns "Unchecked QR Print"

            // when
            viewModel.logEventConfirmAdjustExpiryDate(expiryDate, qrPrint)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_ADJUST_EXPIRED_DATE,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
