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
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.CheckSubmitItemDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ReprintQrFreshLiveDataUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.component.TachyonDateInputFieldResultModel
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemDetailViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemDetailViewModel.UIState
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class ReprintQrFreshItemDetailViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var checkSubmitItemDetailUseCase: CheckSubmitItemDetailUseCase

    @RelaxedMockK
    lateinit var reprintQrFreshLiveDataUseCase: ReprintQrFreshLiveDataUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: ReprintQrFreshItemDetailViewModel

    override fun setUp() {
        super.setUp()
        val mockProductItem =
            ReprintQrFreshItemUIModel(
                barcode = "1234567890123",
                skuName = "Test Product",
                expireDate = "",
                qrPrintCount = 0,
            )
        every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns mockProductItem

        viewModel =
            ReprintQrFreshItemDetailViewModel(
                checkSubmitItemDetailUseCase = checkSubmitItemDetailUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
        every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"
    }

    @Test
    fun `test submitItemDetail success expect update ui state success`() =
        runTest {
            // Given
            val mockItem = mockk<ReprintQrFreshItemUIModel>()
            coEvery {
                checkSubmitItemDetailUseCase.execute(any(), any(), any())
            } returns flowOf(Result.Success(mockItem))

            every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns mockItem

            // When
            viewModel.submitItemDetail(actualQty = 1)

            // Then
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(UIState.NavToItemsScreen), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            verify { reprintQrFreshLiveDataUseCase.addQrFreshProduct(any()) }
        }

    @Test
    fun `test submitItemDetail error expect update ui state error`() =
        runTest {
            // Given
            coEvery {
                checkSubmitItemDetailUseCase.execute(any(), any(), any())
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

            // When
            viewModel.submitItemDetail(actualQty = 1)

            // Then
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `test submitItemDetail throw exception expect update ui state error`() =
        runTest {
            // Given
            coEvery {
                checkSubmitItemDetailUseCase.execute(any(), any(), any())
            } returns flow { throw Exception() }

            // When
            viewModel.submitItemDetail(actualQty = 1)

            // Then
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `test updateProductQrFreshItem when canEdit is true`() =
        runTest {
            // Given
            val mockItem = mockk<ReprintQrFreshItemUIModel>()
            every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value?.expireDate } returns ""

            // When
            viewModel.updateProductQrFreshItem(true, mockItem)

            // Then
            verify { reprintQrFreshLiveDataUseCase.addQrFreshProduct(mockItem) }
        }

    @Test
    fun `test updateProductQrFreshItem when canEdit is false`() =
        runTest {
            // Given
            val mockItem =
                ReprintQrFreshItemUIModel(
                    barcode = "123",
                    skuName = "Test Product",
                    expireDate = "01/01/2024",
                    qrPrintCount = 1,
                )
            every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value?.expireDate } returns "01/01/2024"

            // When
            viewModel.updateProductQrFreshItem(false, mockItem)

            // Then
            verify {
                reprintQrFreshLiveDataUseCase.updateQrPrintCount(
                    any(),
                    any(),
                    any(),
                )
            }
        }

    @Test
    fun `get productQrFreshItem should call usecase`() {
        // when
        viewModel.productQrFreshItem

        // then
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get productQrFreshItem null should call usecase`() {
        // given
        every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns null
        viewModel =
            ReprintQrFreshItemDetailViewModel(
                checkSubmitItemDetailUseCase = checkSubmitItemDetailUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )

        // when
        val result = viewModel.productQrFreshItem

        // then
        assertEquals(null, result.value)
        assertEquals(null, result.value?.expireDate)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get canEditState should call usecase`() {
        // when
        val result = viewModel.canEditState

        // then
        assertEquals(true, result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get canEditState when null value should call usecase`() {
        // given
        every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns null
        viewModel =
            ReprintQrFreshItemDetailViewModel(
                checkSubmitItemDetailUseCase = checkSubmitItemDetailUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )

        // when
        val result = viewModel.canEditState

        // then
        assertEquals(false, result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get canEditState when date not empty should call usecase`() {
        // given
        every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns
            ReprintQrFreshItemUIModel(
                barcode = "1234567890123",
                skuName = "Test Product",
                expireDate = "1/1/2025",
                qrPrintCount = 0,
            )
        viewModel =
            ReprintQrFreshItemDetailViewModel(
                checkSubmitItemDetailUseCase = checkSubmitItemDetailUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )

        // when
        val result = viewModel.canEditState

        // then
        assertEquals(false, result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get inputActualQtyState should call usecase`() {
        // when
        val result = viewModel.inputActualQtyState

        // then
        assertEquals(0, result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get inputActualQtyState when null value should call usecase`() {
        // given
        every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns null
        viewModel =
            ReprintQrFreshItemDetailViewModel(
                checkSubmitItemDetailUseCase = checkSubmitItemDetailUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )

        // when
        val result = viewModel.inputActualQtyState

        // then
        assertEquals(1, result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get dateInputState should call usecase`() {
        // when
        val result = viewModel.dateInputLD

        // then
        assertEquals("", result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get dateInputState when null value should call usecase`() {
        // given
        every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns null
        viewModel =
            ReprintQrFreshItemDetailViewModel(
                checkSubmitItemDetailUseCase = checkSubmitItemDetailUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
        // when
        val result = viewModel.dateInputLD

        // then
        assertEquals("", result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `get dateInputState when date not empty should call usecase`() {
        // given
        every { reprintQrFreshLiveDataUseCase.productQrFreshItem.value } returns
            ReprintQrFreshItemUIModel(
                barcode = "1234567890123",
                skuName = "Test Product",
                expireDate = "1/1/2025",
                qrPrintCount = 0,
            )
        viewModel =
            ReprintQrFreshItemDetailViewModel(
                checkSubmitItemDetailUseCase = checkSubmitItemDetailUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
        // when
        val result = viewModel.dateInputLD

        // then
        assertEquals("1/1/2025", result.value)
        verify { reprintQrFreshLiveDataUseCase.productQrFreshItem.value }
    }

    @Test
    fun `test promptBeforeBack shows confirmation dialog with correct properties`() =
        runTest {
            // Given
            every { resourceUtils.getString(R.string.text_confirm_exit_title_dialog) } returns "Exit Title"
            every { resourceUtils.getString(R.string.text_confirm_exit_detail_dialog) } returns "Exit Message"

            // When
            viewModel.promptBeforeBack()

            // Then
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)

                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(ErrorType.QUESTION, dialog.type)
                assertEquals("Exit Title", dialog.titleText)
                assertEquals("Exit Message", dialog.messageText)
                assertEquals(R.string.text_confirm, dialog.positiveTextRes)
                assertEquals(R.string.text_cancel, dialog.negativeTextRes)

                // Test positive click callback
                dialog.onPositiveClick?.invoke()
                val successState = awaitItem()
                assertEquals(successState, BaseScreenState.Success(UIState.NavToItemsScreen))

                // Test negative click callback
                dialog.onNegativeClick?.invoke()
                val resetState = awaitItem()
                assertEquals(BaseScreenState.Idle, resetState)

                cancelAndIgnoreRemainingEvents()
            }

            verify {
                resourceUtils.getString(R.string.text_confirm_exit_title_dialog)
                resourceUtils.getString(R.string.text_confirm_exit_detail_dialog)
            }
        }

    @Test
    fun `validateDateInputState when result is null expect return alert dialog`() {
        // Given
        val mockDateInputResult =
            TachyonDateInputFieldResultModel(
                resultDate = "",
                errorCode = null,
            )
        viewModel.setDateInputResult(mockDateInputResult)
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
        every { resourceUtils.getString(R.string.text_please_input_expire_date) } returns "Please input expire date"

        // When
        val result = viewModel.validateDateInputState()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `validateDateInputState when error code is null expect return null`() {
        // Given
        val mockDateInputResult =
            TachyonDateInputFieldResultModel(
                resultDate = "01/01/2024",
                errorCode = null,
            )
        viewModel.setDateInputResult(mockDateInputResult)

        // When
        val result = viewModel.validateDateInputState()

        // Then
        assertEquals(null, result)
    }

    @Test
    fun `validateDateInputState when error code is INCOMPLETE_DATE expect return correct alert dialog`() {
        // Given
        val mockDateInputResult =
            TachyonDateInputFieldResultModel(
                resultDate = "",
                errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INCOMPLETE_DATE,
            )
        viewModel.setDateInputResult(mockDateInputResult)
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
        every { resourceUtils.getString(R.string.text_please_input_expire_date) } returns "Please input expire date"

        // When
        val result = viewModel.validateDateInputState()

        // Then
        assertEquals(ErrorType.WARNING, result?.type)
        assertEquals("Unable to continue", result?.titleText)
        assertEquals("Please input expire date", result?.messageText)
        assertEquals(R.string.text_close, result?.positiveTextRes)
    }

    @Test
    fun `validateDateInputState when error code is INVALID_DATE_FORMAT expect return correct alert dialog`() {
        // Given
        val mockDateInputResult =
            TachyonDateInputFieldResultModel(
                resultDate = "invalid",
                errorCode = ErrorCodes.DateInputFieldValidationErrorCodes.INVALID_DATE_FORMAT,
            )
        viewModel.setDateInputResult(mockDateInputResult)
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
        every { resourceUtils.getString(R.string.text_expiration_date_is_incorrect) } returns "Date is incorrect"

        // When
        val result = viewModel.validateDateInputState()

        // Then
        assertEquals(ErrorType.WARNING, result?.type)
        assertEquals("Unable to continue", result?.titleText)
        assertEquals("Date is incorrect", result?.messageText)
        assertEquals(R.string.text_close, result?.positiveTextRes)
    }

    @Test
    fun `getDefaultInitialDate when dateInputLD is empty expect return current year with slashes`() {
        // Given
        viewModel.dateInputLD.value = ""

        // When
        val result = viewModel.getDefaultInitialDate()

        // Then
        assertTrue(result.isNotEmpty())
    }

    @Test
    fun `getDefaultInitialDate when dateInputLD has value expect return that value`() {
        // Given
        val mockDate = "01/01/2024"
        viewModel.dateInputLD.value = mockDate

        // When
        val result = viewModel.getDefaultInitialDate()

        // Then
        assertEquals(mockDate, result)
    }

    @Test
    fun `logEventSubmitUpdateItem should log with correct action and message`() =
        runTest {
            // Given
            val documentData =
                ReprintQrFreshItemUIModel(
                    barcode = "1111111",
                    skuName = "Test Product",
                    expireDate = "01/01/2024",
                    qrPrintCount = 2,
                )

            // When
            viewModel.logEventSubmitUpdateItem(documentData)

            // Then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_UPDATE_ITEM,
                    message = documentData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
