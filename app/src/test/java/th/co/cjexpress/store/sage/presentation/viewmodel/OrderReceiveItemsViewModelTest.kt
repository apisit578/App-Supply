package th.co.cjexpress.store.sage.presentation.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType
import th.co.cjexpress.store.sage.analytics.usecase.TrackTimeEventUseCase
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorCodes.OrderReceiveErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.ScanBarcodeButtonType
import th.co.cjexpress.store.sage.data.model.orderReceive.ShipmentItemRequest
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentByBarcodeData
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentByBarcodeModel
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckItemQtyMoreRefQtyUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckLimitBatchUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckProductInDocumentUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.CheckTotePrefixUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.OrderReceiveCheckSkuUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentByBarcodeUseCase
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.ShipmentDocumentUseCase
import th.co.cjexpress.store.sage.presentation.mapper.UiModelMapper
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentByBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeBackState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeCheckState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeStartOverState
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemsViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.utils.TestDispatchers
import th.co.cjexpress.store.sage.domain.model.Result as DomainResult

class OrderReceiveItemsViewModelTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    @RelaxedMockK
    lateinit var checkLimitBatchUseCase: CheckLimitBatchUseCase

    @RelaxedMockK
    lateinit var checkProductInDocumentUseCase: CheckProductInDocumentUseCase

    @RelaxedMockK
    lateinit var checkItemQtyMoreRefQtyUseCase: CheckItemQtyMoreRefQtyUseCase

    @RelaxedMockK
    lateinit var orderReceiveCheckSkuUseCase: OrderReceiveCheckSkuUseCase

    @RelaxedMockK
    lateinit var checkTotePrefixUseCase: CheckTotePrefixUseCase

    @RelaxedMockK
    lateinit var shipmentDocumentByBarcodeUseCase: ShipmentDocumentByBarcodeUseCase

    @RelaxedMockK
    lateinit var trackTimeEventUseCase: TrackTimeEventUseCase

    @RelaxedMockK
    lateinit var uiModelMapper: UiModelMapper

    private lateinit var viewModel: OrderReceiveItemsViewModel

    override fun setUp() {
        super.setUp()
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable"
        every { resourceUtils.getString(R.string.text_service_failed) } returns "Service failed"
        every { resourceUtils.getString(R.string.text_tote) } returns "Tote"
        every { resourceUtils.getString(R.string.text_title_no_barcode_in_shipment_order) } returns "NoBarcodeTitle"
        every {
            resourceUtils.getString(
                R.string.text_detail_no_barcode_in_shipment_order,
                any(),
            )
        } returns "NoBarcodeInLD %s"
        every { resourceUtils.getString(R.string.text_title_error_not_found_order) } returns "NotFoundOrderTitle"
        every { resourceUtils.getString(R.string.text_description_error_not_found_order) } returns "NotFoundOrderDesc"
        every { resourceUtils.getString(R.string.text_close) } returns "Close"
        every { resourceUtils.getString(R.string.text_retry) } returns "Retry"

        every { shipmentDocumentUseCase.shipmentSummaryLiveData.value } returns null
        every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns null

        viewModel =
            OrderReceiveItemsViewModel(
                resourceUtils = resourceUtils,
                shipmentDocumentUseCase = shipmentDocumentUseCase,
                checkLimitBatchUseCase = checkLimitBatchUseCase,
                shipmentDocumentByBarcodeUseCase = shipmentDocumentByBarcodeUseCase,
                trackTimeEventUseCase = trackTimeEventUseCase,
                uiModelMapper = uiModelMapper,
                dispatcherProvider = TestDispatchers(),
                checkProductInDocumentUseCase = checkProductInDocumentUseCase,
                checkItemQtyMoreRefQtyUseCase = checkItemQtyMoreRefQtyUseCase,
                orderReceiveCheckSkuUseCase = orderReceiveCheckSkuUseCase,
                checkTotePrefixUseCase = checkTotePrefixUseCase,
            )
    }

    @Test
    fun `checkLimitBatch returns Error - should show error dialog and reset on close`() =
        runTest {
            // Arrange
            val barcode = "B001"

            every { checkLimitBatchUseCase.execute(0, barcode) } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = "X12345",
                            type = ErrorType.WARNING,
                            message = null,
                        ),
                    canRetry = true,
                )

            // Act
            viewModel.checkLimitBatch(barcode)

            // Assert
            viewModel.baseScreenState.test {
                val err = awaitItem()
                assert(err is BaseScreenState.Error)
                val dialog = (err as BaseScreenState.Error).errorData
                assertEquals("Unable", dialog.titleText)
                assertEquals("Service failed", dialog.messageText)
                dialog.onPositiveClick?.invoke()
                val reset = awaitItem()
                assertEquals(BaseScreenState.Idle, reset)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `happy path - product in document and qty ok - should upsert via saveShipment and set countActualQty`() =
        runTest {
            val barcode = "B002"
            val summaryUi =
                ShipmentDataItemUIModel(
                    barcode = barcode,
                    actualQty = 1,
                    isTote = false,
                    productName = "P",
                    qty = 1,
                    toteCode = "",
                    unitFactor = 1,
                    unitName = "U",
                    overQty = 0,
                )

            every { checkLimitBatchUseCase.execute(barcode = barcode) } returns
                DomainResult.Success(
                    Unit,
                )
            every { checkProductInDocumentUseCase.execute(barcode) } returns
                DomainResult.Success(
                    Unit,
                )
            every { checkItemQtyMoreRefQtyUseCase.execute(barcode) } returns
                DomainResult.Success(
                    summaryUi,
                )

            every {
                shipmentDocumentUseCase.setIncreaseShipmentDocumentByBarcode(barcode)
            } returns listOf(ShipmentItemRequest(barcode = barcode, actualQty = 2))

            val apiResp = mockk<ShipmentDocumentByBarcodeModel>(relaxed = true)
            every { apiResp.data } returns
                ShipmentByBarcodeData(
                    count = 1,
                    items = emptyList(),
                    sdNo = "SD12345",
                    sumOfActualQty = 7,
                )

            val mapped =
                ShipmentDocumentUIModel(
                    sdNo = "SD12345",
                    ldNo = "LD12345",
                    createdDate = "2023-10-10",
                    count = 5,
                    sumOfActualQty = 7,
                    items = mutableStateListOf(),
                )
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(apiResp) } returns mapped
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Success(
                        apiResp,
                    ),
                )
            every { shipmentDocumentUseCase.updateShipmentSummary(mapped) } returns Unit

            // Act
            viewModel.checkLimitBatch(barcode)

            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                if (first is BaseScreenState.Loading) {
                    assertEquals(BaseScreenState.Idle, awaitItem())
                } else {
                    assertEquals(BaseScreenState.Idle, first)
                }
                cancelAndIgnoreRemainingEvents()
            }

            // Assert
            coVerify(exactly = 1) { shipmentDocumentByBarcodeUseCase.execute(any()) }
            coVerify(exactly = 1) {
                uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(
                    apiResp,
                )
            }
            coVerify(exactly = 1) { shipmentDocumentUseCase.updateShipmentSummary(mapped) }
        }

    @Test
    fun `product not in document - NONE_TOTE_PREFIX checkSku success  prompt alert extra item`() =
        runTest {
            // Arrange
            val barcode = "B003"

            every { checkLimitBatchUseCase.execute(barcode = barcode) } returns
                DomainResult.Success(
                    Unit,
                )

            every { checkProductInDocumentUseCase.execute(barcode) } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = "NOT_IN_DOC",
                            type = ErrorType.WARNING,
                            message = null,
                        ),
                )

            every { checkTotePrefixUseCase.execute(barcode) } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = OrderReceiveErrorCodes.NONE_TOTE_PREFIX.name,
                            type = ErrorType.WARNING,
                            message = null,
                        ),
                )
            every { shipmentDocumentUseCase.getShipmentLDNo() } returns "LD123"

            val skuUi =
                ShipmentDataItemUIModel(
                    barcode = barcode,
                    actualQty = 1,
                    isTote = false,
                    productName = "P",
                    qty = 0,
                    toteCode = "",
                    unitFactor = 1,
                    unitName = "U",
                    overQty = 0,
                )
            coEvery { orderReceiveCheckSkuUseCase.execute(barcode) } returns
                flowOf(
                    DomainResult.Success(
                        skuUi,
                    ),
                )

            // Act
            viewModel.checkLimitBatch(barcode)
            viewModel.baseScreenState.test {
                val err = awaitItem()
                assert(err is BaseScreenState.Error)
                val dialog = (err as BaseScreenState.Error).errorData
                assertEquals(ErrorType.WARNING, dialog.type)
                assertEquals("NoBarcodeTitle", dialog.titleText)
                assertEquals("NoBarcodeInLD %s", dialog.messageText)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `happy path - product in document and qty ok - should upsert and set countActualQty`() =
        runTest {
            val barcode = "B002"
            val summaryUi =
                ShipmentDataItemUIModel(
                    barcode = barcode,
                    actualQty = 1,
                    isTote = false,
                    productName = "P",
                    qty = 1,
                    toteCode = "",
                    unitFactor = 1,
                    unitName = "U",
                    overQty = 0,
                )

            every { checkLimitBatchUseCase.execute(barcode = barcode) } returns
                DomainResult.Success(
                    Unit,
                )
            every { checkProductInDocumentUseCase.execute(barcode) } returns
                DomainResult.Success(
                    Unit,
                )
            every { checkItemQtyMoreRefQtyUseCase.execute(barcode) } returns
                DomainResult.Success(
                    summaryUi,
                )
            every { shipmentDocumentUseCase.setIncreaseShipmentDocumentByBarcode(barcode) } returns emptyList()

            val apiResp = mockk<ShipmentDocumentByBarcodeModel>(relaxed = true)
            every { apiResp.data } returns
                ShipmentByBarcodeData(
                    count = 1,
                    items = emptyList(),
                    sdNo = "123456",
                    sumOfActualQty = 7,
                )
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Success(
                        apiResp,
                    ),
                )

            val mapped = mockk<ShipmentDocumentUIModel>(relaxed = true)
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(apiResp) } returns mapped
            every { shipmentDocumentUseCase.updateShipmentSummary(mapped) } returns Unit

            // Act
            viewModel.checkLimitBatch(barcode)

            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                if (first is BaseScreenState.Loading) {
                    assertEquals(BaseScreenState.Idle, awaitItem())
                } else {
                    assertEquals(BaseScreenState.Idle, first)
                }
                cancelAndIgnoreRemainingEvents()
            }

            // Assert
            coVerify(exactly = 1) {
                uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(
                    apiResp,
                )
            }
            coVerify(exactly = 1) { shipmentDocumentUseCase.updateShipmentSummary(mapped) }
        }

    @Test
    fun `product not in document - NONE_TOTE_PREFIX checkSku success prompt alert extra item`() =
        runTest {
            // Arrange
            val barcode = "B003"
            every { checkLimitBatchUseCase.execute(barcode = barcode) } returns
                DomainResult.Success(
                    Unit,
                )
            every { checkProductInDocumentUseCase.execute(barcode) } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = "NOT_IN_DOC",
                            type = ErrorType.WARNING,
                            message = null,
                        ),
                )
            every { checkTotePrefixUseCase.execute(barcode) } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = OrderReceiveErrorCodes.NONE_TOTE_PREFIX.name,
                            type = ErrorType.WARNING,
                            message = null,
                        ),
                )
            every { shipmentDocumentUseCase.getShipmentLDNo() } returns "LD123"

            val skuUi =
                ShipmentDataItemUIModel(
                    barcode = barcode,
                    actualQty = 1,
                    isTote = false,
                    productName = "P",
                    qty = 0,
                    toteCode = "",
                    unitFactor = 1,
                    unitName = "U",
                    overQty = 0,
                )
            coEvery { orderReceiveCheckSkuUseCase.execute(barcode) } returns
                flowOf(
                    DomainResult.Success(
                        skuUi,
                    ),
                )

            // Act
            viewModel.checkLimitBatch(barcode)
            viewModel.baseScreenState.test {
                val err = awaitItem()
                assert(err is BaseScreenState.Error)
                val dialog = (err as BaseScreenState.Error).errorData
                assertEquals(ErrorType.WARNING, dialog.type)
                assertEquals("NoBarcodeTitle", dialog.titleText)
                assertEquals("NoBarcodeInLD %s", dialog.messageText)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `checkItemQtyMoreRefQty Error CHECK_ALLOW_ITEM then onPositiveClick checkReceiveOverQty success and save`() =
        runTest {
            val barcode = "B004"
            val summaryUi =
                ShipmentDataItemUIModel(
                    barcode = barcode,
                    actualQty = 1,
                    isTote = false,
                    productName = "P",
                    qty = 1,
                    toteCode = "",
                    unitFactor = 1,
                    unitName = "U",
                    overQty = 0,
                )

            every {
                checkItemQtyMoreRefQtyUseCase.execute(barcode)
            } returns
                DomainResult.Error(
                    data = summaryUi,
                    cause =
                        BusinessException(
                            errorCode = OrderReceiveErrorCodes.CHECK_ALLOW_ITEM.name,
                            type = ErrorType.WARNING,
                            title = "T",
                            message = "M",
                        ),
                )

            every { shipmentDocumentUseCase.setIncreaseShipmentDocumentByBarcode(barcode) } returns emptyList()

            val apiResp = mockk<ShipmentDocumentByBarcodeModel>(relaxed = true)
            every { apiResp.data } returns
                ShipmentByBarcodeData(
                    count = 1,
                    items = emptyList(),
                    sdNo = "SD123",
                    sumOfActualQty = 9,
                )
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Success(
                        apiResp,
                    ),
                )

            val mapped = mockk<ShipmentDocumentUIModel>(relaxed = true)
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(apiResp) } returns mapped
            every { shipmentDocumentUseCase.updateShipmentSummary(mapped) } returns Unit

            // Act
            viewModel.checkItemQtyMoreRefQty(barcode)

            // Assert
            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("T", err.errorData.titleText)
                assertEquals("M", err.errorData.messageText)
                err.errorData.onPositiveClick?.invoke()
                val next = awaitItem()
                if (next is BaseScreenState.Loading) {
                    assertEquals(BaseScreenState.Idle, awaitItem())
                } else {
                    assertEquals(BaseScreenState.Idle, next)
                }
                cancelAndIgnoreRemainingEvents()
            }

            // Assert
            coVerify(exactly = 1) { shipmentDocumentUseCase.updateShipmentSummary(mapped) }
        }

    @Test
    fun `mapTypeButtonToState START_OVER when items empty - should show warning dialog`() =
        runTest {
            val emptyItems = SnapshotStateList<ShipmentDataItemUIModel>().apply { clear() }
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                ShipmentDocumentUIModel(
                    items = emptyItems,
                    createdDate = "",
                    sdNo = "",
                    ldNo = "",
                    count = 0,
                    sumOfActualQty = 0,
                )
            viewModel =
                OrderReceiveItemsViewModel(
                    shipmentDocumentUseCase = shipmentDocumentUseCase,
                    shipmentDocumentByBarcodeUseCase = shipmentDocumentByBarcodeUseCase,
                    checkLimitBatchUseCase = checkLimitBatchUseCase,
                    checkProductInDocumentUseCase = checkProductInDocumentUseCase,
                    checkItemQtyMoreRefQtyUseCase = checkItemQtyMoreRefQtyUseCase,
                    orderReceiveCheckSkuUseCase = orderReceiveCheckSkuUseCase,
                    checkTotePrefixUseCase = checkTotePrefixUseCase,
                    trackTimeEventUseCase = trackTimeEventUseCase,
                    uiModelMapper = uiModelMapper,
                    dispatcherProvider = TestDispatchers(),
                    resourceUtils = resourceUtils,
                )

            // Act
            viewModel.mapTypeButtonToState(ScanBarcodeButtonType.START_OVER)

            // Assert
            viewModel.baseScreenState.test {
                val st = awaitItem()
                val dialog = (st as BaseScreenState.Error).errorData
                assertEquals("NotFoundOrderTitle", dialog.titleText)
                assertEquals("NotFoundOrderDesc", dialog.messageText)
                // simulate close
                dialog.onPositiveClick?.invoke()
                val idle = awaitItem()
                assertEquals(BaseScreenState.Idle, idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `mapTypeButtonToState BACK - should set Success BackState`() =
        runTest {
            // Act
            viewModel.mapTypeButtonToState(ScanBarcodeButtonType.BACK)

            // Assert
            viewModel.baseScreenState.test {
                val result = awaitItem()
                assertEquals(
                    BaseScreenState.Success(ShipmentDocumentByBarcodeBackState(null)),
                    result,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `dialogConfirmBackOrch - when items empty should navigate back`() =
        runTest {
            val emptyItems = SnapshotStateList<ShipmentDataItemUIModel>().apply { clear() }
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                ShipmentDocumentUIModel(
                    items = emptyItems,
                    createdDate = "",
                    sdNo = "",
                    ldNo = "",
                    count = 0,
                    sumOfActualQty = 0,
                )

            viewModel.dialogConfirmBackOrch()

            viewModel.baseScreenState.test {
                val st = awaitItem()
                assertEquals(BaseScreenState.Success(ShipmentDocumentByBarcodeBackState()), st)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `saveShipmentDocumentByBarcode error - should show dialog with retry then reset`() =
        runTest {
            // Arrange
            val barcode = "B999"
            every { shipmentDocumentUseCase.setIncreaseShipmentDocumentByBarcode(barcode) } returns emptyList()

            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                title = "T",
                                message = "M",
                            ),
                        canRetry = true,
                    ),
                )

            // Act
            viewModel.saveShipmentDocumentByBarcode(
                barcode = barcode,
                dataSubmit = null,
                dataRequest = emptyList(),
                isIncrease = true,
            )

            // Assert
            viewModel.baseScreenState.test {
                val st = awaitItem()
                val dialog = (st as BaseScreenState.Error).errorData
                assertEquals(true, dialog.showF1Message)
                assertEquals("T", dialog.titleText)
                assertEquals("M", dialog.messageText)
                dialog.onPositiveClick?.invoke()
                val idle = awaitItem()
                assertEquals(BaseScreenState.Idle, idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `checkTotePrefix success - should save tote and update count then idle`() =
        runTest {
            val barcode = "T001"
            // tote-prefix ผ่าน
            every { checkTotePrefixUseCase.execute(barcode) } returns
                DomainResult.Error(
                    data = barcode,
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            message = null,
                        ),
                )
            // dataRequest คืนรายการเดียว
            every {
                shipmentDocumentUseCase.setIncreaseShipmentDocumentByBarcode(barcode)
            } returns listOf(ShipmentItemRequest(1, barcode))
            // ไม่เคยมีใน batch มาก่อน => จะ add
            every { shipmentDocumentUseCase.searchShipmentBatchData(barcode) } returns null
            every { shipmentDocumentUseCase.addShipmentBatchItem(any()) } returns Unit
            every { shipmentDocumentUseCase.updateShipmentSummary(any()) } returns Unit

            val apiResp = mockk<ShipmentDocumentByBarcodeModel>(relaxed = true)
            every { apiResp.data.sumOfActualQty } returns 4
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(DomainResult.Success(apiResp))
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(apiResp) } returns
                mockk(relaxed = true)

            viewModel.checkTotePrefix(barcode)

            viewModel.baseScreenState.test {
                val state = awaitItem()
                Assert.assertTrue(state is BaseScreenState.Error)

                val dialog = (state as BaseScreenState.Error).errorData
                dialog.onPositiveClick?.invoke()
                val successState = awaitItem()
                assertEquals(BaseScreenState.Idle, successState)

                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `checkTotePrefix error with OTHER code - should reset to idle`() =
        runTest {
            val barcode = "X001"
            every {
                checkTotePrefixUseCase.execute(barcode)
            } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = "OTHER",
                            type = ErrorType.WARNING,
                            message = null,
                        ),
                )
            viewModel.baseScreenState.test {
                viewModel.checkTotePrefix(barcode)
                val first = awaitItem()
                if (first is BaseScreenState.Loading) {
                    assertEquals(BaseScreenState.Idle, awaitItem())
                } else {
                    assertEquals(BaseScreenState.Idle, first)
                }
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `checkItemQtyMoreRefQty error (not CHECK_ALLOW_ITEM) - positive click should just reset`() =
        runTest {
            val barcode = "B005"
            every {
                checkItemQtyMoreRefQtyUseCase.execute(barcode)
            } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = "SOMETHING_ELSE",
                            type = ErrorType.WARNING,
                            title = "T",
                            message = "M",
                        ),
                )
            viewModel.checkItemQtyMoreRefQty(barcode)
            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("T", err.errorData.titleText)
                assertEquals("M", err.errorData.messageText)
                err.errorData.onPositiveClick?.invoke()
                val idle = awaitItem()
                assertEquals(BaseScreenState.Idle, idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `checkSku error - canRetry false - close should reset`() =
        runTest {
            val barcode = "B006"
            coEvery { orderReceiveCheckSkuUseCase.execute(barcode) } returns
                flowOf(
                    DomainResult.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                title = "T",
                                message = "M",
                            ),
                        canRetry = false,
                    ),
                )
            viewModel.checkSku(barcode)
            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("T", err.errorData.titleText)
                assertEquals("M", err.errorData.messageText)
                err.errorData.onPositiveClick?.invoke() // close only
                val idle = awaitItem()
                assertEquals(BaseScreenState.Idle, idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `saveShipmentDocumentByBarcode error - no retry - close should reset`() =
        runTest {
            val barcode = "B008"
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                title = "T",
                                message = "M",
                            ),
                        canRetry = false,
                    ),
                )
            viewModel.saveShipmentDocumentByBarcode(
                barcode = barcode,
                dataSubmit = null,
                dataRequest = emptyList(),
                isIncrease = true,
            )
            viewModel.baseScreenState.test {
                val err = awaitItem() as BaseScreenState.Error
                assertEquals("T", err.errorData.titleText)
                assertEquals("M", err.errorData.messageText)
                err.errorData.onPositiveClick?.invoke()
                val idle = awaitItem()
                assertEquals(BaseScreenState.Idle, idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `saveShipmentDocumentByBarcode throws exception - should show F1 error then idle`() =
        runTest {
            val barcode = "B009"
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flow { throw RuntimeException("boom") }
            viewModel.saveShipmentDocumentByBarcode(
                barcode = barcode,
                dataSubmit = null,
                dataRequest = emptyList(),
                isIncrease = true,
            )
            viewModel.baseScreenState.test {
                val st = awaitItem()
                val err = if (st is BaseScreenState.Loading) awaitItem() else st
                val dialog = (err as BaseScreenState.Error).errorData
                assertEquals(true, dialog.showF1Message)
                dialog.onPositiveClick?.invoke()
                val idle = awaitItem()
                assertEquals(BaseScreenState.Idle, idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `mapTypeButtonToState CHECK - should set Success CheckState`() =
        runTest {
            viewModel.mapTypeButtonToState(ScanBarcodeButtonType.CHECK)
            viewModel.baseScreenState.test {
                val result = awaitItem()
                assertEquals(
                    BaseScreenState.Success(ShipmentDocumentByBarcodeCheckState(null)),
                    result,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `mapTypeButtonToState START_OVER when items not empty - should emit StartOver state with current sum`() =
        runTest {
            val items =
                mutableStateListOf(
                    ShipmentDataItemUIModel(
                        actualQty = 1,
                        barcode = "X",
                        isTote = false,
                        toteCode = "T",
                        productName = "P",
                        qty = 1,
                        unitName = "U",
                        unitFactor = 1,
                        overQty = 0,
                    ),
                )
            every { shipmentDocumentUseCase.shipmentBatchLiveData.value } returns
                ShipmentDocumentUIModel(
                    items = items,
                    createdDate = "",
                    sdNo = "",
                    ldNo = "",
                    count = 1,
                    sumOfActualQty = 12,
                )
            viewModel =
                OrderReceiveItemsViewModel(
                    resourceUtils = resourceUtils,
                    shipmentDocumentUseCase = shipmentDocumentUseCase,
                    checkLimitBatchUseCase = checkLimitBatchUseCase,
                    shipmentDocumentByBarcodeUseCase = shipmentDocumentByBarcodeUseCase,
                    trackTimeEventUseCase = trackTimeEventUseCase,
                    uiModelMapper = uiModelMapper,
                    dispatcherProvider = TestDispatchers(),
                    checkProductInDocumentUseCase = checkProductInDocumentUseCase,
                    checkItemQtyMoreRefQtyUseCase = checkItemQtyMoreRefQtyUseCase,
                    orderReceiveCheckSkuUseCase = orderReceiveCheckSkuUseCase,
                    checkTotePrefixUseCase = checkTotePrefixUseCase,
                )

            viewModel.setCountActualQty(12)

            viewModel.mapTypeButtonToState(ScanBarcodeButtonType.START_OVER)
            viewModel.baseScreenState.test {
                val result = awaitItem()
                assertEquals(
                    BaseScreenState.Success(
                        ShipmentDocumentByBarcodeStartOverState(
                            data =
                                ShipmentDocumentByBarcodeUIModel(
                                    sumOfActualQty = 12,
                                ),
                        ),
                    ),
                    result,
                )
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `removeShipmentBatchItem - should reduce shipment document and reset state`() =
        runTest {
            // Arrange
            val item =
                ShipmentDataItemUIModel(
                    actualQty = 1,
                    barcode = "ITEM123",
                    isTote = false,
                    toteCode = "TOTE1",
                    productName = "Product Name",
                    qty = 1,
                    unitName = "PCS",
                    unitFactor = 1,
                    overQty = 0,
                )
            every { shipmentDocumentUseCase.setReduceShipmentDocumentByBarcode(item.barcode) } returns
                listOf(
                    ShipmentItemRequest(barcode = item.barcode, actualQty = 0),
                )
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Success(
                        mockk(relaxed = true),
                    ),
                )

            // Act
            viewModel.removeShipmentBatchItem(item)

            // Assert
            coVerify(exactly = 1) { shipmentDocumentUseCase.setReduceShipmentDocumentByBarcode(item.barcode) }
            coVerify(exactly = 1) { shipmentDocumentByBarcodeUseCase.execute(any()) }
            viewModel.baseScreenState.test {
                assert(awaitItem() is BaseScreenState.Idle)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `promptAlertExtraItem positive - allowExtraItem success upsert and idle`() =
        runTest {
            val data =
                ShipmentDataItemUIModel(
                    barcode = "EXTRA_BARCODE",
                    actualQty = 1,
                    isTote = false,
                    productName = "Extra",
                    qty = 0,
                    toteCode = "",
                    unitFactor = 1,
                    unitName = "U",
                    overQty = 0,
                )

            every { shipmentDocumentUseCase.setIncreaseShipmentDocumentByBarcode(data.barcode) } returns
                listOf(ShipmentItemRequest(barcode = data.barcode, actualQty = 1))
            every { shipmentDocumentUseCase.searchShipmentBatchData(data.barcode) } returns null
            every { shipmentDocumentUseCase.addShipmentBatchItem(any()) } returns Unit

            val api = mockk<ShipmentDocumentByBarcodeModel>(relaxed = true)
            every { api.data.sumOfActualQty } returns 5
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Success(
                        api,
                    ),
                )

            val mapped = mockk<ShipmentDocumentUIModel>(relaxed = true)
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(api) } returns mapped
            every { shipmentDocumentUseCase.updateShipmentSummary(mapped) } returns Unit

            // Act
            viewModel.promptAlertExtraItem(data)
            viewModel.baseScreenState.test {
                val dialogState = awaitItem() as BaseScreenState.Error
                dialogState.errorData.onPositiveClick?.invoke()
                val maybe = awaitItem()
                if (maybe is BaseScreenState.Loading) {
                    assertEquals(BaseScreenState.Idle, awaitItem())
                } else {
                    assertEquals(BaseScreenState.Idle, maybe)
                }
                cancelAndIgnoreRemainingEvents()
            }

            // Assert
            coVerify(exactly = 1) { shipmentDocumentUseCase.addShipmentBatchItem(any()) }
            coVerify(exactly = 1) { shipmentDocumentUseCase.updateShipmentSummary(mapped) }
        }

    @Test
    fun `saveShipmentDocumentByBarcode success - updates existing batch item (no add)`() =
        runTest {
            val barcode = "ABC123"
            every { shipmentDocumentUseCase.searchShipmentBatchData(barcode) } returns
                ShipmentDataItemUIModel(1, barcode, false, "", "P", 1, "U", 1, 0)
            every { shipmentDocumentUseCase.updateShipmentBatch(barcode, true) } returns Unit
            every { shipmentDocumentUseCase.addShipmentBatchItem(any()) } returns Unit

            val api = mockk<ShipmentDocumentByBarcodeModel>(relaxed = true)
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Success(
                        api,
                    ),
                )

            val mapped = mockk<ShipmentDocumentUIModel>(relaxed = true)
            every { uiModelMapper.shipmentDocumentByBarcodeUIMapper.toUIModel(api) } returns mapped
            every { shipmentDocumentUseCase.updateShipmentSummary(mapped) } returns Unit

            // Act
            viewModel.saveShipmentDocumentByBarcode(
                barcode = barcode,
                dataSubmit = null,
                dataRequest = listOf(ShipmentItemRequest(2, barcode)),
                isIncrease = true,
            )

            // Assert
            coVerify(exactly = 1) { shipmentDocumentUseCase.updateShipmentBatch(barcode, true) }
            coVerify(exactly = 0) { shipmentDocumentUseCase.addShipmentBatchItem(any()) }
            coVerify(exactly = 1) { shipmentDocumentUseCase.updateShipmentSummary(mapped) }
        }

    @Test
    fun `checkSku throws exception - should show F1 error then idle`() =
        runTest {
            val barcode = "ABC123"
            coEvery { orderReceiveCheckSkuUseCase.execute(barcode) } returns
                flow {
                    throw RuntimeException(
                        "ERROR",
                    )
                }

            viewModel.checkSku(barcode)

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val err = if (first is BaseScreenState.Loading) awaitItem() else first
                val dialog = (err as BaseScreenState.Error).errorData
                assertEquals(true, dialog.showF1Message)
                dialog.onPositiveClick?.invoke()
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `set item detail and remove batch model - delegates to usecase`() =
        runTest {
            val item = ShipmentDataItemUIModel(1, "ABC123", false, "", "PRODUCT", 0, "U", 1, 0)
            every { shipmentDocumentUseCase.setShipmentDocumentItemDetail(item) } returns Unit
            every { shipmentDocumentUseCase.removeShipmentBatchModel() } returns Unit

            viewModel.setShipmentDocumentItemDetail(item)
            viewModel.removeShipmentBatchModel()

            coVerify(exactly = 1) { shipmentDocumentUseCase.setShipmentDocumentItemDetail(item) }
            coVerify(exactly = 1) { shipmentDocumentUseCase.removeShipmentBatchModel() }
        }

    @Test
    fun `log helpers - just invoke to cover methods`() =
        runTest {
            viewModel.logEventScanBarcode("Z-1")
            viewModel.logEventSubmitSummaryOrder()
            viewModel.logEventOnBack(true)
            viewModel.logEventOnBack(false)
            viewModel.logEventOnSaveOrder(true)
            viewModel.logEventOnSaveOrder(false)
            viewModel.logEventRemoveItem(
                ShipmentDataItemUIModel(1, "Z-2", false, "", "P", 0, "U", 1, 0),
            )
        }

    @Test
    fun `checkSku error canRetry true - positive click should retry`() =
        runTest {
            val barcode = "B007"
            coEvery { orderReceiveCheckSkuUseCase.execute(barcode) } returnsMany
                listOf(
                    flowOf(
                        DomainResult.Error(
                            cause =
                                BusinessException(
                                    errorCode = "E1",
                                    type = ErrorType.ERROR,
                                    title = "T1",
                                    message = "M1",
                                ),
                            canRetry = true,
                        ),
                    ),
                    flowOf(
                        DomainResult.Error(
                            cause =
                                BusinessException(
                                    errorCode = "E2",
                                    type = ErrorType.ERROR,
                                    title = "T2",
                                    message = "M2",
                                ),
                            canRetry = false,
                        ),
                    ),
                )

            viewModel.checkSku(barcode)

            viewModel.baseScreenState.test {
                val first = awaitItem()
                val err = if (first is BaseScreenState.Loading) awaitItem() else first
                val dialog = (err as BaseScreenState.Error).errorData
                dialog.onPositiveClick?.invoke()
                cancelAndIgnoreRemainingEvents()
            }
            coVerify(exactly = 2) { orderReceiveCheckSkuUseCase.execute(barcode) }
        }

    @Test
    fun `saveShipmentDocumentByBarcode - when upstream throws, shows F1 (no retry)`() =
        runTest {
            // Arrange
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Title"
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flow { throw RuntimeException("Error") }

            val barcode = "ABC123"

            // Assert
            viewModel.baseScreenState.test {
                awaitItem()

                viewModel.saveShipmentDocumentByBarcode(
                    barcode = barcode,
                    dataSubmit = null,
                    dataRequest = emptyList(),
                    isIncrease = true,
                )
                advanceUntilIdle()
                val next = awaitItem()
                if (next is BaseScreenState.Loading) next else awaitItem()
                val err = awaitItem() as BaseScreenState.Error
                val dialog = err.errorData

                assertTrue(dialog.showF1Message)
                assertEquals("Title", dialog.titleText)
                assertEquals("Error", dialog.messageText)
                dialog.onPositiveClick?.invoke()
                advanceUntilIdle()
                assertEquals(BaseScreenState.Idle, expectMostRecentItem())
                cancelAndIgnoreRemainingEvents()
            }

            coVerify(exactly = 1) { shipmentDocumentByBarcodeUseCase.execute(any()) }
            confirmVerified(shipmentDocumentByBarcodeUseCase)
        }

    @Test
    fun `checkLimitBatch error with specific type - should show dialog and reset`() =
        runTest {
            // Arrange
            val barcode = "B001"
            every { checkLimitBatchUseCase.execute(barcode = barcode) } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.ERROR,
                            title = "Custom Error",
                            message = "Custom Message",
                        ),
                )

            // Act
            viewModel.checkLimitBatch(barcode)

            // Assert
            viewModel.baseScreenState.test {
                val errorState = awaitItem() as BaseScreenState.Error
                assertEquals("Custom Error", errorState.errorData.titleText)
                assertEquals("Custom Message", errorState.errorData.messageText)
                errorState.errorData.onPositiveClick?.invoke()
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `checkProductInDocument error - should call checkTotePrefix`() =
        runTest {
            // Arrange
            val barcode = "B002"
            every { checkProductInDocumentUseCase.execute(barcode) } returns
                DomainResult.Error(
                    cause =
                        BusinessException(
                            type = ErrorType.ERROR,
                            errorCode = ErrorCodes.NOT_FOUND,
                            title = "Title",
                            message = "Message",
                        ),
                )
            every { checkTotePrefixUseCase.execute(barcode) } returns DomainResult.Success(barcode)

            // Act
            viewModel.checkProductInDocument(barcode)

            // Assert
            coVerify { checkTotePrefixUseCase.execute(barcode) }
        }

    @Test
    fun `checkSku error - canRetry true - negative click should reset`() =
        runTest {
            // Arrange
            val barcode = "B007N"
            coEvery { orderReceiveCheckSkuUseCase.execute(barcode) } returns
                flowOf(
                    DomainResult.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                title = "T",
                                message = "M",
                            ),
                        canRetry = true,
                    ),
                )
            // Act
            viewModel.checkSku(barcode)
            // Assert
            viewModel.baseScreenState.test {
                val first = awaitItem()
                val err = if (first is BaseScreenState.Loading) awaitItem() else first
                val dialog = (err as BaseScreenState.Error).errorData
                dialog.onNegativeClick?.invoke()
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `saveShipmentDocumentByBarcode error - canRetry true - negative click should reset`() =
        runTest {
            // Arrange
            val barcode = "B800"
            coEvery { shipmentDocumentByBarcodeUseCase.execute(any()) } returns
                flowOf(
                    DomainResult.Error(
                        cause =
                            BusinessException(
                                errorCode = "E",
                                type = ErrorType.ERROR,
                                title = "T",
                                message = "M",
                            ),
                        canRetry = true,
                    ),
                )
            // Act
            viewModel.saveShipmentDocumentByBarcode(
                barcode = barcode,
                dataSubmit = null,
                dataRequest = emptyList(),
                isIncrease = true,
            )
            // Assert
            viewModel.baseScreenState.test {
                val st = awaitItem()
                val dialog = (st as BaseScreenState.Error).errorData
                dialog.onNegativeClick?.invoke()
                assertEquals(BaseScreenState.Idle, awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `trackTimeTracking should call logTimeEventUseCase track`() =
        runTest {
            // Arrange
            val type = ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS
            val status = ProcessStatusType.IN_PROCESS

            // Act
            viewModel.trackTimeTracking(type, status)

            // Assert
            coVerify { trackTimeEventUseCase.track(type = type, status = status) }
        }

    @Test
    fun `stopTimeTracking should call logTimeEventUseCase stop`() =
        runTest {
            // Arrange
            val type = ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS

            // Act
            viewModel.stopTimeTracking(type)

            // Assert
            coVerify { trackTimeEventUseCase.stop(type = type) }
        }
}
