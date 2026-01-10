package th.co.cjexpress.store.sage.presentation.viewmodel

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.mutableStateListOf
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceValidateBillModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceCalculateUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.SaveDraftPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.ValidatePurchaseInvoiceBillUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceCalculateUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceSummaryViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceSummaryViewModel.UIState
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class PurchaseInvoiceSummaryViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var purchaseInvoiceCalculateUseCase: PurchaseInvoiceCalculateUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceUseCase: PurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    @RelaxedMockK
    lateinit var validatePurchaseInvoiceBillUseCase: ValidatePurchaseInvoiceBillUseCase

    @RelaxedMockK
    lateinit var saveDraftPurchaseInvoiceUseCase: SaveDraftPurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    // viewModel
    private lateinit var viewModel: PurchaseInvoiceSummaryViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            PurchaseInvoiceSummaryViewModel(
                resourceUtils = resourceUtils,
                purchaseInvoiceUseCase = purchaseInvoiceUseCase,
                purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
                purchaseInvoiceCalculateUseCase = purchaseInvoiceCalculateUseCase,
                validatePurchaseInvoiceBillUseCase = validatePurchaseInvoiceBillUseCase,
                saveDraftPurchaseInvoiceUseCase = saveDraftPurchaseInvoiceUseCase,
                uiModelMapper = uiModelMapper,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_getSupplierLD_should_return_value_from_purchaseInvoiceUseCase() =
        runTest {
            val mockSupplier = mockk<PurchaseInvoiceSupplierDetailUIModel>()
            every { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns mockSupplier
            assertEquals(mockSupplier, viewModel.supplierLD)
        }

    @Test
    fun test_setConfirmSaveDialogState_should_update_showConfirmSaveDialogState() =
        runTest {
            viewModel.setConfirmSaveDialogState(true)
            assertTrue(viewModel.showConfirmSaveDialogState.value)

            viewModel.setConfirmSaveDialogState(false)
            assertFalse(viewModel.showConfirmSaveDialogState.value)
        }

    @Test
    fun test_setShowInvoiceInputDialogState_should_update_showInvoiceInputDialogState() =
        runTest {
            viewModel.setShowInvoiceInputDialogState(true)
            assertTrue(viewModel.showInvoiceInputDialogState.value)

            viewModel.setShowInvoiceInputDialogState(false)
            assertFalse(viewModel.showInvoiceInputDialogState.value)
        }

    @Test
    fun test_calculatePurchaseInvoice_should_emit_success_and_call_setBaseScreenSuccessState() =
        runTest {
            val mappedUI = mockk<PurchaseInvoiceCalculateUIModel>()
            every { purchaseInvoiceCalculateUseCase.execute() } returns
                flowOf(
                    Result.Success(
                        mockk(
                            relaxed = true,
                        ),
                    ),
                )
            every { uiModelMapper.purchaseInvoiceCalculateUIMapper.mapUIModel(any()) } returns mappedUI

            viewModel.calculatePurchaseInvoice()

            viewModel.baseScreenState.test {
                assertEquals(mappedUI, viewModel.purchaseInvoiceCalculatedState.value)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_calculatePurchaseInvoice_throws_and_is_caught() =
        runTest {
            val exception = RuntimeException("Unexpected crash")

            every { purchaseInvoiceCalculateUseCase.execute() } returns flow { throw exception }

            viewModel.calculatePurchaseInvoice()

            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_validatePurchaseInvoiceBill_success_and_isValid_should_call_saveDraft() =
        runTest {
            val billNo = "billNo"
            val piNo = "piNo"
            val supplierCode = "supplierCode"
            val mockResult =
                Result.Success(
                    mockk<PurchaseInvoiceValidateBillModel> {
                        every { isValid } returns true
                    },
                )
            every {
                validatePurchaseInvoiceBillUseCase.execute(
                    billNo,
                    piNo,
                    supplierCode,
                )
            } returns
                flowOf(
                    mockResult,
                )
            val mockEntry = mockk<PurchaseInvoiceEntryUIModel>()
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                mockk {
                    every { entries } returns mutableStateListOf(mockEntry)
                }

            viewModel.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)

            coVerify {
                saveDraftPurchaseInvoiceUseCase.executeList(any())
            }
        }

    @Test
    fun test_validatePurchaseInvoiceBill_success_and_not_valid_should_show_duplicate_bill_error() =
        runTest {
            val billNo = "billNo"
            val piNo = "piNo"
            val supplierCode = "supplierCode"
            val mockResult =
                Result.Success(
                    mockk<PurchaseInvoiceValidateBillModel> {
                        every { isValid } returns false
                    },
                )
            every {
                validatePurchaseInvoiceBillUseCase.execute(
                    billNo,
                    piNo,
                    supplierCode,
                )
            } returns
                flowOf(
                    mockResult,
                )
            every { resourceUtils.getString(R.string.text_error_duplicate_bill_no) } returns "Duplicate bill"
            every { resourceUtils.getString(R.string.text_error_check_information) } returns "Please check"

            viewModel.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)

            viewModel.baseScreenState.test {
                val item = awaitItem()
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals(ErrorType.WARNING, it.type)
                    assertEquals("Duplicate bill", it.titleText)
                    assertEquals("Please check", it.messageText)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_validatePurchaseInvoiceBill_throw_exception_should_show_retry_dialog() =
        runTest {
            val billNo = "billNo"
            val piNo = "piNo"
            val supplierCode = "supplierCode"
            val exception = RuntimeException("Unexpected crash")

            every {
                validatePurchaseInvoiceBillUseCase.execute(
                    billNo,
                    piNo,
                    supplierCode,
                )
            } returns flow { throw exception }
            viewModel.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)

            viewModel.baseScreenState.test {
                val item = awaitItem()
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals("Unexpected crash", it.messageText)
                    assertEquals(ErrorType.ERROR, it.type)
                    assertEquals(true, it.showF1Message)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_success_expect_updateCalled() =
        runTest {
            // given
            val mockProduct: List<IPurchaseInvoiceProductsModel> = mockk<List<IPurchaseInvoiceProductsModel>>()
            val mockProductModel =
                PurchaseInvoiceDraftModel(
                    code = "",
                    ref = "",
                    message = "",
                    timestamp = "",
                    data =
                        PurchaseInvoiceDraftDataModel(
                            piNo = "1234567890",
                            items =
                                listOf(
                                    PurchaseInvoiceDraftItemModel(
                                        barcode = "123456789",
                                        actualQty = 1,
                                        qty = 10,
                                        unitFactor = 1,
                                        unitName = "pcs",
                                        productName = "Test Product",
                                        hhTimestamp = 0L,
                                    ),
                                    PurchaseInvoiceDraftItemModel(
                                        barcode = "1111111",
                                        actualQty = 1,
                                        qty = 10,
                                        unitFactor = 1,
                                        unitName = "pcs",
                                        productName = "Test Product2",
                                        hhTimestamp = 15000L,
                                    ),
                                ),
                        ),
                )
            coEvery { saveDraftPurchaseInvoiceUseCase.executeList(mockProduct) } returns
                flowOf(
                    Result
                        .Success(mockProductModel),
                )
            val piNo = "piNo"
            val product = mockk<IPurchaseInvoiceProductsModel>()
            val result =
                Result.Success(
                    mockk<PurchaseInvoiceDraftModel> {
                        every { data.piNo } returns piNo
                    },
                )
            every { saveDraftPurchaseInvoiceUseCase.execute(product) } returns
                flowOf(
                    result,
                )

            // when
            viewModel.saveDraftPurchaseInvoice(mockProduct)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(UIState.SaveDraftSuccess(piNo = "1234567890")),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_throw_exception_should_show_error_dialog() =
        runTest {
            val product = mockk<List<IPurchaseInvoiceProductsModel>>()
            val exception = RuntimeException("Unexpected crash")

            every { saveDraftPurchaseInvoiceUseCase.executeList(product) } returns flow { throw exception }
            viewModel.saveDraftPurchaseInvoice(product)

            viewModel.baseScreenState.test {
                val item = awaitItem()
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals("Unexpected crash", it.messageText)
                    assertEquals(ErrorType.ERROR, it.type)
                    assertEquals(true, it.showF1Message)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getIsRefPo_should_return_value_from_purchaseInvoiceUseCase() =
        runTest {
            every { purchaseInvoiceUseCase.isRefPo() } returns true
            assertTrue(viewModel.isRefPo)
        }

    @Test
    fun test_setConfirmBackMainMenuDialogState_should_update_confirmBackMainMenuDialogState() =
        runTest {
            viewModel.setConfirmBackMainMenuDialogState(true)
            assertTrue(viewModel.confirmBackMainMenuDialogState.value)

            viewModel.setConfirmBackMainMenuDialogState(false)
            assertFalse(viewModel.confirmBackMainMenuDialogState.value)
        }

    @Test
    fun test_setConfirmBackDialogState_should_update_confirmBackDialogState() =
        runTest {
            viewModel.setConfirmBackDialogState(true)
            assertTrue(viewModel.confirmBackDialogState.value)

            viewModel.setConfirmBackDialogState(false)
            assertFalse(viewModel.confirmBackDialogState.value)
        }

    @Test
    fun test_validatePurchaseInvoiceBill_error_invalid_parameter_should_show_warning_dialog() =
        runTest {
            val billNo = "billNo"
            val piNo = "piNo"
            val supplierCode = "supplierCode"
            every { validatePurchaseInvoiceBillUseCase.execute(billNo, piNo, supplierCode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            mockk {
                                every { type } returns ErrorType.WARNING
                                every { title } returns "Title"
                                every { message } returns "Invalid data"
                                every { errorCode } returns ErrorCodes.INVALID_PARAMETER
                                every { ref } returns null
                            },
                    ),
                )

            viewModel.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val item = if (first is BaseScreenState.Loading) awaitItem() else first
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals("Title", it.titleText)
                    assertEquals("Invalid data", it.messageText)
                    assertEquals(ErrorType.WARNING, it.type)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_validatePurchaseInvoiceBill_error_not_found_doc_should_show_warning_dialog() =
        runTest {
            val billNo = "billNo"
            val piNo = "1234"
            val supplierCode = "supplierCode"

            val expectedTitle = "Data not found"
            val expectedMessage = "ไม่พบเอกสารเลขที่ $piNo"

            every { resourceUtils.getString(R.string.text_data_not_found) } returns expectedTitle
            every {
                resourceUtils.getString(
                    R.string.text_document_no_purchase_invoice_pi,
                    piNo,
                )
            } returns expectedMessage

            every { validatePurchaseInvoiceBillUseCase.execute(billNo, piNo, supplierCode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            mockk {
                                every { type } returns ErrorType.WARNING
                                every { title } returns expectedTitle
                                every { message } returns expectedMessage
                                every { errorCode } returns ErrorCodes.NOT_FOUND
                                every { ref } returns null
                            },
                    ),
                )

            viewModel.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val item = if (first is BaseScreenState.Loading) awaitItem() else first
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals(expectedTitle, it.titleText)
                    assertEquals(expectedMessage, it.messageText)
                    assertEquals(ErrorType.WARNING, it.type)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_error_should_show_warning_dialog() =
        runTest {
            val product = mockk<List<IPurchaseInvoiceProductsModel>>()
            every { saveDraftPurchaseInvoiceUseCase.executeList(product) } returns
                flowOf(
                    Result.Error(
                        cause =
                            mockk {
                                every { type } returns ErrorType.WARNING
                                every { title } returns "Title"
                                every { message } returns "Invalid data"
                                every { errorCode } returns ErrorCodes.INVALID_PARAMETER
                                every { ref } returns null
                            },
                    ),
                )

            viewModel.saveDraftPurchaseInvoice(product)

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val item = if (first is BaseScreenState.Loading) awaitItem() else first
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals("Title", it.titleText)
                    assertEquals("Invalid data", it.messageText)
                    assertEquals(ErrorType.WARNING, it.type)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_calculatePurchaseInvoice_error_with_warning_type() =
        runTest {
            every { purchaseInvoiceCalculateUseCase.execute() } returns
                flowOf(
                    Result.Error(
                        cause =
                            mockk {
                                every { type } returns ErrorType.WARNING
                                every { title } returns "Custom Title"
                                every { message } returns "Bad Request"
                                every { errorCode } returns ErrorCodes.BAD_REQUEST
                                every { ref } returns null
                            },
                    ),
                )

            viewModel.calculatePurchaseInvoice()

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val item = if (first is BaseScreenState.Loading) awaitItem() else first
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals("Custom Title", it.titleText)
                    assertEquals("Bad Request", it.messageText)
                    assertEquals(false, it.showF1Message)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_calculatePurchaseInvoice_error_with_title_and_show_retry_button() =
        runTest {
            every { purchaseInvoiceCalculateUseCase.execute() } returns
                flowOf(
                    Result.Error(
                        cause =
                            mockk {
                                every { type } returns ErrorType.ERROR
                                every { title } returns "Custom Title"
                                every { message } returns "Bad Request"
                                every { errorCode } returns ErrorCodes.BAD_REQUEST
                                every { ref } returns null
                            },
                    ),
                )

            viewModel.calculatePurchaseInvoice()

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val item = if (first is BaseScreenState.Loading) awaitItem() else first
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                error.errorData.let {
                    assertEquals("Custom Title", it.titleText)
                    assertEquals("Bad Request", it.messageText)
                    assertEquals(true, it.showF1Message)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_updatePurchaseInvoiceDetailBillNo_should_update() =
        runTest {
            val billNo = "billNo"
            viewModel.updatePurchaseInvoiceDetailBillNo(billNo)

            verify(exactly = 1) {
                purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceDetailBillNo(
                    billNo,
                )
            }
            assertEquals(billNo, viewModel.billNoState.value)
        }

    @Test
    fun `logEventEditInvoiceNumber should logInfo with correct action and message`() =
        runTest {
            // when
            viewModel.logEventEditInvoiceNumber("111111")

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.EDIT_INVOICE_NUMBER,
                    message = "111111",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSavePurchaseInvoice when state true should logInfo with correct action and message`() =
        runTest {
            // when
            viewModel.logEventSavePurchaseInvoice(true)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CONFIRM_SAVE_PURCHASE_INVOICE,
                    message = LogAction.CONFIRM_SAVE_PURCHASE_INVOICE.name,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSavePurchaseInvoice when state false should logInfo with correct action and message`() =
        runTest {
            // when
            viewModel.logEventSavePurchaseInvoice(false)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CANCEL_SAVE_PURCHASE_INVOICE,
                    message = LogAction.CANCEL_SAVE_PURCHASE_INVOICE.name,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun test_hideKeyboard_calls_toggleSoftInput() =
        runTest {
            val imm = mockk<InputMethodManager>(relaxed = true)
            val ctx =
                mockk<Context> {
                    every { getSystemService(Context.INPUT_METHOD_SERVICE) } returns imm
                }

            viewModel.hideKeyboard(ctx)

            verify {
                imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }

    @Test
    fun `validatePurchaseInvoiceBill success isValid false`() =
        runTest {
            every { validatePurchaseInvoiceBillUseCase.execute(any(), any(), any()) } returns
                flowOf(Result.Success(mockk<PurchaseInvoiceValidateBillModel> { every { isValid } returns false }))
            every { resourceUtils.getString(R.string.text_error_duplicate_bill_no) } returns "Duplicate bill"
            every { resourceUtils.getString(R.string.text_error_check_information) } returns "Please check"

            viewModel.validatePurchaseInvoiceBill("B1", "P1", "S1")

            viewModel.baseScreenState.test {
                val item = awaitItem()
                assertTrue(item is BaseScreenState.Error)
                val err = item as BaseScreenState.Error
                assertEquals("Duplicate bill", err.errorData.titleText)
                assertEquals("Please check", err.errorData.messageText)
                coVerify(exactly = 0) { saveDraftPurchaseInvoiceUseCase.execute(any()) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `validatePurchaseInvoiceBill success isValid true do not save`() =
        runTest {
            every { validatePurchaseInvoiceBillUseCase.execute(any(), any(), any()) } returns
                flowOf(Result.Success(mockk<PurchaseInvoiceValidateBillModel> { every { isValid } returns true }))
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns null

            viewModel.validatePurchaseInvoiceBill("B1", "P1", "S1")

            coVerify(exactly = 0) { saveDraftPurchaseInvoiceUseCase.execute(any()) }
        }

    @Test
    fun `validatePurchaseInvoiceBill success isValid true entries empty do not save`() =
        runTest {
            every { validatePurchaseInvoiceBillUseCase.execute(any(), any(), any()) } returns
                flowOf(Result.Success(mockk<PurchaseInvoiceValidateBillModel> { every { isValid } returns true }))
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                mockk(relaxed = true) {
                    every { this@mockk.entries } returns mutableStateListOf() // empty
                }

            viewModel.validatePurchaseInvoiceBill("B1", "P1", "S1")

            coVerify(exactly = 0) { saveDraftPurchaseInvoiceUseCase.execute(any()) }
        }

    @Test
    fun `validatePurchaseInvoiceBill success isValid true entries has first save first entry`() =
        runTest {
            every { validatePurchaseInvoiceBillUseCase.execute(any(), any(), any()) } returns
                flowOf(Result.Success(mockk<PurchaseInvoiceValidateBillModel> { every { isValid } returns true }))
            val firstEntry = mockk<PurchaseInvoiceEntryUIModel>(relaxed = true)
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                mockk(relaxed = true) {
                    every { this@mockk.entries } returns mutableStateListOf(firstEntry, mockk(relaxed = true))
                }

            viewModel.validatePurchaseInvoiceBill("B1", "P1", "S1")

            coVerify(exactly = 1) { saveDraftPurchaseInvoiceUseCase.executeList(any()) }
        }

    @Test
    fun `createRetryAlertDialogData when message is null uses fallback service_failed`() =
        runTest {
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            val dialog =
                viewModel.createRetryAlertDialogData(
                    message = null,
                    onConfirmRetry = {},
                )

            assertEquals("Unable to continue", dialog.titleText)
            assertEquals("Service failed", dialog.messageText)
            assertTrue(dialog.showF1Message)
            assertEquals(ErrorType.ERROR, dialog.type)
        }

    @Test
    fun `createRetryAlertDialogData negative click resets base screen state to Default`() =
        runTest {
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            val dialog =
                viewModel.createRetryAlertDialogData(
                    message = "any",
                    onConfirmRetry = {},
                )

            viewModel.setBaseScreenErrorState(dialog)

            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                dialog.onNegativeClick?.invoke()
                assertTrue(awaitItem() is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showDuplicateBillWarning emits Error with expected title message and type WARNING`() =
        runTest {
            every { resourceUtils.getString(R.string.text_error_duplicate_bill_no) } returns "Duplicate bill"
            every { resourceUtils.getString(R.string.text_error_check_information) } returns "Please check"

            // when
            viewModel.showDuplicateBillWarning()

            // then
            viewModel.baseScreenState.test {
                val item = awaitItem()
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error
                with(error.errorData) {
                    assertEquals(ErrorType.WARNING, type)
                    assertEquals("Duplicate bill", titleText)
                    assertEquals("Please check", messageText)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showDuplicateBillWarning positive click resets BaseScreenState to Idle`() =
        runTest {
            every { resourceUtils.getString(R.string.text_error_duplicate_bill_no) } returns "Duplicate bill"
            every { resourceUtils.getString(R.string.text_error_check_information) } returns "Please check"

            viewModel.showDuplicateBillWarning()

            viewModel.baseScreenState.test {
                // first emission is the Error dialog
                val item = awaitItem()
                assertTrue(item is BaseScreenState.Error)
                val error = item as BaseScreenState.Error

                // act: simulate user pressing positive button
                error.errorData.onPositiveClick?.invoke()

                // assert: state resets to Idle
                val next = awaitItem()
                assertTrue(next is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    private fun buildErrorResult(
        type: ErrorType,
        title: String? = null,
        message: String? = null,
        canRetry: Boolean = false,
    ): Result.Error<PurchaseInvoiceValidateBillModel> {
        val cause =
            mockk<BusinessException>(relaxed = true) {
                every { this@mockk.type } returns type
                every { this@mockk.title } returns title
                every { this@mockk.message } returns message
                every { this@mockk.ref } returns null
            }
        return Result.Error(cause = cause, canRetry = canRetry)
    }

    @Test
    fun `showResultError sets showF1Message true when type ERROR`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = false)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertTrue(item.errorData.showF1Message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError sets showF1Message false when type WARNING`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.WARNING, canRetry = false)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertFalse(item.errorData.showF1Message)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError title falls back to Unable to continue when title is null`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, title = null)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals("Unable", item.errorData.titleText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError message falls back to Service failed when message is null`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, message = null)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals("Failed", item.errorData.messageText)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError negativeTextRes is Close when canRetry true`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = true)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(R.string.text_close, item.errorData.negativeTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError negativeTextRes is null when canRetry false`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = false)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(null, item.errorData.negativeTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError onNegativeClick resets to Idle`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = true)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                err.errorData.onNegativeClick?.invoke()
                val next = awaitItem()
                assertTrue(next is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError positiveTextRes is Retry when canRetry true`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = true)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(R.string.text_retry, item.errorData.positiveTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError positiveTextRes is Close when canRetry false`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = false)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(R.string.text_close, item.errorData.positiveTextRes)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError onPositiveClick resets to Idle`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = false)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            viewModel.showResultError(result, onRetry = {})

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                err.errorData.onPositiveClick?.invoke()
                val next = awaitItem()
                assertTrue(next is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showResultError onPositiveClick calls onRetry when canRetry true`() =
        runTest {
            val result = buildErrorResult(type = ErrorType.ERROR, canRetry = true)
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Failed"

            var retryCalled = 0
            viewModel.showResultError(result, onRetry = { retryCalled++ })

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error

                // act
                err.errorData.onPositiveClick?.invoke()

                // assert: first Idle (reset), and onRetry executed
                assertTrue(awaitItem() is BaseScreenState.Idle)
                assertEquals(1, retryCalled)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
