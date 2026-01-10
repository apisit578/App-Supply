package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import androidx.compose.runtime.mutableStateListOf
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftItemModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SaveDraftPurchaseInvoiceUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var purchaseInvoiceUseCase: PurchaseInvoiceUseCase

    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveDataUseCase: PurchaseInvoiceDetailLiveDataUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var saveDraftPurchaseInvoiceUseCase: SaveDraftPurchaseInvoiceUseCase

    override fun setUp() {
        super.setUp()
        saveDraftPurchaseInvoiceUseCase =
            SaveDraftPurchaseInvoiceUseCase(
                repository,
                purchaseInvoiceUseCase,
                purchaseInvoiceDetailLiveDataUseCase,
                resourceUtils,
            )
    }

    @Test
    fun test_saveDraftPurchaseInvoice_success_when_actualQty_not_over_expect_result_success() =
        runTest {
            // given
            val dataMock =
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
                                ),
                        ),
                )
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "123456789",
                                actualQty = 0,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            every { purchaseInvoiceDetailLiveDataUseCase.updatedTimestamp.value } returns 0L

            coEvery {
                repository.saveDraftPurchaseInvoice(any(), any(), any(), any(), any(), any(), any())
            } returns flowOf(Result.Success(data = dataMock))
            // when
            val flow =
                saveDraftPurchaseInvoiceUseCase.execute(
                    product =
                        PurchaseInvoiceDraftItemModel(
                            barcode = "123456789",
                            actualQty = 1,
                            qty = 10,
                            unitFactor = 1,
                            unitName = "pcs",
                            productName = "Test Product",
                            hhTimestamp = 0L,
                        ),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = dataMock), result)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_success_when_actualQty_over_expect_result_error() =
        runTest {
            // given
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "123456789",
                                actualQty = 11,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            every { purchaseInvoiceDetailLiveDataUseCase.updatedTimestamp.value } returns 0L

            // when
            val flow =
                saveDraftPurchaseInvoiceUseCase.execute(
                    product =
                        PurchaseInvoiceDraftItemModel(
                            barcode = "123456789",
                            actualQty = 11,
                            qty = 10,
                            unitFactor = 1,
                            unitName = "pcs",
                            productName = "Test Product",
                            hhTimestamp = 0L,
                        ),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_failed_when_purchaseInvoiceDetail_null_expect_result_error() =
        runTest {
            // given
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                null

            // when
            val flow =
                saveDraftPurchaseInvoiceUseCase.execute(
                    product =
                        PurchaseInvoiceDraftItemModel(
                            barcode = "123456789",
                            actualQty = 11,
                            qty = 10,
                            unitFactor = 1,
                            unitName = "pcs",
                            productName = "Test Product",
                            hhTimestamp = 0L,
                        ),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_failed_when_barcode_not_found_expect_result_error() =
        runTest {
            // given
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "11111111",
                                actualQty = 11,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )

            // when
            val flow =
                saveDraftPurchaseInvoiceUseCase.execute(
                    product =
                        PurchaseInvoiceDraftItemModel(
                            barcode = "123456789",
                            actualQty = 11,
                            qty = 10,
                            unitFactor = 1,
                            unitName = "pcs",
                            productName = "Test Product",
                            hhTimestamp = 0L,
                        ),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_failed_expect_result_error() =
        runTest {
            // given
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "1234567890",
                                actualQty = 0,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            coEvery {
                repository.saveDraftPurchaseInvoice(any(), any(), any(), any(), any(), any(), any())
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
            val flow =
                saveDraftPurchaseInvoiceUseCase.execute(
                    product =
                        PurchaseInvoiceDraftItemModel(
                            barcode = "123456789",
                            actualQty = 1,
                            qty = 10,
                            unitFactor = 1,
                            unitName = "pcs",
                            productName = "Test Product",
                            hhTimestamp = 0L,
                        ),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_should_pass_actualQty_plus_one() =
        runTest {
            // given
            val inputActualQty = 3
            val expectedQty = inputActualQty + 1

            val product =
                PurchaseInvoiceDraftItemModel(
                    barcode = "123456789",
                    actualQty = inputActualQty,
                    qty = 10,
                    unitFactor = 1,
                    unitName = "pcs",
                    productName = "Test Product",
                    hhTimestamp = 0L,
                )

            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "SUP456",
                    poNo = "",
                    piNo = "PI789",
                    billNo = "BILL999",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = product.barcode,
                                actualQty = 0,
                                productName = "test product",
                                unitName = "แพค",
                                unitFactor = 1,
                                qty = 10,
                                salePrice = 0.0,
                            ),
                        ),
                )
            every { purchaseInvoiceDetailLiveDataUseCase.updatedTimestamp.value } returns 0L

            val responseMock =
                PurchaseInvoiceDraftModel(
                    code = "",
                    ref = "",
                    message = "",
                    timestamp = "",
                    data =
                        PurchaseInvoiceDraftDataModel(
                            piNo = "PI789",
                            items =
                                listOf(
                                    PurchaseInvoiceDraftItemModel(
                                        barcode = product.barcode,
                                        actualQty = expectedQty,
                                        qty = 10,
                                        unitFactor = 1,
                                        unitName = "pcs",
                                        productName = "Test Product",
                                        hhTimestamp = 0L,
                                    ),
                                ),
                        ),
                )

            coEvery {
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI789",
                    docNo = "DOC123",
                    flagPO = any(),
                    supplierCode = "SUP456",
                    billNo = "BILL999",
                    items =
                        match {
                            it.size == 1 && it[0].actualQty == expectedQty
                        },
                    timestamp = 0L,
                )
            } returns flowOf(Result.Success(data = responseMock))

            // when
            val flow = saveDraftPurchaseInvoiceUseCase.execute(product)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_when_actualQty_over_qty_and_refPo_should_return_warning_error() =
        runTest {
            // given
            val qty = 10
            val product =
                PurchaseInvoiceDraftItemModel(
                    barcode = "123456789",
                    actualQty = 11,
                    qty = qty,
                    unitFactor = 1,
                    unitName = "pcs",
                    productName = "Test Product",
                    hhTimestamp = 0L,
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "ไม่สามารถทำรายการได้"
            every { resourceUtils.getString(R.string.text_alert_over_actual_qty) } returns "จำนวนรับต้องไม่เกินจำนวนอ้างอิง"

            every { purchaseInvoiceUseCase.isRefPo() } returns true

            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = product.barcode,
                                actualQty = qty,
                                qty = qty,
                                productName = "",
                                unitName = "",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                        ),
                )

            // when
            val flow = saveDraftPurchaseInvoiceUseCase.execute(product)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, error.type)
                assertEquals("จำนวนรับต้องไม่เกินจำนวนอ้างอิง", error.message)
            }
        }

    @Test
    fun test_saveDraftPurchaseInvoice_when_refPo_but_api_error_should_return_error() =
        runTest {
            // given
            val qty = 10
            val product =
                PurchaseInvoiceDraftItemModel(
                    barcode = "123456789",
                    actualQty = 8,
                    qty = qty,
                    unitFactor = 1,
                    unitName = "pcs",
                    productName = "Test Product",
                    hhTimestamp = 0L,
                )

            every { purchaseInvoiceUseCase.isRefPo() } returns true
            every { purchaseInvoiceDetailLiveDataUseCase.updatedTimestamp.value } returns null
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "",
                    poNo = "",
                    piNo = "",
                    billNo = "",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = product.barcode,
                                actualQty = qty,
                                qty = qty,
                                productName = "",
                                unitName = "",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                        ),
                )
            coEvery {
                repository.saveDraftPurchaseInvoice(any(), any(), any(), any(), any(), any(), any())
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
            val flow = saveDraftPurchaseInvoiceUseCase.execute(product)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun `handleError with NOT_FOUND should return warning`() {
        // given
        val error =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = ErrorCodes.NOT_FOUND,
                        type = ErrorType.ERROR,
                        title = "old",
                        message = "NOT_FOUND",
                    ),
            )

        // when
        val result = saveDraftPurchaseInvoiceUseCase.handleError(error)

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.WARNING, result.cause.type)
        assertEquals(result.cause.errorCode, ErrorCodes.NOT_FOUND)
    }

    @Test
    fun `handleError with DOC_NOT_FOUND should return warning with piNo`() {
        // given
        val error =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = ErrorCodes.DOC_NOT_FOUND,
                        type = ErrorType.ERROR,
                        title = "old",
                        message = "DOC_NOT_FOUND",
                    ),
            )

        // when
        val result = saveDraftPurchaseInvoiceUseCase.handleError(error)

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.WARNING, result.cause.type)
        assertEquals(result.cause.errorCode, ErrorCodes.DOC_NOT_FOUND)
    }

    @Test
    fun `handleError with INVALID_STATUS should return warning`() {
        // given
        val error =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = ErrorCodes.INVALID_STATUS,
                        type = ErrorType.ERROR,
                        title = "old",
                        message = "INVALID_STATUS",
                    ),
            )

        // when
        val result = saveDraftPurchaseInvoiceUseCase.handleError(error)

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.WARNING, result.cause.type)
        assertEquals(result.cause.errorCode, ErrorCodes.INVALID_STATUS)
    }

    @Test
    fun `handleError with DUPLICATE_DATA should return warning`() {
        // given
        val error =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = ErrorCodes.DUPLICATE_DATA,
                        type = ErrorType.ERROR,
                        title = "old",
                        message = "DUPLICATE_DATA",
                    ),
            )

        // when
        val result = saveDraftPurchaseInvoiceUseCase.handleError(error)

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.WARNING, result.cause.type)
        assertEquals(result.cause.errorCode, ErrorCodes.DUPLICATE_DATA)
    }

    @Test
    fun `handleError with SUPPLIER_INACTIVE should return warning`() {
        // given
        val error =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = ErrorCodes.SUPPLIER_INACTIVE,
                        type = ErrorType.ERROR,
                        title = "old",
                        message = "SUPPLIER_INACTIVE",
                    ),
            )

        // when
        val result = saveDraftPurchaseInvoiceUseCase.handleError(error)

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.WARNING, result.cause.type)
        assertEquals(result.cause.errorCode, ErrorCodes.SUPPLIER_INACTIVE)
    }

    @Test
    fun `handleError with INTERNAL_SERVER_ERROR should return warning`() {
        // given
        val error =
            Result.Error(
                data = null,
                cause =
                    BusinessException(
                        errorCode = ErrorCodes.INTERNAL_SERVER_ERROR,
                        type = ErrorType.ERROR,
                        title = "INTERNAL_SERVER_ERROR",
                        message = "INTERNAL_SERVER_ERROR",
                    ),
            )

        // when
        val result = saveDraftPurchaseInvoiceUseCase.handleError(error)

        // then
        assertTrue(result is Result.Error)
        assertEquals(ErrorType.ERROR, result.cause.type)
        assertEquals(result.cause.errorCode, ErrorCodes.INTERNAL_SERVER_ERROR)
    }

    @Test
    fun test_executeList_when_actualQty_over_qty_and_refPo_should_return_warning_error() =
        runTest {
            // given
            val products =
                listOf(
                    PurchaseInvoiceDraftItemModel(
                        barcode = "111",
                        actualQty = 8,
                        qty = 10,
                        unitFactor = 1,
                        unitName = "pcs",
                        productName = "Product A",
                        hhTimestamp = 0L,
                    ),
                    PurchaseInvoiceDraftItemModel(
                        barcode = "222",
                        actualQty = 12,
                        qty = 10,
                        unitFactor = 1,
                        unitName = "pcs",
                        productName = "Product B",
                        hhTimestamp = 0L,
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "ไม่สามารถทำรายการได้"
            every { resourceUtils.getString(R.string.text_alert_over_actual_qty) } returns "จำนวนรับต้องไม่เกินจำนวนอ้างอิง"
            every { purchaseInvoiceUseCase.isRefPo() } returns true

            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "SUP",
                    poNo = "PO1",
                    piNo = "PI1",
                    billNo = "B1",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "111",
                                actualQty = 0,
                                qty = 10,
                                productName = "Product A",
                                unitName = "pcs",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                            PurchaseInvoiceEntryUIModel(
                                barcode = "222",
                                actualQty = 0,
                                qty = 10,
                                productName = "Product B",
                                unitName = "pcs",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                        ),
                )

            // when
            val flow = saveDraftPurchaseInvoiceUseCase.executeList(products)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, error.type)
                assertEquals("จำนวนรับต้องไม่เกินจำนวนอ้างอิง", error.message)
            }
        }

    @Test
    fun test_executeList_success_when_refPo_and_not_over_qty() =
        runTest {
            // given
            every { purchaseInvoiceUseCase.isRefPo() } returns true

            val products =
                listOf(
                    PurchaseInvoiceDraftItemModel(
                        barcode = "111",
                        actualQty = 5,
                        qty = 10,
                        unitFactor = 1,
                        unitName = "pcs",
                        productName = "Product A",
                        hhTimestamp = 0L,
                    ),
                    PurchaseInvoiceDraftItemModel(
                        barcode = "222",
                        actualQty = 3,
                        qty = 10,
                        unitFactor = 1,
                        unitName = "pcs",
                        productName = "Product B",
                        hhTimestamp = 0L,
                    ),
                )

            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "SUP1",
                    poNo = "PO1",
                    piNo = "PI1",
                    billNo = "B1",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "111",
                                actualQty = 0,
                                qty = 10,
                                productName = "Product A",
                                unitName = "pcs",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                            PurchaseInvoiceEntryUIModel(
                                barcode = "222",
                                actualQty = 0,
                                qty = 10,
                                productName = "Product B",
                                unitName = "pcs",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                        ),
                )
            every { purchaseInvoiceDetailLiveDataUseCase.updatedTimestamp.value } returns 0L

            val responseMock =
                PurchaseInvoiceDraftModel(
                    code = "",
                    ref = "",
                    message = "",
                    timestamp = "",
                    data =
                        PurchaseInvoiceDraftDataModel(
                            piNo = "PI1",
                            items = products,
                        ),
                )

            coEvery {
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI1",
                    docNo = "PO1",
                    flagPO = any(),
                    supplierCode = "SUP1",
                    billNo = "B1",
                    timestamp = 0L,
                    items = any(),
                )
            } returns flowOf(Result.Success(data = responseMock))

            // when
            val flow = saveDraftPurchaseInvoiceUseCase.executeList(products)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
            }
        }

    @Test
    fun test_executeList_when_not_refPo_should_use_purchaseOrder_entries_actualQty_greater_than_zero() =
        runTest {
            // given
            every { purchaseInvoiceUseCase.isRefPo() } returns false
            val products =
                listOf(
                    PurchaseInvoiceDraftItemModel(
                        barcode = "AAA",
                        actualQty = 100,
                        qty = 100,
                        unitFactor = 1,
                        unitName = "pcs",
                        productName = "Dummy",
                        hhTimestamp = 0L,
                    ),
                )

            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns
                PurchaseInvoiceDetailUIModel(
                    supplierName = "",
                    supplierCode = "SUP2",
                    poNo = "PO2",
                    piNo = "PI2",
                    billNo = "B2",
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "111",
                                actualQty = 0, // filtered out
                                qty = 10,
                                productName = "Product A",
                                unitName = "pcs",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                            PurchaseInvoiceEntryUIModel(
                                barcode = "222",
                                actualQty = 4, // included
                                qty = 10,
                                productName = "Product B",
                                unitName = "pcs",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                            PurchaseInvoiceEntryUIModel(
                                barcode = "333",
                                actualQty = 2, // included
                                qty = 10,
                                productName = "Product C",
                                unitName = "pcs",
                                unitFactor = 1,
                                salePrice = 0.0,
                            ),
                        ),
                )
            every { purchaseInvoiceDetailLiveDataUseCase.updatedTimestamp.value } returns 0L

            val responseMock =
                PurchaseInvoiceDraftModel(
                    code = "",
                    ref = "",
                    message = "",
                    timestamp = "",
                    data =
                        th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDraftDataModel(
                            piNo = "PI2",
                            items =
                                listOf(
                                    PurchaseInvoiceDraftItemModel(
                                        barcode = "222",
                                        actualQty = 4,
                                        qty = 10,
                                        unitFactor = 1,
                                        unitName = "pcs",
                                        productName = "Product B",
                                        hhTimestamp = 0L,
                                    ),
                                    PurchaseInvoiceDraftItemModel(
                                        barcode = "333",
                                        actualQty = 2,
                                        qty = 10,
                                        unitFactor = 1,
                                        unitName = "pcs",
                                        productName = "Product C",
                                        hhTimestamp = 0L,
                                    ),
                                ),
                        ),
                )

            coEvery {
                repository.saveDraftPurchaseInvoice(
                    piNo = "PI2",
                    docNo = "PO2",
                    flagPO = any(),
                    supplierCode = "SUP2",
                    billNo = "B2",
                    timestamp = 0L,
                    items = any(),
                )
            } returns flowOf(Result.Success(data = responseMock))

            // when
            val flow = saveDraftPurchaseInvoiceUseCase.executeList(products)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
            }
        }

    @Test
    fun test_executeList_failed_when_purchaseInvoiceDetail_null_expect_error() =
        runTest {
            // given
            every { purchaseInvoiceDetailLiveDataUseCase.purchaseInvoiceDetail.value } returns null
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "ไม่สามารถทำรายการได้"
            every { resourceUtils.getString(R.string.text_service_failed) } returns "ระบบไม่สามารถเชื่อมต่อได้"

            val products =
                listOf(
                    PurchaseInvoiceDraftItemModel(
                        barcode = "111",
                        actualQty = 1,
                        qty = 10,
                        unitFactor = 1,
                        unitName = "pcs",
                        productName = "Product A",
                        hhTimestamp = 0L,
                    ),
                )

            // when
            val flow = saveDraftPurchaseInvoiceUseCase.executeList(products)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorType.ERROR, error.type)
                assertEquals("ระบบไม่สามารถเชื่อมต่อได้", error.message)
            }
        }
}
