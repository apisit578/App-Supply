package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
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
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.FilterSupplierUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetFrequencySupplierUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetSupplierByCodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierListUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.purchaseInvoice.PurchaseInvoiceSupplierListState
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceSupplierListViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class PurchaseInvoiceSupplierListViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var purchaseInvoiceUseCase: PurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var getFrequencySupplierUseCase: GetFrequencySupplierUseCase

    @RelaxedMockK
    lateinit var filterSupplierUseCase: FilterSupplierUseCase

    @RelaxedMockK
    lateinit var getSupplierByCodeUseCase: GetSupplierByCodeUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var viewModel: PurchaseInvoiceSupplierListViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            PurchaseInvoiceSupplierListViewModel(
                purchaseInvoiceUseCase = purchaseInvoiceUseCase,
                getFrequencySupplierUseCase = getFrequencySupplierUseCase,
                filterSupplierUseCase = filterSupplierUseCase,
                getSupplierByCodeUseCase = getSupplierByCodeUseCase,
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
                dispatcherProvider = TestDispatchers(),
            )
    }

    @Test
    fun test_getFrequencySupplier_success_expect_update_ui_state_success() =
        runTest {
            // Given
            val mockPurchaseInvoiceSupplierListModel =
                mockk<PurchaseInvoiceSupplierListModel>(relaxed = true)
            val mockPurchaseInvoiceSupplierListUIModel =
                mockk<PurchaseInvoiceSupplierListUIModel>(relaxed = true)

            coEvery { getFrequencySupplierUseCase.execute() } returns
                flowOf(Result.Success(mockPurchaseInvoiceSupplierListModel))

            every {
                uiModelMapper.purchaseInvoiceSupplierListUIMapper.toUIModel(
                    mockPurchaseInvoiceSupplierListModel,
                )
            } returns mockPurchaseInvoiceSupplierListUIModel

            // When
            viewModel.getPurchaseInvoiceSupplierList()

            // Then
            viewModel.purchaseInvoiceSupplierListState.test {
                assertEquals(
                    PurchaseInvoiceSupplierListState(mockPurchaseInvoiceSupplierListUIModel),
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
    fun test_getFrequencySupplier_error_expect_update_ui_state_error() =
        runTest {
            // Given
            coEvery { getFrequencySupplierUseCase.execute() } returns
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
            viewModel.getPurchaseInvoiceSupplierList()

            // Then
            viewModel.purchaseInvoiceSupplierListState.test {
                assertEquals(PurchaseInvoiceSupplierListState(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getFrequencySupplier_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            coEvery { getFrequencySupplierUseCase.execute() } returns flow { throw Exception() }

            // When
            viewModel.getPurchaseInvoiceSupplierList()

            // Then
            viewModel.purchaseInvoiceSupplierListState.test {
                assertEquals(PurchaseInvoiceSupplierListState(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getFilterSupplierList_success_expect_update_ui_state_success() =
        runTest {
            // given
            val searchKey = "CJ"
            val mockModel = mockk<PurchaseInvoiceSupplierListModel>(relaxed = true)
            val mockUIModel = mockk<PurchaseInvoiceSupplierListUIModel>(relaxed = true)

            coEvery { filterSupplierUseCase.execute(searchKey) } returns
                flowOf(Result.Success(mockModel))
            every {
                uiModelMapper.purchaseInvoiceSupplierListUIMapper.toUIModel(mockModel)
            } returns mockUIModel

            // when
            viewModel.getFilterSupplierList(searchKey)

            // then
            viewModel.purchaseInvoiceSupplierListState.test {
                assertEquals(
                    PurchaseInvoiceSupplierListState(mockUIModel),
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
    fun test_getFilterSupplierList_error_expect_update_ui_state_error() =
        runTest {
            // Given
            val searchKey = "CJ"
            coEvery { filterSupplierUseCase.execute(searchKey) } returns
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
            viewModel.getFilterSupplierList(searchKey)

            // Then
            viewModel.purchaseInvoiceSupplierListState.test {
                assertEquals(PurchaseInvoiceSupplierListState(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getFilterSupplierList_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val searchKey = "CJ"
            coEvery { filterSupplierUseCase.execute(searchKey) } returns flow { throw Exception() }

            // When
            viewModel.getFilterSupplierList(searchKey)

            // Then
            viewModel.purchaseInvoiceSupplierListState.test {
                assertEquals(PurchaseInvoiceSupplierListState(), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }

            viewModel.baseScreenState.test {
                assertTrue(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSupplierByCode_success_expect_update_ui_state_success() =
        runTest {
            // given
            val searchCode = "CJ"
            val mockModel = mockk<PurchaseInvoiceSupplierModel>(relaxed = true)
            val mockUIModel = mockk<PurchaseInvoiceSupplierUIModel>(relaxed = true)
            coEvery { getSupplierByCodeUseCase.execute(searchCode) } returns
                flowOf(Result.Success(mockModel))
            every {
                uiModelMapper.purchaseInvoiceSupplierUIMapper.toUIModel(mockModel)
            } returns mockUIModel

            // when
            viewModel.getSupplierByCode(searchCode)

            // then
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(PurchaseInvoiceSupplierListViewModel.UIState.NavToPIList),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSupplierByCode_error_expect_update_ui_state_error() =
        runTest {
            // Given
            val searchCode = "CJ"
            coEvery { getSupplierByCodeUseCase.execute(searchCode) } returns
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
            viewModel.getSupplierByCode(searchCode)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_getSupplierByCode_throw_exception_expect_update_ui_state_error() =
        runTest {
            // given
            val searchKey = "CJ"
            coEvery { filterSupplierUseCase.execute(searchKey) } returns flow { throw Exception() }

            // When
            viewModel.getFilterSupplierList(searchKey)

            // then
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun test_setInputPurchaseInvoiceState_expect_state_updated() =
        runTest {
            val newValue = TextFieldValue(text = "INV123", selection = TextRange(6))
            viewModel.setInputPurchaseInvoiceState(newValue)

            assertEquals(newValue, viewModel.inputPurchaseInvoiceState.value)
        }

    @Test
    fun test_clearInputPurchaseInvoiceState_expect_empty_state() =
        runTest {
            viewModel.setInputPurchaseInvoiceState(
                TextFieldValue(text = "SOMETHING", selection = TextRange(9)),
            )

            viewModel.clearInputPurchaseInvoiceState()

            assertEquals(
                TextFieldValue(text = "", selection = TextRange(0)),
                viewModel.inputPurchaseInvoiceState.value,
            )
        }

    @Test
    fun test_setupPurchaseInvoiceLiveData_expect_delegate_to_liveData() =
        runTest {
            val uiModel = mockk<PurchaseInvoiceSupplierUIModel>()

            viewModel.setupPurchaseInvoiceLiveData(uiModel)

            verify(exactly = 1) { purchaseInvoiceUseCase.setPurchaseInvoiceSupplier(uiModel) }
        }

    @Test
    fun test_resetPurchaseInvoiceLiveData_expect_delegate_to_liveData() =
        runTest {
            viewModel.resetPurchaseInvoiceLiveData()

            verify(exactly = 1) { purchaseInvoiceUseCase.resetPurchaseInvoiceSupplier() }
        }

    @Test
    fun `logEventGetSupplierList should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = mockk<PurchaseInvoiceSupplierListUIModel>()

            // when
            viewModel.logEventGetSupplierList(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.GET_SUPPLIER_LIST,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventFilterSupplier should logInfo with correct action and formatted message`() =
        runTest {
            // given
            val searchKey = "CJ"
            every { resourceUtils.getString(R.string.log_message_filter_supplier) } returns "Search key : %s"

            // when
            viewModel.logEventFilterSupplier(searchKey)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.FILTER_SUPPLIER,
                    message = "Search key : CJ",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectSupplier should logInfo with correct action and formatted message`() =
        runTest {
            // given
            val searchKey = "CJ"
            every { resourceUtils.getString(R.string.log_message_select_supplier) } returns "Select supplier: %s"

            // when
            viewModel.logEventSelectSupplier(searchKey)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_SUPPLIER,
                    message = "Select supplier: CJ",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventClearInput should logInfo with correct action and message`() =
        runTest {
            // when
            viewModel.logEventClearInput()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CLEAR_INPUT_FILTER_SUPPLIER,
                    message = LogAction.CLEAR_INPUT_FILTER_SUPPLIER.name,
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
