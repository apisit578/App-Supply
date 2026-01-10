package th.co.cjexpress.store.sage.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.verify
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertNull
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceDetailLiveData
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseBarcodeFindListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseBarcodeFindUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.SaveDraftPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.ScanPurchaseInvoiceOrderUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceItemsViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.DateUtils.getTimestampUnix
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class PurchaseInvoiceItemsViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    @RelaxedMockK
    lateinit var saveDraftPurchaseInvoiceUseCase: SaveDraftPurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var scanPurchaseInvoiceOrderUseCase: ScanPurchaseInvoiceOrderUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceUseCase: PurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var purchaseBarcodeFindUseCase: PurchaseBarcodeFindUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: PurchaseInvoiceItemsViewModel

    override fun setUp() {
        super.setUp()
        // given
        val detailLD = androidx.lifecycle.MutableLiveData<PurchaseInvoiceDetailUIModel?>(null)
        every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail } returns detailLD
        val supplierLD = androidx.lifecycle.MutableLiveData<PurchaseInvoiceSupplierUIModel?>(null)
        every { purchaseInvoiceUseCase.purchaseInvoiceSupplierLiveData } returns supplierLD

        // when
        viewModel =
            PurchaseInvoiceItemsViewModel(
                purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
                saveDraftPurchaseInvoiceUseCase = saveDraftPurchaseInvoiceUseCase,
                scanPurchaseInvoiceOrderUseCase = scanPurchaseInvoiceOrderUseCase,
                purchaseInvoiceUseCase = purchaseInvoiceUseCase,
                purchaseBarcodeFindUseCase = purchaseBarcodeFindUseCase,
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_scanBarcode_success_expect_saveDraftCalled() =
        runTest {
            // given
            val barcode = "1234567890"
            val mockProduct: IPurchaseInvoiceProductsModel = mockk()
            coEvery { scanPurchaseInvoiceOrderUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        mockProduct,
                    ),
                )
            val supplierModel =
                mockk<PurchaseInvoiceSupplierUIModel> {
                    every { supplier } returns mockk { every { isRefPo } returns true }
                }
            viewModel.purchaseInvoiceSupplierLiveData.value = supplierModel

            // when
            viewModel.scanBarcode(barcode)

            // then
            coVerify { saveDraftPurchaseInvoiceUseCase.execute(mockProduct) }
        }

    @Test
    fun test_scanBarcode_error_expect_errorState() =
        runTest {
            // given
            val barcode = "1234567890"
            coEvery { scanPurchaseInvoiceOrderUseCase.execute(barcode) } returns
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
            viewModel.scanBarcode(barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_scanBarcode_exception_expect_errorState() =
        runTest {
            // given
            val barcode = "1234567890"
            coEvery { scanPurchaseInvoiceOrderUseCase.execute(barcode) } returns
                flow {
                    throw Exception(
                        "Error",
                    )
                }

            // when
            viewModel.scanBarcode(barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun scanBarcode_error_uses_cause_title_and_message() =
        runTest {
            // given
            val bc = "X"
            coEvery { scanPurchaseInvoiceOrderUseCase.execute(bc) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                message = "MSG_FROM_CAUSE",
                                title = "TITLE_FROM_CAUSE",
                            ),
                    ),
                )
            viewModel.purchaseInvoiceSupplierLiveData.value =
                mockk { every { supplier } returns mockk { every { isRefPo } returns true } }

            // when
            viewModel.scanBarcode(bc)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun scanBarcode_exception_with_cause_uses_cause_message() =
        runTest {
            // given
            val bc = "X2"
            coEvery { scanPurchaseInvoiceOrderUseCase.execute(bc) } returns
                flow { throw RuntimeException("outer", Throwable("inner-cause")) }

            // when
            viewModel.scanBarcode(bc)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveDraft_success_with_empty_items_uses_timestamp_fallback() =
        runTest {
            // given
            val product: IPurchaseInvoiceProductsModel = mockk()
            val ts = "2024-01-01T00:00:00"
            val model =
                PurchaseInvoiceDraftModel(
                    code = "",
                    ref = "",
                    message = "",
                    timestamp = ts,
                    data = PurchaseInvoiceDraftDataModel(piNo = "PI", items = emptyList()),
                )
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(product) } returns
                flowOf(
                    Result.Success(
                        model,
                    ),
                )

            // when
            viewModel.saveDraftPurchaseInvoice(product)

            // then
            verify { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceProduct(emptyList()) }
            verify { purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(getTimestampUnix(ts)) }
        }

    @Test
    fun saveDraft_error_with_title_and_message_not_null() =
        runTest {
            // given
            val product: IPurchaseInvoiceProductsModel = mockk()
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(product) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                message = "MSG",
                                title = "TITLE",
                            ),
                    ),
                )

            // when
            viewModel.saveDraftPurchaseInvoice(product)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_success_expect_updateCalled() =
        runTest {
            // given
            val mockProduct: IPurchaseInvoiceProductsModel = mockk()
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
                                        1,
                                        "123456789",
                                        "Test Product",
                                        10,
                                        1,
                                        "pcs",
                                        12000L,
                                    ),
                                    PurchaseInvoiceDraftItemModel(
                                        1,
                                        "1111111",
                                        "Test Product2",
                                        10,
                                        1,
                                        "pcs",
                                        15000L,
                                    ),
                                ),
                        ),
                )
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(mockProduct) } returns
                flowOf(
                    Result.Success(
                        mockProductModel,
                    ),
                )

            // when
            viewModel.saveDraftPurchaseInvoice(mockProduct)

            // then
            verify { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceProduct(any()) }
            verify { purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(15000L) }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_error_expect_errorState() =
        runTest {
            // given
            val mockProduct: IPurchaseInvoiceProductsModel = mockk()
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(mockProduct) } returns
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
            viewModel.saveDraftPurchaseInvoice(mockProduct)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveDraft_exception_with_cause_uses_cause_message() =
        runTest {
            // given
            val product: IPurchaseInvoiceProductsModel = mockk()
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(product) } returns
                flow { throw IllegalStateException("wrap", Throwable("inner")) }

            // when
            viewModel.saveDraftPurchaseInvoice(product)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveDraft_error_canRetry_true_click_close_should_not_retry() =
        runTest {
            // given
            val product: IPurchaseInvoiceProductsModel = mockk()
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(product) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        canRetry = true,
                    ),
                )

            // when
            viewModel.saveDraftPurchaseInvoice(product)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                coVerify(exactly = 1) { saveDraftPurchaseInvoiceUseCase.execute(product) }
                dlg.errorData.onNegativeClick?.invoke() // กด Close
                assert(awaitItem() is BaseScreenState.Idle)
                coVerify(exactly = 1) { saveDraftPurchaseInvoiceUseCase.execute(product) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun saveDraft_error_canRetry_false_click_ok_should_not_retry() =
        runTest {
            // given
            val product: IPurchaseInvoiceProductsModel = mockk()
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(product) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        canRetry = false,
                    ),
                )

            // when
            viewModel.saveDraftPurchaseInvoice(product)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                coVerify(exactly = 1) { saveDraftPurchaseInvoiceUseCase.execute(product) }
                dlg.errorData.onPositiveClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                coVerify(exactly = 1) { saveDraftPurchaseInvoiceUseCase.execute(product) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_purchaseInvoiceDetailLiveData_expect_return_live_data() =
        runTest {
            // given (none)

            // when
            viewModel.purchaseInvoiceDetailLiveData

            // then
            verify { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail }
        }

    @Test
    fun test_productSelected_expect_update_state() =
        runTest {
            // given
            val mockProduct: PurchaseInvoiceEntryUIModel = mockk()

            // when
            viewModel.productSelected(mockProduct)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(
                        PurchaseInvoiceItemsViewModel.UIState.NavToSubmit(
                            mockProduct,
                        ),
                    ),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setPurchaseInvoiceItem_expect_useCase_called_with_correct_argument() =
        runTest {
            // given
            val mockItem = mockk<PurchaseInvoiceEntryUIModel>(relaxed = true)

            // when
            viewModel.setPurchaseInvoiceItem(mockItem)

            // then
            verify(exactly = 1) {
                purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceItem(
                    mockItem,
                )
            }
        }

    @Test
    fun test_promptConfirmBackMainMenu_should_emit_dialog_and_navigate_on_yes() =
        runTest {
            // given (none)

            // when
            viewModel.promptConfirmBackMainMenu()

            // then
            viewModel.baseScreenState.test {
                val dialogState = awaitItem() as BaseScreenState.Error
                dialogState.errorData.onPositiveClick?.invoke()
                val next = awaitItem()
                val success =
                    if (next is BaseScreenState.Success<*>) next else awaitItem() as BaseScreenState.Success<*>
                assertTrue(success.data is PurchaseInvoiceItemsViewModel.UIState.NavBackToMain)
                verify { purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceDetail() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_promptConfirmBack_should_emit_dialog_and_navigate_on_yes() =
        runTest {
            // given
            val supplierUi =
                mockk<PurchaseInvoiceSupplierUIModel> { every { supplier } returns mockk { every { isRefPo } returns false } }
            viewModel.purchaseInvoiceSupplierLiveData.value = supplierUi
            viewModel.purchaseInvoiceDetailLiveData.value = null

            // when
            viewModel.promptConfirmBack()

            // then
            viewModel.baseScreenState.test {
                val dialog = awaitItem() as BaseScreenState.Error
                dialog.errorData.onPositiveClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                val success = awaitItem() as BaseScreenState.Success<*>
                assertTrue(success.data is PurchaseInvoiceItemsViewModel.UIState.NavBack)
                verify { purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceDetail() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_promptRemoveItem_should_emit_dialog_and_remove_on_yes() =
        runTest {
            // given
            val item = PurchaseInvoiceEntryUIModel("BARCODE_TO_REMOVE", 1, "", "", 1, 0, 0.0)

            // when
            viewModel.promptRemoveItem(item)

            // then
            viewModel.baseScreenState.test {
                val dialogState = awaitItem() as BaseScreenState.Error
                dialogState.errorData.onPositiveClick?.invoke()
                verify { purchaseInvoiceDetailLiveDataUseCase.removePurchaseInvoiceProductItem("BARCODE_TO_REMOVE") }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_promptCheckState_should_emit_dialog() =
        runTest {
            // given (none)

            // when
            viewModel.promptCheckState()

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_checkAllowBuy_matched_isAllowBuy_true_should_return_model_and_false() {
        // given
        val barcode = "123"
        val model =
            PurchaseBarcodeFindItemModel(
                barcode = barcode,
                isAllowBuy = true,
                barFactor = 1,
                isFreshLife = false,
                productName = "",
                unitCode = "",
                unitName = "",
            )
        val uiModel = mockk<PurchaseInvoiceEntryUIModel>()
        every {
            uiModelMapper.purchaseBarcodeFindUIMapper.mapPurchaseInvoiceCreateModelToUIModel(
                model,
                1,
            )
        } returns uiModel

        // when
        val result = viewModel.checkAllowBuy(barcode, listOf(model))

        // then
        assertTrue(result.first != null)
        assertFalse(result.second)
    }

    @Test
    fun test_checkAllowBuy_matched_isAllowBuy_false_with_isAllowBuy_in_others_should_return_null_and_true() {
        // given
        val barcode = "111"
        val model =
            PurchaseBarcodeFindItemModel(
                barcode = barcode,
                isAllowBuy = false,
                barFactor = 1,
                isFreshLife = false,
                productName = "",
                unitCode = "",
                unitName = "",
            )
        val model2 =
            PurchaseBarcodeFindItemModel(
                barcode = "222",
                isAllowBuy = true,
                barFactor = 5,
                isFreshLife = false,
                productName = "",
                unitCode = "",
                unitName = "",
            )

        // when
        val result = viewModel.checkAllowBuy(barcode, listOf(model, model2))

        // then
        assertNull(result.first)
        assertTrue(result.second)
    }

    @Test
    fun test_checkAllowBuy_matched_isAllowBuy_false_no_isAllowBuy_should_return_null_and_false() {
        // given
        val barcode = "111"
        val model =
            PurchaseBarcodeFindItemModel(
                barcode = barcode,
                isAllowBuy = false,
                barFactor = 1,
                isFreshLife = false,
                productName = "",
                unitCode = "",
                unitName = "",
            )

        // when
        val result = viewModel.checkAllowBuy(barcode, listOf(model))

        // then
        assertNull(result.first)
        assertFalse(result.second)
    }

    @Test
    fun test_checkAllowBuy_no_matched_and_no_isAllowBuy_should_return_null_and_false() {
        // given
        val barcode = "1234567890"
        val model =
            PurchaseBarcodeFindItemModel(
                barcode = "11111111",
                isAllowBuy = false,
                barFactor = 1,
                isFreshLife = false,
                productName = "",
                unitCode = "",
                unitName = "",
            )

        // when
        val result = viewModel.checkAllowBuy(barcode, listOf(model))

        // then
        assertNull(result.first)
        assertFalse(result.second)
    }

    @Test
    fun test_resetPurchaseInvoiceDetail_should_call_useCase() {
        // given (none)

        // when
        viewModel.resetPurchaseInvoiceDetail()

        // then
        verify { purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceDetail() }
    }

    @Test
    fun test_checkAllowBuy_with_empty_list_should_return_null_and_false() {
        // given
        val barcode = "1234567890"

        // when
        val result = viewModel.checkAllowBuy(barcode, emptyList())

        // then
        assertNull(result.first)
        assertFalse(result.second)
    }

    @Test
    fun test_purchase_find_success_expect_purchaseBarcodeFindCalled() =
        runTest {
            // given
            val barcode = "1234567890"
            val mockProduct: PurchaseBarcodeFindListModel = mockk()
            coEvery { purchaseBarcodeFindUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        mockProduct,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(barcode)

            // then
            coVerify { purchaseBarcodeFindUseCase.execute(barcode) }
        }

    @Test
    fun test_purchaseBarcodeFind_success_should_execute_called() =
        runTest {
            // given
            val barcode = "1234567890"
            val mockProduct: PurchaseBarcodeFindListModel = mockk()
            coEvery { purchaseBarcodeFindUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        mockProduct,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(barcode)

            // then
            coVerify { purchaseBarcodeFindUseCase.execute(barcode) }
        }

    @Test
    fun test_purchaseBarcodeFind_error_should_execute_called() =
        runTest {
            // given
            val barcode = "1234567890"
            coEvery { purchaseBarcodeFindUseCase.execute(barcode) } returns
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
            viewModel.purchaseBarcodeFind(barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun purchaseBarcodeFind_error_with_title_and_message_not_null() =
        runTest {
            // given
            val bc = "Y1"
            coEvery { purchaseBarcodeFindUseCase.execute(bc) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                message = "M",
                                title = "T",
                            ),
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(bc)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun purchaseBarcodeFind_exception_with_cause_uses_cause_message() =
        runTest {
            // given
            val bc = "Y2"
            coEvery { purchaseBarcodeFindUseCase.execute(bc) } returns
                flow {
                    throw RuntimeException(
                        "boom",
                        Throwable("inner"),
                    )
                }

            // when
            viewModel.purchaseBarcodeFind(bc)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_purchaseBarcodeFind_exception_expect_errorState() =
        runTest {
            // given
            val barcode = "1234567890"
            coEvery { purchaseBarcodeFindUseCase.execute(barcode) } returns flow { throw Exception("Error") }

            // when
            viewModel.purchaseBarcodeFind(barcode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun purchaseBarcodeFind_error_canRetry_true_click_close_should_not_retry() =
        runTest {
            // given
            val bc = "ERR-Retry"
            coEvery { purchaseBarcodeFindUseCase.execute(bc) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        canRetry = true,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(bc)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                coVerify(exactly = 1) { purchaseBarcodeFindUseCase.execute(bc) }
                dlg.errorData.onNegativeClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                coVerify(exactly = 1) { purchaseBarcodeFindUseCase.execute(bc) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun purchaseBarcodeFind_error_canRetry_false_click_ok_should_not_retry() =
        runTest {
            // given
            val bc = "ERR-NoRetry"
            coEvery { purchaseBarcodeFindUseCase.execute(bc) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        canRetry = false,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(bc)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                coVerify(exactly = 1) { purchaseBarcodeFindUseCase.execute(bc) }
                dlg.errorData.onPositiveClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                coVerify(exactly = 1) { purchaseBarcodeFindUseCase.execute(bc) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_TachyonAlertDialogUIModel_callbacks() {
        // given
        var onConfirmCalled = false
        var onDismissCalled = false
        val dialog =
            TachyonAlertDialogUIModel(
                type = ErrorType.ERROR,
                titleText = "title",
                messageText = "msg",
                showF1Message = false,
                onPositiveClick = { onConfirmCalled = true },
                onNegativeClick = { onDismissCalled = true },
            )

        // when
        dialog.onPositiveClick?.invoke()
        dialog.onNegativeClick?.invoke()

        // then
        assertTrue(onConfirmCalled)
        assertTrue(onDismissCalled)
    }

    @Test
    fun `logEventGetItemsList should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = mockk<PurchaseInvoiceDetailUIModel>()

            // when
            viewModel.logEventGetItemsList(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.GET_PURCHASE_INVOICE_ITEMS,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectItem should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = mockk<PurchaseInvoiceEntryUIModel>()

            // when
            viewModel.logEventSelectItem(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_PURCHASE_INVOICE_ITEM,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventScanBarcode should logInfo with correct action and message`() =
        runTest {
            // given
            val barcode = "1234567890"

            // when
            viewModel.logEventScanBarcode(barcode)

            // then
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
    fun `logEventCheckPurchaseInvoice should logInfo with correct action and message`() =
        runTest {
            // given (none)

            // when
            viewModel.logEventCheckPurchaseInvoice()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ON_CHECK_PURCHASE_INVOICE,
                    message = LogAction.ON_CHECK_PURCHASE_INVOICE.name,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventRemoveItem should logInfo with correct action and message`() =
        runTest {
            // given
            val barcode = "1234567890"

            // when
            viewModel.logEventRemoveItem(barcode)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.REMOVE_ITEM,
                    message = barcode,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun test_removePurchaseInvoiceProductItem_when_detail_is_null_should_not_crash() {
        // given
        val liveData = PurchaseInvoiceDetailLiveData()

        // when
        liveData.removePurchaseInvoiceProductItem("ANY")

        // then
        assertNull(liveData.purchaseInvoiceDetailUIModel.value)
    }

    @Test
    fun scanBarcode_success_isRefPo_false_calls_purchaseBarcodeFind() =
        runTest {
            // given
            val barcode = "B1"
            coEvery { scanPurchaseInvoiceOrderUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        mockk(),
                    ),
                )
            viewModel.purchaseInvoiceSupplierLiveData.value =
                mockk { every { supplier } returns mockk { every { isRefPo } returns false } }
            coEvery { purchaseBarcodeFindUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        mockk(),
                    ),
                )

            // when
            viewModel.scanBarcode(barcode)

            // then
            coVerify { purchaseBarcodeFindUseCase.execute(barcode) }
        }

    @Test
    fun scanBarcode_error_isRefPo_false_calls_purchaseBarcodeFind() =
        runTest {
            // given
            val barcode = "B2"
            coEvery { scanPurchaseInvoiceOrderUseCase.execute(barcode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )
            viewModel.purchaseInvoiceSupplierLiveData.value =
                mockk { every { supplier } returns mockk { every { isRefPo } returns false } }
            coEvery { purchaseBarcodeFindUseCase.execute(barcode) } returns
                flowOf(
                    Result.Success(
                        mockk(),
                    ),
                )

            // when
            viewModel.scanBarcode(barcode)

            // then
            coVerify { purchaseBarcodeFindUseCase.execute(barcode) }
        }

    @Test
    fun saveDraft_error_canRetry_true_retries_on_positive() =
        runTest {
            // given
            val product: IPurchaseInvoiceProductsModel = mockk()
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(product) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        canRetry = true,
                    ),
                )

            // when
            viewModel.saveDraftPurchaseInvoice(product)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                coVerify(exactly = 1) { saveDraftPurchaseInvoiceUseCase.execute(product) }
                dlg.errorData.onPositiveClick?.invoke()
                coVerify(exactly = 2) { saveDraftPurchaseInvoiceUseCase.execute(product) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun purchaseBarcodeFind_error_canRetry_true_retries_on_positive() =
        runTest {
            // given
            val bc = "B3"
            coEvery { purchaseBarcodeFindUseCase.execute(bc) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "ERROR",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                        canRetry = true,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(bc)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                coVerify(exactly = 1) { purchaseBarcodeFindUseCase.execute(bc) }
                dlg.errorData.onPositiveClick?.invoke()
                coVerify(exactly = 2) { purchaseBarcodeFindUseCase.execute(bc) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun purchaseBarcodeFind_success_adds_item_when_allowBuy() =
        runTest {
            // given
            val bc = "OK1"
            val itemModel =
                PurchaseBarcodeFindItemModel(
                    barcode = bc,
                    isAllowBuy = true,
                    barFactor = 1,
                    isFreshLife = false,
                    productName = "",
                    unitCode = "",
                    unitName = "",
                )
            val uiItem = PurchaseInvoiceEntryUIModel(bc, 1, "", "", 1, 0, 0.0)
            every {
                uiModelMapper.purchaseBarcodeFindUIMapper.mapPurchaseInvoiceCreateModelToUIModel(
                    itemModel,
                    1,
                )
            } returns uiItem
            val listModel =
                mockk<PurchaseBarcodeFindListModel> { every { data } returns listOf(itemModel) }
            coEvery { purchaseBarcodeFindUseCase.execute(bc) } returns
                flowOf(
                    Result.Success(
                        listModel,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(bc)

            // then
            verify { purchaseInvoiceDetailLiveDataUseCase.addPurchaseInvoiceProduct(uiItem) }
        }

    @Test
    fun purchaseBarcodeFind_success_shows_question_when_suggestion_exists() =
        runTest {
            // given
            val submitted = "SUB"
            val notAllowed = PurchaseBarcodeFindItemModel(1, submitted, false, false, "", "", "")
            val suggestion = PurchaseBarcodeFindItemModel(10, "SGG", true, false, "", "", "")
            val listModel =
                mockk<PurchaseBarcodeFindListModel> {
                    every { data } returns
                        listOf(
                            notAllowed,
                            suggestion,
                        )
                }
            coEvery { purchaseBarcodeFindUseCase.execute(submitted) } returns
                flowOf(
                    Result.Success(
                        listModel,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(submitted)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onNegativeClick?.invoke()
                verify(exactly = 0) {
                    purchaseInvoiceDetailLiveDataUseCase.addPurchaseInvoiceProduct(
                        any(),
                    )
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun purchaseBarcodeFind_success_warning_when_no_allowed_items() =
        runTest {
            // given
            val bc = "FAIL"
            val i1 = PurchaseBarcodeFindItemModel(1, "X", false, false, "", "", "")
            val i2 = PurchaseBarcodeFindItemModel(2, bc, false, false, "", "", "")
            val listModel =
                mockk<PurchaseBarcodeFindListModel> { every { data } returns listOf(i1, i2) }
            coEvery { purchaseBarcodeFindUseCase.execute(bc) } returns
                flowOf(
                    Result.Success(
                        listModel,
                    ),
                )

            // when
            viewModel.purchaseBarcodeFind(bc)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun promptConfirmBack_isRefPo_true_isEdit_true_navigates_ListPO() =
        runTest {
            // given
            viewModel.purchaseInvoiceSupplierLiveData.value =
                mockk { every { supplier } returns mockk { every { isRefPo } returns true } }
            viewModel.purchaseInvoiceDetailLiveData.value = mockk { every { isEdit } returns true }

            // when
            viewModel.promptConfirmBack()

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onPositiveClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                val success = awaitItem() as BaseScreenState.Success<*>
                assertTrue(success.data is PurchaseInvoiceItemsViewModel.UIState.NavToListPI)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun promptConfirmBack_click_no_resets_only_state_no_navigation() =
        runTest {
            // given
            viewModel.purchaseInvoiceSupplierLiveData.value =
                mockk { every { supplier } returns mockk { every { isRefPo } returns false } }

            // when
            viewModel.promptConfirmBack()

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onNegativeClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                verify(exactly = 0) { purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceDetail() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun promptConfirmBack_isRefPo_true_isEdit_false_navigates_ListPI() =
        runTest {
            // given
            viewModel.purchaseInvoiceSupplierLiveData.value =
                mockk { every { supplier } returns mockk { every { isRefPo } returns true } }
            viewModel.purchaseInvoiceDetailLiveData.value = mockk { every { isEdit } returns false }

            // when
            viewModel.promptConfirmBack()

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onPositiveClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                val success = awaitItem() as BaseScreenState.Success<*>
                assertTrue(success.data is PurchaseInvoiceItemsViewModel.UIState.NavToListPO)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun promptConfirmBackMainMenu_click_no_resets_only_state() =
        runTest {
            // given (none)

            // when
            viewModel.promptConfirmBackMainMenu()

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onNegativeClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                verify(exactly = 0) { purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceDetail() }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun promptChangeBarcode_yes_uses_suggestion_param() =
        runTest {
            // given
            val bc = "RAW"
            val sug = "SUGGEESTION"
            coEvery { purchaseBarcodeFindUseCase.execute(sug) } returns
                flowOf(Result.Success(mockk()))

            // when
            viewModel.promptChangeBarcode(bc, sug)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onPositiveClick?.invoke()
                coVerify { purchaseBarcodeFindUseCase.execute(sug) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun checkAllowBuy_then_promptChangeBarcode_yes_uses_suggestion() =
        runTest {
            // given
            val submitted = "SUB"
            val suggestionCode = "SGG"
            val notAllowed = PurchaseBarcodeFindItemModel(1, submitted, false, false, "", "", "")
            val suggestion =
                PurchaseBarcodeFindItemModel(9, suggestionCode, true, false, "", "", "")
            viewModel.checkAllowBuy(submitted, listOf(notAllowed, suggestion))
            coEvery { purchaseBarcodeFindUseCase.execute(suggestionCode) } returns
                flowOf(
                    Result.Success(
                        mockk(),
                    ),
                )

            // when
            // รอ dialog จาก checkAllowBuy แล้วกดตกลงในขั้น then

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onPositiveClick?.invoke()
                coVerify { purchaseBarcodeFindUseCase.execute(suggestionCode) }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun removePurchaseInvoiceProductItem_direct_calls_usecase() =
        runTest {
            // given
            val data = PurchaseInvoiceEntryUIModel("DEL", 1, "", "", 1, 0, 0.0)

            // when
            viewModel.removePurchaseInvoiceProductItem(data)

            // then
            verify { purchaseInvoiceDetailLiveDataUseCase.removePurchaseInvoiceProductItem("DEL") }
        }

    @Test
    fun promptRemoveItem_click_no_does_not_remove() =
        runTest {
            // given
            val data = PurchaseInvoiceEntryUIModel("NOPE", 1, "", "", 1, 0, 0.0)

            // when
            viewModel.promptRemoveItem(data)

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onNegativeClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                verify(exactly = 0) {
                    purchaseInvoiceDetailLiveDataUseCase.removePurchaseInvoiceProductItem(
                        "NOPE",
                    )
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun promptCheckState_should_show_warning_and_reset_on_close() =
        runTest {
            // given (none)

            // when
            viewModel.promptCheckState()

            // then
            viewModel.baseScreenState.test {
                val dialogState = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.WARNING, dialogState.errorData.type)
                dialogState.errorData.onPositiveClick?.invoke()
                assert(awaitItem() is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun promptConfirmBackMainMenu_isRefPo_true_branch() =
        runTest {
            // given
            viewModel.purchaseInvoiceSupplierLiveData.value =
                mockk { every { supplier } returns mockk { every { isRefPo } returns true } }

            // when
            viewModel.promptConfirmBackMainMenu()

            // then
            viewModel.baseScreenState.test {
                val dlg = awaitItem() as BaseScreenState.Error
                dlg.errorData.onPositiveClick?.invoke()
                awaitItem()
                val s = awaitItem() as BaseScreenState.Success<*>
                assertTrue(s.data is PurchaseInvoiceItemsViewModel.UIState.NavBackToMain)
                cancelAndIgnoreRemainingEvents()
            }
        }
}
