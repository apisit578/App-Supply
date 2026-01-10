package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.OrderReceiveToteDiscountType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitItemModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitMappingModel
import th.co.cjexpress.store.sage.domain.model.reprintQrFresh.QrDocumentSubmitModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote.SdToteDiscountsConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ReprintQrFreshLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.reprintQrFresh.ShipmentQrDocumentSubmitUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshItemUIModel
import th.co.cjexpress.store.sage.presentation.model.reprintQrFresh.ReprintQrFreshUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemsViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

@ExperimentalCoroutinesApi
class ReprintQrFreshItemsViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    private lateinit var sdToteDiscountsConfigUseCase: SdToteDiscountsConfigUseCase

    @RelaxedMockK
    private lateinit var shipmentQrDocumentSubmitUseCase: ShipmentQrDocumentSubmitUseCase

    @RelaxedMockK
    private lateinit var reprintQrFreshLiveDataUseCase: ReprintQrFreshLiveDataUseCase

    @RelaxedMockK
    private lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    private lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: ReprintQrFreshItemsViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            ReprintQrFreshItemsViewModel(
                sdToteDiscountsConfigUseCase = sdToteDiscountsConfigUseCase,
                shipmentQrDocumentSubmitUseCase = shipmentQrDocumentSubmitUseCase,
                reprintQrFreshLiveDataUseCase = reprintQrFreshLiveDataUseCase,
                uiModelMapper = uiModelMapper,
                dispatcherProvider = TestDispatchers(),
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun `searchBarcode when item exists should set success state`() =
        runTest {
            // Given
            val barcode = "123456"
            val existingItem =
                ReprintQrFreshItemUIModel(
                    barcode = barcode,
                    skuName = "Test Item",
                    expireDate = "",
                    qrPrintCount = 1,
                )
            every { reprintQrFreshLiveDataUseCase.getProductQrFreshItem(barcode) } returns existingItem

            // When
            viewModel.searchBarcode(barcode)

            // Then
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Success<*>)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `searchBarcode when getProductQrFreshItem is null should call getProductDiscountConfigs`() =
        runTest {
            // Given
            val barcode = "123456"
            val configModel =
                SdToteDiscountsConfigModel(
                    barcode = barcode,
                    skuName = "Test Item",
                    discountType = OrderReceiveToteDiscountType.FRESHLIFE.name,
                    skuCode = "",
                    unitName = "",
                    noOfExp = 1,
                )
            coEvery { sdToteDiscountsConfigUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        configModel,
                    ),
                )
            every { reprintQrFreshLiveDataUseCase.getProductQrFreshItem(barcode) } returns null

            // When
            viewModel.searchBarcode(barcode)

            // Then
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Success<*>)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getProductDiscountConfigs when success should validate discount type`() =
        runTest {
            // Given
            val barcode = "123456"
            val configModel =
                SdToteDiscountsConfigModel(
                    barcode = barcode,
                    skuName = "Test Item",
                    discountType = OrderReceiveToteDiscountType.FRESHLIFE.name,
                    skuCode = "",
                    unitName = "",
                    noOfExp = 1,
                )
            coEvery { sdToteDiscountsConfigUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        configModel,
                    ),
                )

            // When
            viewModel.getProductDiscountConfigs(barcode)

            // Then
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Success<*>)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `getProductDiscountConfigs when error should set error state`() =
        runTest {
            // Given
            val barcode = "123456"
            val errorMessage = "Error occurred"
            coEvery { sdToteDiscountsConfigUseCase.execute(barcode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "ERROR",
                                type = ErrorType.ERROR,
                                message = errorMessage,
                            ),
                    ),
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"

            // When
            viewModel.getProductDiscountConfigs(barcode)

            // Then
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `submitQrDocument when success should show success dialog`() =
        runTest {
            // Given
            val documentData =
                QrDocumentSubmitModel(
                    lotNumber = "L000000000",
                    data = emptyList(),
                )
            coEvery { shipmentQrDocumentSubmitUseCase.execute(documentData) } returns
                flowOf(Result.Success(QrDocumentSubmitMappingModel("DOC001")))
            every { resourceUtils.getString(R.string.text_save_success) } returns "Save Success"
            every { resourceUtils.getString(R.string.text_go_to_reprint_qr_menu_on_posadmin) } returns "Go to reprint"

            // When
            viewModel.submitQrDocument(documentData)

            // Then
            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `performActionToMainMenu when items not empty should show dialog`() =
        runTest {
            // Given
            val confirmTitle = "Confirm Exit"
            val confirmMessage = "Are you sure you want to exit?"
            every { resourceUtils.getString(R.string.text_confirm_exit_title_dialog) } returns confirmTitle
            every { resourceUtils.getString(R.string.text_confirm_exit_detail_dialog) } returns confirmMessage
            every { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value } returns
                ReprintQrFreshUIModel(
                    items =
                        mutableStateListOf(
                            ReprintQrFreshItemUIModel(
                                barcode = "123456",
                                skuName = "Test Item",
                                expireDate = "",
                                qrPrintCount = 1,
                            ),
                        ),
                )

            // When
            viewModel.performActionToMainMenu()

            // Then
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData

                assertEquals(ErrorType.QUESTION, dialog.type)
                assertEquals(confirmTitle, dialog.titleText)
                assertEquals(confirmMessage, dialog.messageText)
                assertEquals(R.string.text_confirm, dialog.positiveTextRes)
                assertEquals(R.string.text_cancel, dialog.negativeTextRes)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeBackToMainMenu should show confirmation dialog with correct properties`() =
        runTest {
            // Given
            val confirmTitle = "Confirm Exit"
            val confirmMessage = "Are you sure you want to exit?"
            every { resourceUtils.getString(R.string.text_confirm_exit_title_dialog) } returns confirmTitle
            every { resourceUtils.getString(R.string.text_confirm_exit_detail_dialog) } returns confirmMessage

            // When
            viewModel.promptBeforeBackToMainMenu()

            // Then
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData

                assertEquals(ErrorType.QUESTION, dialog.type)
                assertEquals(confirmTitle, dialog.titleText)
                assertEquals(confirmMessage, dialog.messageText)
                assertEquals(R.string.text_confirm, dialog.positiveTextRes)
                assertEquals(R.string.text_cancel, dialog.negativeTextRes)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeBackToMainMenu when positive button clicked should reset and navigate to main menu`() =
        runTest {
            // Given
            every { resourceUtils.getString(any()) } returns ""

            // When
            viewModel.promptBeforeBackToMainMenu()

            // Then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                // Click positive button
                dialog.onPositiveClick?.invoke()

                // Verify resetLiveData was called
                verify {
                    reprintQrFreshLiveDataUseCase.resetQrFreshItemList()
                    reprintQrFreshLiveDataUseCase.resetQrFreshItem()
                }

                // Verify navigation state
                val navigationState = awaitItem()
                assertTrue(navigationState is BaseScreenState.Success<*>)
                assertEquals(
                    ReprintQrFreshItemsViewModel.UIState.NavToMainMenu,
                    (navigationState as BaseScreenState.Success<*>).data,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeBackToMainMenu when negative button clicked should reset screen state`() =
        runTest {
            // Given
            every { resourceUtils.getString(any()) } returns ""

            // When
            viewModel.promptBeforeBackToMainMenu()

            // Then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                // Click negative button
                dialog.onNegativeClick?.invoke()

                // Verify screen state is reset
                assertEquals(BaseScreenState.Idle, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `validateDiscountType when type is FRESHLIFE should set success state`() =
        runTest {
            // Given
            val data =
                SdToteDiscountsConfigModel(
                    barcode = "123456",
                    skuName = "Test Item",
                    discountType = OrderReceiveToteDiscountType.FRESHLIFE.name,
                    skuCode = "",
                    unitName = "",
                    noOfExp = 1,
                )

            // When
            viewModel.validateDiscountType(data)

            // Then
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Success<*>)
                val success = state as BaseScreenState.Success<*>
                val model = success.data as ReprintQrFreshItemUIModel
                assertEquals(data.barcode, model.barcode)
                assertEquals(data.skuName, model.skuName)
                assertEquals("", model.expireDate)
                assertEquals(1, model.qrPrintCount)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `validateDiscountType when type is not FRESHLIFE should set error state`() =
        runTest {
            // Given
            val data =
                SdToteDiscountsConfigModel(
                    barcode = "123456",
                    skuName = "Test Item",
                    discountType = "OTHER",
                    skuCode = "",
                    unitName = "",
                    noOfExp = 1,
                )
            every { resourceUtils.getString(R.string.text_title_barcode_mismatch) } returns "Barcode Mismatch"
            every { resourceUtils.getString(R.string.text_please_check_and_scan_again) } returns "Please check and scan again"

            // When
            viewModel.validateDiscountType(data)

            // Then
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val error = state as BaseScreenState.Error
                val dialog = error.errorData
                assertEquals(ErrorType.WARNING, dialog.type)
                assertEquals("Barcode Mismatch", dialog.titleText)
                assertEquals("Please check and scan again", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `validateDiscountType when type is not FRESHLIFE should call resetLiveData on positive click`() =
        runTest {
            // Given
            val data =
                SdToteDiscountsConfigModel(
                    barcode = "123456",
                    skuName = "Test Item",
                    discountType = "OTHER",
                    skuCode = "",
                    unitName = "",
                    noOfExp = 1,
                )
            every { resourceUtils.getString(any()) } returns ""

            // When
            viewModel.validateDiscountType(data)

            // Then
            viewModel.baseScreenState.test {
                // Click positive button for resetState
                val state = awaitItem() as BaseScreenState.Error
                state.errorData.onPositiveClick?.invoke()

                // Check if there is a new state of Idle
                val resetState = awaitItem()
                assertTrue(resetState is BaseScreenState.Idle)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `resetLiveData should call reset methods`() {
        // When
        viewModel.resetLiveData()

        // Then
        verify {
            reprintQrFreshLiveDataUseCase.resetQrFreshItemList()
            reprintQrFreshLiveDataUseCase.resetQrFreshItem()
        }
    }

    @Test
    fun `prepareEditingItem should set item and reset state`() {
        // Given
        val item = mockk<ReprintQrFreshItemUIModel>()

        // When
        viewModel.prepareEditingItem(item)

        // Then
        verify {
            reprintQrFreshLiveDataUseCase.setQrFreshItem(item)
        }
    }

    @Test
    fun `promptBeforeSubmit should show confirmation dialog with correct properties`() =
        runTest {
            // Given
            val confirmTitle = "Confirm Save"
            val confirmMessage = "Do you want to save?"
            val mockItems = mutableStateListOf(mockk<ReprintQrFreshItemUIModel>())
            val mockSubmitItems = listOf(mockk<QrDocumentSubmitItemModel>())

            every { resourceUtils.getString(R.string.text_confirm_to_save_document) } returns confirmTitle
            every { resourceUtils.getString(R.string.text_question_confirm_to_save_qr_document) } returns confirmMessage
            every { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value?.items } returns mockItems
            every {
                uiModelMapper.reprintQrFreshSubmitUIMapper.toQrDocumentSubmitItems(mockItems)
            } returns mockSubmitItems

            // When
            viewModel.promptBeforeSubmit()

            // Then
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assertTrue(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData

                assertEquals(ErrorType.QUESTION, dialog.type)
                assertEquals(confirmTitle, dialog.titleText)
                assertEquals(confirmMessage, dialog.messageText)
                assertEquals(R.string.text_yes, dialog.positiveTextRes)
                assertEquals(R.string.text_no, dialog.negativeTextRes)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeSubmit when positive button clicked should submit document`() =
        runTest {
            // Given
            val mockItems = mutableStateListOf(mockk<ReprintQrFreshItemUIModel>())
            val mockSubmitItems = listOf(mockk<QrDocumentSubmitItemModel>())

            every { resourceUtils.getString(any()) } returns ""
            every { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value } returns
                ReprintQrFreshUIModel(
                    items = mutableStateListOf(),
                )

            every {
                uiModelMapper.reprintQrFreshSubmitUIMapper.toQrDocumentSubmitItems(mockItems)
            } returns mockSubmitItems

            // When
            viewModel.promptBeforeSubmit()

            // Then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                // Verify submitQrDocument is called with correct parameters when clicking positive button
                dialog.onPositiveClick?.invoke()

                verify {
                    uiModelMapper.reprintQrFreshSubmitUIMapper.toQrDocumentSubmitItems(any())
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeSubmit when productQrFreshDetail null positive button clicked should submit document`() =
        runTest {
            // Given
            val mockItems = mutableStateListOf(mockk<ReprintQrFreshItemUIModel>())
            val mockSubmitItems = listOf(mockk<QrDocumentSubmitItemModel>())

            every { resourceUtils.getString(any()) } returns ""
            every { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value } returns null

            every {
                uiModelMapper.reprintQrFreshSubmitUIMapper.toQrDocumentSubmitItems(mockItems)
            } returns mockSubmitItems

            // When
            viewModel.promptBeforeSubmit()

            // Then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                // Verify submitQrDocument is called with correct parameters when clicking positive button
                dialog.onPositiveClick?.invoke()

                verify {
                    uiModelMapper.reprintQrFreshSubmitUIMapper.toQrDocumentSubmitItems(any())
                }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptBeforeSubmit when negative button clicked should reset screen state`() =
        runTest {
            // Given
            every { resourceUtils.getString(any()) } returns ""

            // When
            viewModel.promptBeforeSubmit()

            // Then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                // Verify resetBaseScreenState is called when clicking negative button
                dialog.onNegativeClick?.invoke()

                // Verify the next state is Idle after reset
                assertEquals(BaseScreenState.Idle, awaitItem())

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `get documentDetailLD should call productQrFreshDetail`() =
        runTest {
            // when
            viewModel.documentDetailLD

            // then
            verify { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value }
        }

    @Test
    fun `logEventScanBarcode should log with correct action and message`() =
        runTest {
            // Given
            val barcode = "12345678"

            // When
            viewModel.logEventScanBarcode(barcode)

            // Then
            verify {
                logUtils.logInfo(
                    action = LogAction.SCAN_BARCODE,
                    message = barcode,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSubmitReprintQrDocument should log with correct action and message`() =
        runTest {
            // Given
            val documentData =
                QrDocumentSubmitModel(
                    lotNumber = "L000000000",
                    data = listOf(),
                )

            // When
            viewModel.logEventSubmitReprintQrDocument(documentData)

            // Then
            verify {
                logUtils.logInfo(
                    action = LogAction.SUBMIT_REPRINT_QR_DOCUMENT,
                    message = documentData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectItem should log with correct action and message`() =
        runTest {
            // Given
            val itemData =
                ReprintQrFreshItemUIModel(
                    barcode = "12345678",
                    skuName = "Test Item",
                    expireDate = "2024-12-31",
                    qrPrintCount = 1,
                )

            // When
            viewModel.logEventSelectItem(itemData)

            // Then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_ITEM,
                    message = itemData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `handleDisplayError negativeTextRes when canRetry true should be text_close`() =
        runTest {
            // given
            val barcode = "BC1"
            every { resourceUtils.getString(any()) } returns ""
            val cause = BusinessException(errorCode = "X", type = ErrorType.ERROR, message = "m")
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = true)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals(R.string.text_close, dialog.negativeTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError positiveTextRes when canRetry true should be text_retry`() =
        runTest {
            // given
            val barcode = "BC2"
            every { resourceUtils.getString(any()) } returns ""
            val cause = BusinessException(errorCode = "X", type = ErrorType.ERROR, message = "m")
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = true)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals(R.string.text_retry, dialog.positiveTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError NOT_FOUND should map title and message with barcode`() =
        runTest {
            // given
            val barcode = "BC3"
            every { resourceUtils.getString(R.string.text_data_not_found) } returns "Data not found"
            every { resourceUtils.getString(R.string.text_purchase_order_barcode, barcode) } returns "PO barcode $barcode"
            val cause = BusinessException(errorCode = ErrorCodes.NOT_FOUND, type = ErrorType.WARNING, message = null)
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = false)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals("Data not found", dialog.titleText)
                assertEquals("PO barcode $barcode", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError title fallback when cause title null should use unable_to_continue`() =
        runTest {
            // given
            val barcode = "BC4"
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            val cause = BusinessException(errorCode = "X", type = ErrorType.ERROR, title = null, message = "m")
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = false)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals("Unable to continue", dialog.titleText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError INVALID_BODY should use scan_or_enter_barcode message`() =
        runTest {
            // given
            val barcode = "BC5"
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_scan_or_enter_barcode) } returns "Please scan or enter"
            val cause = BusinessException(errorCode = ErrorCodes.INVALID_BODY, type = ErrorType.ERROR, title = null, message = null)
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = false)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals("Please scan or enter", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError showF1Message should be true when type is ERROR`() =
        runTest {
            // given
            val barcode = "BC6"
            every { resourceUtils.getString(any()) } returns ""
            val cause = BusinessException(errorCode = "X", type = ErrorType.ERROR, message = "boom")
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = false)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertTrue(dialog.showF1Message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError onNegativeClick should resetBaseScreenState to Idle`() =
        runTest {
            // given
            val barcode = "BC7"
            every { resourceUtils.getString(any()) } returns ""
            val cause = BusinessException(errorCode = "X", type = ErrorType.WARNING, message = "m")
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = true)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                dialog.onNegativeClick?.invoke()
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError title null should fallback to unable_to_continue`() =
        runTest {
            // given
            val barcode = "BC_TITLE_FALLBACK"
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            val cause =
                BusinessException(
                    errorCode = "X",
                    type = ErrorType.ERROR,
                    title = null,
                    message = "m",
                )
            val result =
                Result.Error<SdToteDiscountsConfigModel>(
                    cause = cause,
                    canRetry = false,
                )

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals("Unable to continue", dialog.titleText)
                assertEquals("m", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError title not null should use cause title`() =
        runTest {
            // given
            val barcode = "BC_TITLE_DIRECT"
            val cause =
                BusinessException(
                    errorCode = "X",
                    type = ErrorType.ERROR,
                    title = "Direct Title",
                    message = "m",
                )
            val result =
                Result.Error<SdToteDiscountsConfigModel>(
                    cause = cause,
                    canRetry = false,
                )

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals("Direct Title", dialog.titleText)
                assertEquals("m", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError message null should fallback to empty string`() =
        runTest {
            // given
            val barcode = "BC_MESSAGE_NULL"
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            val cause =
                BusinessException(
                    errorCode = "X",
                    type = ErrorType.ERROR,
                    title = "Some Title",
                    message = null,
                )
            val result =
                Result.Error<SdToteDiscountsConfigModel>(
                    cause = cause,
                    canRetry = false,
                )

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                assertEquals("Some Title", dialog.titleText)
                assertEquals("", dialog.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError onPositiveClick when canRetry false should only reset to Idle`() =
        runTest {
            // given
            val barcode = "BC_NO_RETRY"
            every { resourceUtils.getString(any()) } returns ""
            val cause =
                BusinessException(
                    errorCode = "X",
                    type = ErrorType.WARNING,
                    title = "T",
                    message = "M",
                )
            val result =
                Result.Error<SdToteDiscountsConfigModel>(
                    cause = cause,
                    canRetry = false,
                )

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                dialog.onPositiveClick?.invoke()

                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError onNegativeClick should reset to Idle`() =
        runTest {
            // given
            val barcode = "BC_RESET"
            val cause =
                BusinessException(
                    errorCode = "X",
                    type = ErrorType.WARNING,
                    title = "T",
                    message = "M",
                )
            val result =
                Result.Error<SdToteDiscountsConfigModel>(
                    cause = cause,
                    canRetry = true,
                )

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData
                dialog.onNegativeClick?.invoke()
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleDisplayError onPositiveClick when canRetry true should call getProductDiscountConfigs`() =
        runTest {
            // given
            val barcode = "BC_CAN_RETRY"
            every { resourceUtils.getString(any()) } returns ""
            every { resourceUtils.getString(any(), any()) } returns ""

            val successModel =
                SdToteDiscountsConfigModel(
                    barcode = barcode,
                    skuName = "OK",
                    discountType = OrderReceiveToteDiscountType.FRESHLIFE.name,
                    skuCode = "",
                    unitName = "",
                    noOfExp = 1,
                )
            coEvery { sdToteDiscountsConfigUseCase.execute(barcode) } returns flowOf(Result.Success(successModel))

            val cause = BusinessException(errorCode = "X", type = ErrorType.ERROR, title = null, message = "boom")
            val result = Result.Error<SdToteDiscountsConfigModel>(cause = cause, canRetry = true)

            // when
            viewModel.handleDisplayError(result, barcode)

            // then
            viewModel.baseScreenState.test {
                val dialog = (awaitItem() as BaseScreenState.Error).errorData

                dialog.onPositiveClick?.invoke()

                coVerify(exactly = 1) { sdToteDiscountsConfigUseCase.execute(barcode) }

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `performActionToMainMenu when items non empty should call promptBeforeBackToMainMenu`() =
        runTest {
            // given
            val spiedVm = spyk(viewModel)
            every { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value } returns
                ReprintQrFreshUIModel(
                    items =
                        mutableStateListOf(
                            ReprintQrFreshItemUIModel(
                                barcode = "123456",
                                skuName = "Test Item",
                                expireDate = "",
                                qrPrintCount = 1,
                            ),
                        ),
                )

            // when
            spiedVm.performActionToMainMenu()

            // then
            verify(exactly = 1) { spiedVm.promptBeforeBackToMainMenu() }
        }

    @Test
    fun `performActionToMainMenu when data null should navigate to main menu`() =
        runTest {
            // given
            every { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value } returns null

            // when
            viewModel.performActionToMainMenu()

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(ReprintQrFreshItemsViewModel.UIState.NavToMainMenu),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `performActionToMainMenu when items empty should navigate to main menu`() =
        runTest {
            // given
            every { reprintQrFreshLiveDataUseCase.productQrFreshDetail.value } returns
                ReprintQrFreshUIModel(items = mutableStateListOf())

            // when
            viewModel.performActionToMainMenu()

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(ReprintQrFreshItemsViewModel.UIState.NavToMainMenu),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }
}
