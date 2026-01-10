package th.co.cjexpress.store.sage.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.Runs
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.just
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.IPurchaseInvoiceProductsModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.SaveDraftPurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceItemDetailViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class PurchaseInvoiceItemDetailViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    @RelaxedMockK
    lateinit var saveDraftPurchaseInvoiceUseCase: SaveDraftPurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: PurchaseInvoiceItemDetailViewModel

    override fun setUp() {
        super.setUp()

        val mockItem = mockk<PurchaseInvoiceEntryUIModel>(relaxed = true)
        every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value } returns mockItem

        viewModel =
            PurchaseInvoiceItemDetailViewModel(
                purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
                saveDraftPurchaseInvoiceUseCase = saveDraftPurchaseInvoiceUseCase,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun `setInputActualQtyState should update inputActualQtyState`() {
        val qty = 5
        viewModel.setInputActualQtyState(qty)
        val state = viewModel.inputActualQtyState.value
        assertEquals(qty, state)
    }

    @Test
    fun `validateBarcode should return true when item is found`() {
        val barcode = "ABC123"
        every { purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder(barcode) } returns mockk()

        val result = viewModel.validateBarcode(barcode)

        assertTrue(result)
    }

    @Test
    fun `validateBarcode should return false when item is null`() {
        val barcode = "ABC999"
        every { purchaseInvoiceDetailLiveDataUseCase.getPurchaseInvoiceOrder(barcode) } returns null

        val result = viewModel.validateBarcode(barcode)

        assertFalse(result)
    }

    @Test
    fun `setCanEditState should update canEditState and reset base screen state`() {
        val canEdit = true

        viewModel.setCanEditState(canEdit)

        assertEquals(canEdit, viewModel.canEditState.value)
    }

    @Test
    fun `updatePurchaseInvoiceItem should call usecase method`() {
        val actualQty = 10
        every { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItemActualQty(actualQty) } just Runs

        viewModel.updatePurchaseInvoiceItem(actualQty)

        verify { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItemActualQty(actualQty) }
    }

    @Test
    fun `resetPurchaseInvoiceItem should call usecase method`() {
        every { purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceItem() } just Runs

        viewModel.resetPurchaseInvoiceItem()

        verify { purchaseInvoiceDetailLiveDataUseCase.resetPurchaseInvoiceItem() }
    }

    @Test
    fun test_setConfirmBackDialogState_expect_false() =
        runTest {
            // when
            viewModel.setConfirmBackDialogState(false)

            // then
            viewModel.confirmBackDialogState.test {
                assertTrue(!awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_purchaseInvoiceDetailLiveData_expect_return_live_data() =
        runTest {
            // when
            viewModel.purchaseInvoiceItemState

            // then
            verify { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem }
        }

    @Test
    fun `setShowNotMatchDialogState should update canEditState and reset base screen state`() {
        val showNotMatchDialogState = true

        viewModel.setShowNotMatchDialogState(showNotMatchDialogState)

        assertEquals(showNotMatchDialogState, viewModel.showNotMatchDialogState.value)
    }

    @Test
    fun test_saveDraftPurchaseInvoice_success_expect_updateCalled() =
        runTest {
            // given
            val mockProduct: IPurchaseInvoiceProductsModel = mockk<IPurchaseInvoiceProductsModel>()
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
            coEvery { saveDraftPurchaseInvoiceUseCase.execute(mockProduct) } returns
                flowOf(
                    Result
                        .Success(mockProductModel),
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
    fun `handleCheckIsRefPO should call saveDraftPurchaseInvoice when qty is not zero`() =
        runTest {
            // Given
            val model = mockk<PurchaseInvoiceEntryUIModel>(relaxed = true)
            every { model.actualQty } returns 0

            val draftItem = mockk<PurchaseInvoiceEntryUIModel>()
            every { draftItem.qty } returns 5
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value } returns draftItem

            coEvery { saveDraftPurchaseInvoiceUseCase.execute(any()) } returns
                flowOf(
                    Result.Success(
                        mockk(),
                    ),
                )
            every { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceProduct(any()) } just Runs

            // When
            viewModel.handleCheckIsRefPO(model)

            // Then
            verify { saveDraftPurchaseInvoiceUseCase.execute(model) }
        }

    @Test
    fun `handleCheckIsRefPO should show zero qty dialog when qty and actualQty are both zero`() =
        runTest {
            // Given
            val model = mockk<PurchaseInvoiceEntryUIModel>()
            every { model.actualQty } returns 0

            val draftItem = mockk<PurchaseInvoiceEntryUIModel>()
            every { draftItem.qty } returns 0
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value } returns draftItem

            // When
            viewModel.handleCheckIsRefPO(model)

            // Then
            viewModel.showQtyEqualZeroDialogState.test {
                assertTrue(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleCheckIsRefPO should update item when qty is zero but actualQty is not zero`() =
        runTest {
            // Given
            val model = mockk<PurchaseInvoiceEntryUIModel>()
            every { model.actualQty } returns 10

            val draftItem = mockk<PurchaseInvoiceEntryUIModel>()
            every { draftItem.qty } returns 0
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value } returns draftItem

            every { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItem(any()) } just Runs

            // When
            viewModel.handleCheckIsRefPO(model)

            // Then
            verify { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItem(model) }
        }

    @Test
    fun `handleCheckIsRefPO should do nothing when purchaseInvoiceItem is null`() =
        runTest {
            // Given
            val model = mockk<PurchaseInvoiceEntryUIModel>()
            every { model.actualQty } returns 0
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceItem.value } returns null

            // When
            viewModel.handleCheckIsRefPO(model)

            // Then
            viewModel.showQtyEqualZeroDialogState.test {
                skipItems(1)
                expectNoEvents()
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 0) { saveDraftPurchaseInvoiceUseCase.execute(any()) }
            verify(exactly = 0) { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceItem(any()) }
        }

    @Test
    fun `setShowQtyEqualZeroDialogState should update state to false`() =
        runTest {
            // When
            viewModel.setShowQtyEqualZeroDialogState(false)

            // Then
            viewModel.showQtyEqualZeroDialogState.test {
                assertFalse(awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `showDialogBarcodeInvalid when barcode is blank`() =
        runTest {
            // When
            viewModel.showDialogBarcodeInvalid()

            // Then
            viewModel.baseScreenState.test {
                val state = awaitItem()
                assert(state is BaseScreenState.Error)
                val dialog = (state as BaseScreenState.Error).errorData
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    dialog.titleText,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_scan_or_enter_barcode),
                    dialog.messageText,
                )
                dialog.onPositiveClick?.invoke()
                val resetState = awaitItem()
                assertEquals(BaseScreenState.Idle, resetState)
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
    fun `logEventAdjustItemQuantity should logInfo with correct action and message`() =
        runTest {
            // when
            viewModel.logEventAdjustItemQuantity(1)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ADJUST_ITEM_QUANTITY,
                    message = "1",
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
            val mockState = mockk<PurchaseInvoiceEntryUIModel>()

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
