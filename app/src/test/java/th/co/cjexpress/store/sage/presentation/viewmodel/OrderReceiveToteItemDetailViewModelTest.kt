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
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteLowestFactorUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteReceivingBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitAdjustUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteSubmitUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteLowestFactorState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteItemDetailViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers
import java.util.Date

class OrderReceiveToteItemDetailViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var sdToteSubmitUseCase: SdToteSubmitUseCase

    @RelaxedMockK
    lateinit var sdToteLowestFactorUseCase: SdToteLowestFactorUseCase

    @RelaxedMockK
    lateinit var sdToteReceivingBarcodeUseCase: SdToteReceivingBarcodeUseCase

    @RelaxedMockK
    lateinit var sdToteSubmitAdjustUseCase: SdToteSubmitAdjustUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: OrderReceiveToteItemDetailViewModel

    override fun setUp() {
        super.setUp()
        every { sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value } returns mockk(relaxed = true)
        viewModel =
            OrderReceiveToteItemDetailViewModel(
                sdToteSubmitUseCase = sdToteSubmitUseCase,
                sdToteReceivingBarcodeUseCase = sdToteReceivingBarcodeUseCase,
                sdToteLowestFactorUseCase = sdToteLowestFactorUseCase,
                sdToteSubmitAdjustUseCase = sdToteSubmitAdjustUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
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
                    BaseScreenState.Success(SdToteBarcodeEditingSubmitState(mockModel)),
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
    fun test_getLowestFactor_success_expect_success_state() =
        runTest {
            // given
            val sdNo = "SD123"
            val barcode = "1234567890"
            val itemBarcode = "1234"

            coEvery {
                sdToteLowestFactorUseCase.execute(sdNo, itemBarcode, barcode)
            } returns flowOf(Result.Success(mockk(relaxed = true)))

            // when
            viewModel.getLowestFactor(sdNo, itemBarcode, barcode)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(SdToteLowestFactorState(isSuccess = true)),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getLowestFactor_error_expect_error_state() =
        runTest {
            // given
            val sdNo = "SD123"
            val barcode = "1234567890"
            val itemBarcode = "1234"

            coEvery {
                sdToteLowestFactorUseCase.execute(sdNo, itemBarcode, barcode)
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
            viewModel.getLowestFactor(sdNo, itemBarcode, barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getLowestFactor_exception_expect_error_state() =
        runTest {
            // given
            val sdNo = "SD123"
            val barcode = "1234567890"
            val itemBarcode = "1234"

            val exception = RuntimeException("Network failed")

            coEvery {
                sdToteLowestFactorUseCase.execute(sdNo, itemBarcode, barcode)
            } returns flow { throw exception }

            // when
            viewModel.getLowestFactor(sdNo, itemBarcode, barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
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
    fun test_removeSubmitExpiryDateLD_should_call_removeSubmitExpiryDate() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.removeSubmitExpiryDateLD(date, true)

            // then
            verify { sdToteReceivingBarcodeUseCase.removeSubmitExpiryDate(date, true) }
        }

    @Test
    fun test_setRemovingExpiryDateState_expect_update_removingExpiryDateState() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.setRemovingExpiryDateState(date, true)

            // then
            viewModel.removingExpiryDateState.test {
                assertEquals(awaitItem(), Pair(date, true))
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSdToteDocNoLD_expect_correct_return_from_usecase() =
        runTest {
            // given
            val mockData = "MOCK"

            every {
                sdToteReceivingBarcodeUseCase.getSdToteDocNo()
            } returns mockData

            // when
            val result =
                viewModel.getSdToteDocNoLD()

            // then
            assertEquals(mockData, result)
        }

    @Test
    fun test_validateBarcode_when_barcode_is_not_null_expect_return_true() =
        runTest {
            // given
            val mockData = "MOCK"

            every {
                sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value?.barcode
            } returns mockData

            // when
            val result =
                viewModel.validateBarcode(mockData)

            // then
            assertEquals(true, result)
        }

    @Test
    fun test_validateBarcode_when_barcode_is_null_expect_correct_return_false() =
        runTest {
            // given
            val mockData = "MOCK"

            every {
                sdToteReceivingBarcodeUseCase.submitBarcodeFlowLD.value
            } returns null

            // when
            val result =
                viewModel.validateBarcode(mockData)

            // then
            assertEquals(false, result)
        }

    @Test
    fun test_setInputActualQtyState_expect_update_inputActualQtyState() =
        runTest {
            // given
            val mockData = 1

            // when
            viewModel.setInputActualQtyState(mockData)

            // then
            viewModel.inputActualQtyState.test {
                assertEquals(awaitItem(), mockData)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_updateSubmitActualQtyLD_should_call_updateSubmitActualQty() =
        runTest {
            // given
            val mockData = 1

            // when
            viewModel.updateSubmitActualQtyLD(mockData)

            // then
            verify { sdToteReceivingBarcodeUseCase.updateSubmitActualQty(mockData) }
        }

    @Test
    fun test_setCanEditState_expect_update_canEditState() =
        runTest {
            // given
            val mockData = true

            // when
            viewModel.setCanEditState(mockData)

            // then
            viewModel.canEditState.test {
                assertEquals(awaitItem(), mockData)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_updateIsAddLotStateLD_should_call_updateIsAddLotState() =
        runTest {
            // given
            val mockData = true

            // when
            viewModel.updateIsAddLotStateLD(mockData)

            // then
            verify { sdToteReceivingBarcodeUseCase.updateIsAddLotState(mockData) }
        }

    @Test
    fun test_incrementFreshLifeExpiryDate_should_call_incrementFreshLifeExpiryDate() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.incrementFreshLifeExpiryDate(date, false)

            // then
            verify { sdToteReceivingBarcodeUseCase.incrementFreshLifeExpiryDate(date, false) }
        }

    @Test
    fun test_decrementFreshLifeExpiryDate_should_call_decrementFreshLifeExpiryDate() =
        runTest {
            // given
            val date = Date()

            // when
            viewModel.decrementFreshLifeExpiryDate(date, true)

            // then
            verify { sdToteReceivingBarcodeUseCase.decreaseFreshLifeExpiryDate(date, true) }
        }

    @Test
    fun test_sdToteSubmitAdjust_success_expect_update_ui_state_success() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitAdjustUseCase.execute(any())
            } returns flowOf(Result.Success(mockk(relaxed = true)))

            // when
            viewModel.sdToteSubmitAdjust(mockModel)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(SdToteBarcodeEditingSubmitState(mockModel)),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            verify { sdToteReceivingBarcodeUseCase.updateReceivingBarcode(any(), any(), any()) }
        }

    @Test
    fun test_sdToteSubmitAdjust_error_expect_update_ui_state_error() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitAdjustUseCase.execute(any())
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
            viewModel.sdToteSubmitAdjust(mockModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_sdToteSubmitAdjust_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val mockModel = mockk<SdToteSubmitUIModel>(relaxed = true)
            coEvery {
                sdToteSubmitAdjustUseCase.execute(any())
            } returns flow { throw Exception() }

            // when
            viewModel.sdToteSubmitAdjust(mockModel)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `logEventSubmitBarcode should logInfo with correct action and message`() =
        runTest {
            // given
            val barcode = "BARCODE123"

            // when
            viewModel.logEventSubmitBarcode(barcode)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_BARCODE_EDIT_STATE,
                    message = barcode,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventAddNewExpiryDate should logInfo with correct action and message`() =
        runTest {
            // given
            val mockState = mockk<SdToteSubmitUIModel>()

            // when
            viewModel.logEventAddNewExpiryDate(mockState)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ADD_NEW_EXPIRED_DATE,
                    message = mockState.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventAdjustItemQuantityFreshLife should logInfo with correct action and message`() =
        runTest {
            // given
            val mockState =
                mockk<SdToteSubmitUIModel> {
                    every { productName } returns ""
                    every { barcode } returns ""
                }
            val expiryDate = Date()
            val isAdd = true
            every { resourceUtils.getString(R.string.log_message_adjust_quantity_fresh_life) } returns "Adjust quantity: %s, %s, %s"

            // when
            viewModel.logEventAdjustItemQuantityFreshLife(mockState, expiryDate, isAdd)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.INCREASE_ITEM_QUANTITY,
                    message =
                        String.format(
                            "Adjust quantity: %s, %s, %s",
                            mockState.productName,
                            mockState.barcode,
                            expiryDate,
                        ),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventAdjustItemQuantityFreshLife when isAdd false should logInfo with correct action and message`() =
        runTest {
            // given
            val mockState =
                mockk<SdToteSubmitUIModel> {
                    every { productName } returns ""
                    every { barcode } returns ""
                }
            val expiryDate = Date()
            val isAdd = false
            every { resourceUtils.getString(R.string.log_message_adjust_quantity_fresh_life) } returns "Adjust quantity: %s, %s, %s"

            // when
            viewModel.logEventAdjustItemQuantityFreshLife(mockState, expiryDate, isAdd)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.DECREASE_ITEM_QUANTITY,
                    message =
                        String.format(
                            "Adjust quantity: %s, %s, %s",
                            mockState.productName,
                            mockState.barcode,
                            expiryDate,
                        ),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventAdjustItemQuantity should logInfo with correct action and message`() =
        runTest {
            // given
            val mockState =
                mockk<SdToteSubmitUIModel> {
                    every { productName } returns ""
                    every { barcode } returns ""
                }
            every { resourceUtils.getString(R.string.log_message_adjust_quantity_normal) } returns "Adjust quantity: %s, %s"

            // when
            viewModel.logEventAdjustItemQuantity(mockState)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ADJUST_ITEM_QUANTITY,
                    message =
                        String.format(
                            "Adjust quantity: %s, %s",
                            mockState.productName,
                            mockState.barcode,
                        ),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSubmitEditItem should logInfo with correct action and message`() =
        runTest {
            // given
            val mockState = mockk<SdToteSubmitUIModel>()

            // when
            viewModel.logEventSubmitEditItem(mockState)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_EDIT_ITEM_DETAIL,
                    message = mockState.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
