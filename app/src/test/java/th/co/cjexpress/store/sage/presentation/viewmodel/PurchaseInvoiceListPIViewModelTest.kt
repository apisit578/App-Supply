package th.co.cjexpress.store.sage.presentation.viewmodel

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.justRun
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
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseInvoiceDetailUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.GetPurchaseInvoiceDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceDetailLiveDataUseCase
import th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice.PurchaseInvoiceUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceItemPIUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceListPIViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers
import java.util.Date

class PurchaseInvoiceListPIViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var getPurchaseInvoiceDocumentUseCase: GetPurchaseInvoiceDocumentUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceUseCase: PurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var getPurchaseInvoiceDetailUseCase: GetPurchaseInvoiceDetailUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    // viewModel
    private lateinit var viewModel: PurchaseInvoiceListPIViewModel

    override fun setUp() {
        super.setUp()
        viewModel =
            PurchaseInvoiceListPIViewModel(
                getPurchaseInvoiceDocumentUseCase = getPurchaseInvoiceDocumentUseCase,
                getPurchaseInvoiceDetailUseCase = getPurchaseInvoiceDetailUseCase,
                purchaseInvoiceUseCase = purchaseInvoiceUseCase,
                purchaseInvoiceDetailLiveDataUseCase = purchaseInvoiceDetailLiveDataUseCase,
                uiModelMapper = uiModelMapper,
                dispatcherProvider = TestDispatchers(),
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun `test loadPurchaseInvoice when success and have items`() =
        runTest {
            // Arrange
            val mockPurchaseInvoiceDocumentList =
                PurchaseInvoiceDocumentListModel(
                    code = "SUCCESS",
                    ref = "mocked-ref-001",
                    message = "Data fetched successfully",
                    timestamp = "2025-06-17T17:29:40.16627+07:00",
                    data =
                        listOf(
                            PurchaseInvoiceDocumentItemModel(
                                piNo = "PI2506M04C",
                                poNo = "PO123456789",
                                billNo = "INV002",
                                createdDate = Date(),
                                docDate = Date(),
                                dueDate = Date(),
                                shipmentDate = Date(),
                            ),
                        ),
                )

            val mockPurchaseInvoiceDocumentListResult =
                flowOf(Result.Success(mockPurchaseInvoiceDocumentList))
            val purchaseInvoiceItemsPIUI =
                listOf(
                    PurchaseInvoiceItemPIUIModel(
                        piNo = mockPurchaseInvoiceDocumentList.data.first().piNo,
                        poNo = mockPurchaseInvoiceDocumentList.data.first().poNo,
                        createdDate =
                            mockPurchaseInvoiceDocumentList.data
                                .first()
                                .createdDate
                                .toStringDDMMYYYYFormat(),
                    ),
                )

            coEvery { getPurchaseInvoiceDocumentUseCase.execute(any()) } answers {
                mockPurchaseInvoiceDocumentListResult
            }

            coEvery { uiModelMapper.purchaseInvoicePIUIMapper.mapDocumentListToItemsPIUIModel(any()) } answers {
                purchaseInvoiceItemsPIUI
            }

            // Act
            viewModel.loadPurchaseInvoice()

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(data = null), awaitItem())
            }

            viewModel.piList.test {
                assertEquals(purchaseInvoiceItemsPIUI, awaitItem())
            }
        }

    @Test
    fun `test loadPurchaseInvoice when success and have no items`() =
        runTest {
            // Arrange
            val mockPurchaseInvoiceDocumentList =
                PurchaseInvoiceDocumentListModel(
                    code = "SUCCESS",
                    ref = "mocked-ref-001",
                    message = "Data fetched successfully",
                    timestamp = "2025-06-17T17:29:40.16627+07:00",
                    data = emptyList(),
                )

            val mockPurchaseInvoiceDocumentListResult =
                flowOf(Result.Success(mockPurchaseInvoiceDocumentList))

            val expectedDataResult = emptyList<PurchaseInvoiceItemPIUIModel>()

            coEvery { getPurchaseInvoiceDocumentUseCase.execute(any()) } answers {
                mockPurchaseInvoiceDocumentListResult
            }

            coEvery { uiModelMapper.purchaseInvoicePIUIMapper.mapDocumentListToItemsPIUIModel(any()) } answers {
                emptyList()
            }

            // Act
            viewModel.loadPurchaseInvoice()

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(data = null), awaitItem())
            }

            viewModel.piList.test {
                assertEquals(expectedDataResult, awaitItem())
            }
        }

    @Test
    fun `test loadPurchaseInvoice when success with error`() =
        runTest {
            // Arrange
            coEvery { getPurchaseInvoiceDocumentUseCase.execute(any()) } answers {
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
            viewModel.loadPurchaseInvoice()

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
    fun `test loadPurchaseInvoice when error`() =
        runTest {
            // Arrange
            val message = "Error!!!!!"
            val exception = RuntimeException(message)

            coEvery { getPurchaseInvoiceDocumentUseCase.execute(any()) } answers {
                flow { throw exception }
            }

            // Act
            viewModel.loadPurchaseInvoice()

            // Assert
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(ErrorType.ERROR, item.errorData.type)
                assertEquals(
                    resourceUtils.getString(R.string.text_unable_to_continue),
                    item.errorData.titleText,
                )
                assertEquals(exception.message, item.errorData.messageText)
            }
        }

    @Test
    fun `should update baseState error type to QUESTION when call onBackClick`() =
        runTest {
            // Arrange
            val expectedDataResult = ErrorType.QUESTION

            // Act
            viewModel.onBackClick {}

            // Assert
            viewModel.baseScreenState.test {
                val item = awaitItem() as BaseScreenState.Error
                assertEquals(expectedDataResult, item.errorData.type)
            }
        }

    @Test
    fun `should update baseState to null when call onDismissClick`() =
        runTest {
            // Arrange
            val expectedDataResult = null

            // Act
            viewModel.onDismissClick()

            // Assert
            viewModel.baseScreenState.test {
                assertEquals(BaseScreenState.Success(data = expectedDataResult), awaitItem())
            }
        }

    @Test
    fun `should navigate to PO list when supplier isRefPo true`() =
        runTest {
            // Arrange
            every { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns
                mockk {
                    every { isRefPo } returns true
                }

            // Act
            viewModel.onStartReceiveClick()

            // Assert
            viewModel.baseScreenState.test {
                val successItem = awaitItem() as BaseScreenState.Success<*>
                assertEquals(PurchaseInvoiceListPIViewModel.UIState.NavToPOList, successItem.data)
            }
        }

    @Test
    fun `should navigate to PO list when supplier isRefPo false`() =
        runTest {
            // Arrange
            every { purchaseInvoiceUseCase.getPurchaseInvoiceSupplier() } returns
                mockk {
                    every { supplierCode } returns ""
                    every { supplierName } returns ""
                    every { isRefPo } returns false
                }

            // Act
            viewModel.onStartReceiveClick()

            // Assert
            viewModel.showInvoiceInputDialogState.test {
                val item = awaitItem()
                assertEquals(true, item)
            }
        }

    @Test
    fun `getPurchaseInvoiceDetail should set detail and navigate when success (captured list)`() =
        runTest {
            // Arrange
            val piNo = "PI12434"
            val detailModel = mockk<PurchaseInvoiceDetailModel>(relaxed = true)

            val expectedUi =
                PurchaseInvoiceDetailUIModel(
                    supplierCode = "S001",
                    supplierName = "Supplier",
                    poNo = "PO1",
                    piNo = piNo,
                    billNo = "B001",
                    entries = androidx.compose.runtime.mutableStateListOf(),
                    isEdit = false,
                )

            // Act
            coEvery { getPurchaseInvoiceDetailUseCase.execute(piNo) } returns
                flowOf(Result.Success(detailModel))
            every {
                uiModelMapper.purchaseInvoicePIUIMapper
                    .mapPurchaseInvoiceDetailToUIModel(detailModel)
            } returns expectedUi

            every { purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(any()) } returns Unit
            every { purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(any()) } returns Unit

            // Assert
            viewModel.baseScreenState.test {
                viewModel.getPurchaseInvoiceDetail(piNo)
                val first = awaitItem()
                assertTrue(first is BaseScreenState.Loading || awaitItem() is BaseScreenState.Loading)
                val success = awaitItem() as BaseScreenState.Success<*>
                assertEquals(PurchaseInvoiceListPIViewModel.UIState.NavToPIDetail, success.data)
                cancelAndIgnoreRemainingEvents()
            }

            val result = mutableListOf<PurchaseInvoiceDetailUIModel>()
            verify(exactly = 1) {
                purchaseInvoiceDetailLiveDataUseCase.setPurchaseInvoiceDetail(capture(result))
            }
            assertTrue(result.isNotEmpty())
            val data = result.last()
            assertEquals(expectedUi.piNo, data.piNo)
            assertEquals(expectedUi.supplierCode, data.supplierCode)
            assertEquals(expectedUi.poNo, data.poNo)
            assertEquals(expectedUi.billNo, data.billNo)
            assertEquals(false, data.isEdit)

            verify(exactly = 1) { purchaseInvoiceDetailLiveDataUseCase.setUpdatedTimestamp(any()) }
        }

    @Test
    fun `getPurchaseInvoiceDetail should set error state when error`() =
        runTest {
            // Arrange
            val piNo = "PI123456"

            coEvery { getPurchaseInvoiceDetailUseCase.execute(piNo) } answers {
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.WARNING,
                                title = resourceUtils.getString(R.string.text_unable_to_continue),
                                message = resourceUtils.getString(R.string.text_service_failed),
                            ),
                    ),
                )
            }

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"

            // Act
            viewModel.getPurchaseInvoiceDetail(piNo)

            // Assert
            viewModel.baseScreenState.test {
                val errorItem = awaitItem() as BaseScreenState.Error

                assertEquals(ErrorType.WARNING, errorItem.errorData.type)
                assertEquals("Unable to continue", errorItem.errorData.titleText)
                assertEquals("Service failed", errorItem.errorData.messageText)
            }
        }

    @Test
    fun `updatePurchaseInvoiceDetailBillNo should update billNo and navigate to detail`() =
        runTest {
            // Arrange
            val billNo = "INV999ASFf"

            justRun { purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceDetailBillNo(billNo) }

            // Act
            viewModel.updatePurchaseInvoiceDetailBillNo(billNo)

            // Assert
            verify(exactly = 1) {
                purchaseInvoiceDetailLiveDataUseCase.updatePurchaseInvoiceDetailBillNo(
                    billNo,
                )
            }

            viewModel.baseScreenState.test {
                val item = awaitItem()
                assertTrue(item is BaseScreenState.Success<*>)

                val success = item as BaseScreenState.Success<*>
                assertEquals(PurchaseInvoiceListPIViewModel.UIState.NavToPIDetail, success.data)
            }
        }

    @Test
    fun `supplierCode should return value from purchaseInvoiceUseCase`() =
        runTest {
            // Arrange
            val expectedCode = "SUP1234"
            every { purchaseInvoiceUseCase.getInvoiceSupplierCode() } returns expectedCode

            // Act
            val actual = viewModel.supplierCode

            // Assert
            assertEquals(expectedCode, actual)
            verify(exactly = 1) { purchaseInvoiceUseCase.getInvoiceSupplierCode() }
        }

    @Test
    fun `logEventGetPIList should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = listOf(mockk<PurchaseInvoiceItemPIUIModel>())

            // when
            viewModel.logEventGetPIList(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.GET_PI_LIST,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectPI should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = mockk<PurchaseInvoiceDetailUIModel>()

            // when
            viewModel.logEventSelectPI(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_PI,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventStartOrderReceive should logInfo with correct action and message`() =
        runTest {
            // given
            val mockData = mockk<PurchaseInvoiceSupplierDetailUIModel>()

            // when
            viewModel.logEventStartOrderReceive(mockData)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.START_ORDER_RECEIVE,
                    message = mockData.toString(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
