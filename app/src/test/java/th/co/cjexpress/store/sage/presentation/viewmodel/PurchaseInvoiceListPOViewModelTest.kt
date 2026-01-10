package th.co.cjexpress.store.sage.presentation.viewmodel

import android.content.Context
import android.view.inputmethod.InputMethodManager
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.CheckPurchaseOrderExistsUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.CreatePurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseOrderDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierPOUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceListPOViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.DateUtils
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers

class PurchaseInvoiceListPOViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var getPurchaseOrderDocumentUseCase: GetPurchaseOrderDocumentUseCase

    @RelaxedMockK
    lateinit var checkPurchaseOrderExistsUseCase: CheckPurchaseOrderExistsUseCase

    @RelaxedMockK
    lateinit var createPurchaseInvoiceUseCase: CreatePurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceUseCase: PurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    // viewModel
    private lateinit var viewModel: PurchaseInvoiceListPOViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            PurchaseInvoiceListPOViewModel(
                getPurchaseOrderDocumentUseCase = getPurchaseOrderDocumentUseCase,
                checkPurchaseOrderExistsUseCase = checkPurchaseOrderExistsUseCase,
                createPurchaseInvoiceUseCase = createPurchaseInvoiceUseCase,
                purchaseInvoiceUseCase = purchaseInvoiceUseCase,
                purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
                dispatcherProvider = TestDispatchers(),
                uiModelMapper = uiModelMapper,
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun `test loadPurchaseOrderDocument when success`() =
        runTest {
            // Arrange
            val supplierCode = "test1234"
            val supplierName = "Supplier A"
            val mockPurchaseOrderDocumentList =
                PurchaseOrderDocumentListModel(
                    code = "SUCCESS",
                    ref = "mock-ref-0001",
                    message = "Mock purchase order list loaded",
                    timestamp = "2025-05-29T10:53:16.824412+07:00",
                    data =
                        listOf(
                            PurchaseOrderDocumentItemModel(
                                docNo = "2224082904",
                                shipmentDate = "27/08/2024",
                                grandTotalAmount = 61229.07,
                            ),
                            PurchaseOrderDocumentItemModel(
                                docNo = "4002005766",
                                shipmentDate = "11/11/2024",
                                grandTotalAmount = 0.0,
                            ),
                            PurchaseOrderDocumentItemModel(
                                docNo = "1111011102",
                                shipmentDate = "26/03/2025",
                                grandTotalAmount = 50000.00,
                            ),
                        ),
                )

            val mockPurchaseInvoiceSupplierPOUIModelList =
                listOf(
                    PurchaseInvoiceSupplierPOUIModel(
                        docNo = "1698812230",
                        shipmentDate = "16/11/2023",
                        grandTotalAmountText = "4,758.50",
                    ),
                    PurchaseInvoiceSupplierPOUIModel(
                        docNo = "16988122528",
                        shipmentDate = "16/11/2023",
                        grandTotalAmountText = "4,758.50",
                    ),
                )

            val mockPurchaseOrderDocumentListModelResult =
                flowOf(Result.Success(mockPurchaseOrderDocumentList))

            val mockPurchaseInvoiceSupplierDetailUIModel =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = supplierCode,
                    supplierName = supplierName,
                    isRefPo = true,
                )
            val mockPurchaseInvoiceSupplier =
                PurchaseInvoiceSupplierUIModel(
                    supplier =
                        PurchaseInvoiceSupplierDetailUIModel(
                            supplierCode = supplierCode,
                            supplierName = supplierName,
                            isRefPo = true,
                        ),
                )

            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } answers {
                mockPurchaseInvoiceSupplierDetailUIModel
            }

            coEvery { getPurchaseOrderDocumentUseCase.execute(supplierCode = supplierCode) } answers {
                mockPurchaseOrderDocumentListModelResult
            }

            coEvery { purchaseInvoiceUseCase.purchaseInvoiceSupplierLiveData.value } answers {
                mockPurchaseInvoiceSupplier
            }

            coEvery {
                uiModelMapper.purchaseInvoicePOUIMapper.mapPOUIModel(any())
            } answers {
                mockPurchaseInvoiceSupplierPOUIModelList
            }

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(data = null), awaitItem())
            }

            viewModel.poList.test {
                assertEquals(mockPurchaseInvoiceSupplierPOUIModelList, awaitItem())
            }
        }

    @Test
    fun `test loadPurchaseOrderDocument when success with error`() =
        runTest {
            // Arrange
            val supplierCode = "test1234"
            val supplierName = "Supplier A"
            val mockPurchaseInvoiceSupplierDetailUIModel =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = supplierCode,
                    supplierName = supplierName,
                    isRefPo = true,
                )

            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } answers {
                mockPurchaseInvoiceSupplierDetailUIModel
            }

            coEvery { getPurchaseOrderDocumentUseCase.execute(supplierCode = any()) } answers {
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.ERROR,
                                title = resourceUtils.getString(R.string.text_unable_to_continue),
                                message = resourceUtils.getString(R.string.text_service_failed),
                            ),
                    ),
                )
            }

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    item.errorData.titleText,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_service_failed),
                    item.errorData.messageText,
                )
            }
        }

    @Test
    fun `test loadPurchaseOrderDocument when error`() =
        runTest {
            // Arrange
            val supplierCode = "test1234"
            val supplierName = "Supplier A"
            val mockPurchaseInvoiceSupplierDetailUIModel =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = supplierCode,
                    supplierName = supplierName,
                    isRefPo = true,
                )

            val message = "Error!!!!!"
            val exception = RuntimeException(message)

            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } answers {
                mockPurchaseInvoiceSupplierDetailUIModel
            }
            coEvery { getPurchaseOrderDocumentUseCase.execute(any()) } answers {
                flow { throw exception }
            }

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    item.errorData.titleText,
                )
                assertEquals(
                    message,
                    item.errorData.messageText,
                )
            }
        }

    @Test
    fun `test createPurchaseInvoice when success`() =
        runTest {
            val poNo = "PO001"
            val billNo = "BILL001"
            val supplierCode = "SUP001"

            val createModel =
                mockk<PurchaseInvoiceCreateModel> {
                    every { entries } returns listOf(mockk { every { hhTimestamp } returns 1234567890L })
                }
            val uiModel = mockk<PurchaseInvoiceDetailUIModel>(relaxed = true)
            every { uiModel.copy(any(), any(), any(), any(), any(), any(), any()) } returns uiModel

            coEvery {
                createPurchaseInvoiceUseCase.execute(
                    poNo = poNo,
                    billNo = billNo,
                    supplierCode = supplierCode,
                )
            } returns flowOf(Result.Success(createModel))

            coEvery {
                uiModelMapper.purchaseInvoicePIUIMapper
                    .mapPurchaseInvoiceCreateModelToUIModel(createModel)
            } returns uiModel

            viewModel.baseScreenState.test {
                awaitItem()
                viewModel.createPurchaseInvoice(poNo, billNo, supplierCode)
                assert(awaitItem() is BaseScreenState.Loading)
                val success = awaitItem() as BaseScreenState.Success<*>
                assertEquals(PurchaseInvoiceListPOViewModel.UIState.NavToPIDetail, success.data)

                cancelAndIgnoreRemainingEvents()
            }

            verify {
                purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(uiModel)
                purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(1234567890L)
            }
        }

    @Test
    fun `logEventGetPOList should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = listOf(mockk<PurchaseInvoiceSupplierPOUIModel>())

            // when
            viewModel.logEventGetPOList(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.GET_PO_LIST,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `test createPurchaseInvoice when error`() =
        runTest {
            // Arrange
            val poNo = "PO001"
            val billNo = "BILL001"
            val supplierCode = "SUP001"
            val errorMessage = "Error message"

            coEvery {
                createPurchaseInvoiceUseCase.execute(
                    poNo = poNo,
                    billNo = billNo,
                    supplierCode = supplierCode,
                )
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                message = errorMessage,
                            ),
                    ),
                )

            coEvery {
                resourceUtils.getString(R.string.text_unable_to_continue)
            } returns "Unable to continue"

            // Act
            viewModel.createPurchaseInvoice(poNo, billNo, supplierCode)

            // Assert
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals("Unable to continue", item.errorData.titleText)
            }
        }

    @Test
    fun `logEventSearchPO should logInfo with correct action and formatted message`() =
        runTest {
            // given
            val poNo = "PO123"
            every { resourceUtils.getString(R.string.log_message_po_no) } returns "PO Number: %s"

            // when
            viewModel.logEventSearchPO(poNo)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ON_SEARCH_PO,
                    message = "PO Number: PO123",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `test createPurchaseInvoice when exception`() =
        runTest {
            // Arrange
            val poNo = "PO001"
            val billNo = "BILL001"
            val supplierCode = "SUP001"
            val exceptionMessage = "Network error"
            val exception = RuntimeException(exceptionMessage)

            coEvery {
                createPurchaseInvoiceUseCase.execute(
                    poNo = poNo,
                    billNo = billNo,
                    supplierCode = supplierCode,
                )
            } returns flow { throw exception }

            coEvery {
                resourceUtils.getString(R.string.text_unable_to_continue)
            } returns "Unable to continue"

            // Act
            viewModel.createPurchaseInvoice(poNo, billNo, supplierCode)

            // Assert
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals("Unable to continue", item.errorData.titleText)
                assertEquals(exceptionMessage, item.errorData.messageText)
            }
        }

    @Test
    fun `logEventCreatePO should logInfo with correct action and formatted message`() =
        runTest {
            // given
            val poNo = "PO123"
            val billNo = "BILL456"
            val supplierCode = "SUP789"
            every { resourceUtils.getString(R.string.log_message_create_po) } returns "Bill: %s, PO: %s, Supplier: %s"

            // when
            viewModel.logEventCreatePO(poNo, billNo, supplierCode)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.ON_CREATE_PO,
                    message = "Bill: BILL456, PO: PO123, Supplier: SUP789",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `test clear selected PO`() =
        runTest {
            // when
            viewModel.clearSelectedPo()

            // then
            assertEquals("", viewModel.selectedPoNo.value)
        }

    @Test
    fun `test show dialog confirm back state true`() =
        runTest {
            // when
            viewModel.setShowDialogConfirmBackState(true)

            // then
            assertEquals(true, viewModel.showConfirmBackDialogState.value)
        }

    @Test
    fun `test show invoice input dialog state true`() =
        runTest {
            // when
            viewModel.setShowInvoiceInputDialogState(true)

            // then
            assertEquals(true, viewModel.showInvoiceInputDialogState.value)
        }

    @Test
    fun `test loadPurchaseOrderDocument when session timeout then navigate to PI list`() =
        runTest {
            // Arrange
            val supplierCode = "SUP001"
            val supplierName = "Supplier A"
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = supplierCode,
                    supplierName = supplierName,
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier
            coEvery { getPurchaseOrderDocumentUseCase.execute(supplierCode = supplierCode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.SESSION_TIMEOUT,
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(PurchaseInvoiceListPOViewModel.UIState.NavToPIList),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `test onSearchPo empty should show warning dialog`() =
        runTest {
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_pleas_fill_up_po_document) } returns "Please fill PO"

            viewModel.onSearchPo("")

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.WARNING, item.errorData.type)
                assertEquals("Unable to continue", item.errorData.titleText)
                assertEquals("Please fill PO", item.errorData.messageText)
            }
        }

    @Test
    fun `test onSearchPo when success sets selectedPoNo and opens dialog`() =
        runTest {
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "SUP001",
                    supplierName = "Supplier",
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier
            coEvery {
                checkPurchaseOrderExistsUseCase.execute(
                    poNo = "PO001",
                    supplierCode = supplier.supplierCode,
                )
            } returns flowOf(Result.Success(mockk(relaxed = true)))

            viewModel.onSearchPo("PO001")

            assertEquals("PO001", viewModel.selectedPoNo.value)
            assertEquals(true, viewModel.showInvoiceInputDialogState.value)
        }

    @Test
    fun `test onSearchPo when error shows fallback strings`() =
        runTest {
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "SUP001",
                    supplierName = "Supplier",
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            coEvery {
                checkPurchaseOrderExistsUseCase.execute(
                    poNo = "POERR",
                    supplierCode = supplier.supplierCode,
                )
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                title = null, // trigger fallback
                                message = null, // trigger fallback
                            ),
                    ),
                )

            viewModel.onSearchPo("POERR")

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals("Unable to continue", item.errorData.titleText)
                assertEquals("Service failed", item.errorData.messageText)
            }
        }

    @Test
    fun `test onSearchPo when exception shows error with exception message`() =
        runTest {
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "SUP001",
                    supplierName = "Supplier",
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"

            val ex = RuntimeException("boom")
            coEvery {
                checkPurchaseOrderExistsUseCase.execute(
                    poNo = "POX",
                    supplierCode = supplier.supplierCode,
                )
            } answers { flow { throw ex } }

            viewModel.onSearchPo("POX")

            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals("Unable to continue", item.errorData.titleText)
                assertEquals("boom", item.errorData.messageText)
            }
        }

    @Test
    fun `test loadPurchaseOrderDocument error with canRetry true triggers retry on positive click`() =
        runTest {
            // Arrange
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "SUP001",
                    supplierName = "Supplier",
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier
            coEvery { getPurchaseOrderDocumentUseCase.execute(supplierCode = supplier.supplierCode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                message = "Error message",
                            ),
                        canRetry = true,
                    ),
                )

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                val errorItem = first as? BaseScreenState.Error ?: awaitItem() as BaseScreenState.Error
                errorItem.errorData.onPositiveClick?.invoke()
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(atLeast = 2) {
                getPurchaseOrderDocumentUseCase.execute(supplierCode = supplier.supplierCode)
            }
        }

    @Test
    fun `test createPurchaseInvoice when error should keep billNo`() =
        runTest {
            val poNo = "PO001"
            val billNo = "BILL001"
            val supplierCode = "SUP001"

            coEvery {
                createPurchaseInvoiceUseCase.execute(poNo = poNo, billNo = billNo, supplierCode = supplierCode)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                message = "oops",
                            ),
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"

            viewModel.createPurchaseInvoice(poNo, billNo, supplierCode)

            assertEquals(billNo, viewModel.keepBillNo.value)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `test createPurchaseInvoice success uses fallback timestamp when entries empty`() =
        runTest {
            val poNo = "POFALL"
            val billNo = "BILLFALL"
            val supplierCode = "SUPFALL"

            val model =
                mockk<PurchaseInvoiceCreateModel> {
                    every { entries } returns emptyList()
                    every { timestamp } returns "2025-01-01T00:00:00+07:00"
                }
            val uiModel = mockk<PurchaseInvoiceDetailUIModel>(relaxed = true)

            coEvery {
                createPurchaseInvoiceUseCase.execute(poNo = poNo, billNo = billNo, supplierCode = supplierCode)
            } returns flowOf(Result.Success(model))

            coEvery {
                uiModelMapper.purchaseInvoicePIUIMapper.mapPurchaseInvoiceCreateModelToUIModel(model)
            } returns uiModel

            mockkObject(DateUtils)
            every { DateUtils.getTimestampUnix(any()) } returns 4242L

            // trigger
            viewModel.createPurchaseInvoice(poNo, billNo, supplierCode)

            advanceUntilIdle()
            verify { purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(any()) }
            verify { purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(4242L) }

            unmockkAll()
        }

    @Test
    fun `test navigateToPIList emits success UI state`() =
        runTest {
            viewModel.navigateToPIList()
            viewModel.baseScreenState.test {
                assertEquals(
                    BaseScreenState.Success(PurchaseInvoiceListPOViewModel.UIState.NavToPIList),
                    awaitItem(),
                )
            }
        }

    @Test
    fun `test onSearchPo empty then positive click should reset base state`() =
        runTest {
            // Arrange
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_pleas_fill_up_po_document) } returns "Please fill PO"

            // Act
            viewModel.onSearchPo("")

            // Assert
            viewModel.baseScreenState.test {
                val error = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.WARNING, error.errorData.type)
                assertEquals("Unable to continue", error.errorData.titleText)
                assertEquals("Please fill PO", error.errorData.messageText)

                error.errorData.onPositiveClick?.invoke()

                val afterReset = awaitItem()
                assertTrue(
                    "Expected non-Error state after reset, but was $afterReset",
                    afterReset !is BaseScreenState.Error,
                )

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `test loadPurchaseOrderDocument throwable then positive click should reset and retry`() =
        runTest {
            // Arrange
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "SUP_THROW",
                    supplierName = "Supplier T",
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier

            val ex = RuntimeException("boom-throwable")
            coEvery { getPurchaseOrderDocumentUseCase.execute(supplierCode = supplier.supplierCode) } answers {
                flow { throw ex }
            }

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                val error = first as? BaseScreenState.Error ?: awaitItem() as BaseScreenState.Error

                assertTrue(error.errorData.titleText == "Unable to continue")
                assertTrue(error.errorData.messageText == "boom-throwable")

                error.errorData.onPositiveClick?.invoke()

                val afterReset = awaitItem()
                assertTrue(afterReset !is BaseScreenState.Error)

                cancelAndIgnoreRemainingEvents()
            }

            coVerify(atLeast = 2) {
                getPurchaseOrderDocumentUseCase.execute(supplierCode = supplier.supplierCode)
            }
        }

    @Test
    fun `test loadPurchaseOrderDocument throwable then negative click should only reset without retry`() =
        runTest {
            // Arrange
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "SUP_THROW_CLOSE",
                    supplierName = "Supplier T2",
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier

            val ex = RuntimeException("network-fail")
            coEvery { getPurchaseOrderDocumentUseCase.execute(supplierCode = supplier.supplierCode) } answers {
                flow { throw ex }
            }

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                val error = first as? BaseScreenState.Error ?: awaitItem() as BaseScreenState.Error

                error.errorData.onNegativeClick?.invoke()

                val afterReset = awaitItem()
                assertTrue(afterReset !is BaseScreenState.Error)

                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 1) {
                getPurchaseOrderDocumentUseCase.execute(supplierCode = supplier.supplierCode)
            }
        }

    @Test
    fun `loadPurchaseOrderDocument error with null message should fallback to service_failed string`() =
        runTest {
            // Arrange
            val supplier =
                PurchaseInvoiceSupplierDetailUIModel(
                    supplierCode = "SUP_FALLBACK",
                    supplierName = "Supplier",
                    isRefPo = true,
                )
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier

            // สร้าง Error ที่ message = null เพื่อบังคับ fallback
            coEvery { getPurchaseOrderDocumentUseCase.execute(supplier.supplierCode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                title = null, // ให้ fallback title ด้วยก็ได้ หรือจะตั้งค่าไว้ก็ได้
                                message = null, // จุดสำคัญ -> ต้องเป็น null
                            ),
                        canRetry = false,
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                val error = (first as? BaseScreenState.Error) ?: awaitItem() as BaseScreenState.Error

                // title ตกลงมาที่ fallback
                assertEquals("Unable to continue", error.errorData.titleText)
                // message ต้อง fallback เป็น "Service failed"
                assertEquals("Service failed", error.errorData.messageText)

                // canRetry = false → ไม่มี negative ปุ่ม (ตาม buildTachyonAlertDialog)
                assertEquals(null, error.errorData.negativeTextRes)
                // ปุ่มบวกเป็น Close อย่างเดียว
                assertEquals(R.string.text_close, error.errorData.positiveTextRes)

                cancelAndIgnoreRemainingEvents()
            }

            // ไม่มีการ retry ใดๆ
            coVerify(exactly = 1) { getPurchaseOrderDocumentUseCase.execute(supplier.supplierCode) }
        }

    @Test
    fun `handleLoadPurchaseOrderDocumentThrowable uses cause message when present`() =
        runTest {
            // e.cause?.message != null
            val throwable = RuntimeException("wrapper", RuntimeException("inner-msg"))

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            viewModel.handleLoadPurchaseOrderDocumentThrowable(throwable)

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("Unable", err.errorData.titleText)
                assertEquals("inner-msg", err.errorData.messageText) // ใช้ cause.message
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleLoadPurchaseOrderDocumentThrowable falls back when cause exists but message is null`() =
        runTest {
            // e.cause != null แต่ e.cause.message == null
            val throwable = RuntimeException("wrapper", RuntimeException()) // default message = null

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            viewModel.handleLoadPurchaseOrderDocumentThrowable(throwable)

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("Unable", err.errorData.titleText)
                assertEquals("Service failed", err.errorData.messageText) // fallback
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `handleLoadPurchaseOrderDocumentThrowable falls back when cause is null`() =
        runTest {
            // e.cause == null
            val throwable = RuntimeException("wrapper") // no cause

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            viewModel.handleLoadPurchaseOrderDocumentThrowable(throwable)

            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("Unable", err.errorData.titleText)
                assertEquals("Service failed", err.errorData.messageText) // fallback
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `buildTachyonAlertDialog WARNING should set showF1Message false`() =
        runTest {
            val dlg =
                viewModel.buildTachyonAlertDialog(
                    canRetry = false,
                    type = ErrorType.WARNING,
                    title = "t",
                    message = "m",
                    ref = null,
                    onRetry = { },
                    onClose = { },
                )
            assertEquals(false, dlg.showF1Message)
        }

    @Test
    fun `loadPurchaseOrderDocument error with canRetry false then positive click closes without retry`() =
        runTest {
            // Arrange
            val supplier = PurchaseInvoiceSupplierDetailUIModel("SUP_NO_RETRY", "Supplier", true)
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier
            coEvery { getPurchaseOrderDocumentUseCase.execute(supplier.supplierCode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                title = "t",
                                message = "m",
                            ),
                        canRetry = false,
                    ),
                )

            // Act
            viewModel.loadPurchaseOrderDocument()

            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                val err = (first as? BaseScreenState.Error) ?: awaitItem() as BaseScreenState.Error

                // negative should be null when canRetry=false
                assertEquals(null, err.errorData.negativeTextRes)

                // positive should only close, not retry
                err.errorData.onPositiveClick?.invoke()
                cancelAndIgnoreRemainingEvents()
            }

            // execute should be called only once (no retry)
            coVerify(exactly = 1) { getPurchaseOrderDocumentUseCase.execute(supplier.supplierCode) }
        }

    @Test
    fun `loadPurchaseOrderDocument error with canRetry true then negative click resets without retry`() =
        runTest {
            val supplier = PurchaseInvoiceSupplierDetailUIModel("SUP_ERR_NEG", "Supplier", true)
            coEvery { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns supplier
            coEvery { getPurchaseOrderDocumentUseCase.execute(supplier.supplierCode) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                title = "t",
                                message = "m",
                            ),
                        canRetry = true,
                    ),
                )

            viewModel.loadPurchaseOrderDocument()

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val err = (first as? BaseScreenState.Error) ?: awaitItem() as BaseScreenState.Error
                err.errorData.onNegativeClick?.invoke()
                val afterReset = awaitItem()
                assertTrue(afterReset !is BaseScreenState.Error)
                cancelAndIgnoreRemainingEvents()
            }

            // no retry
            coVerify(exactly = 1) { getPurchaseOrderDocumentUseCase.execute(supplier.supplierCode) }
        }

    @Test
    fun `hideKeyboard should call toggleSoftInput on InputMethodManager`() =
        runTest {
            // Arrange
            val mockContext = mockk<Context>()
            val mockImm = mockk<InputMethodManager>(relaxed = true)

            every { mockContext.getSystemService(Context.INPUT_METHOD_SERVICE) } returns mockImm

            // Act
            viewModel.hideKeyboard(mockContext)

            // Assert
            verify {
                mockImm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0)
            }
        }
}
