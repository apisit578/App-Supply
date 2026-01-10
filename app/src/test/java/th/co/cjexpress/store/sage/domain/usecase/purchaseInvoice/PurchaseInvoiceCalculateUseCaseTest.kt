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
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.livedata.purchaseInvoice.PurchaseInvoiceDetailLiveData
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateDataModel
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCalculateModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class PurchaseInvoiceCalculateUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK
    lateinit var purchaseInvoiceDetailLiveData: PurchaseInvoiceDetailLiveData

    private lateinit var useCase: PurchaseInvoiceCalculateUseCase
    private lateinit var dtoModelMapper: DTOModelMapper

    // Mock data
    private val testPurchaseInvoiceDetailUIModel =
        PurchaseInvoiceDetailUIModel(
            supplierCode = "0000100917",
            supplierName = "Test Supplier",
            poNo = "TESTB112233445",
            piNo = "PI2506M04C-000031",
            billNo = "BILL123",
            entries =
                mutableStateListOf(
                    PurchaseInvoiceEntryUIModel(
                        barcode = "8851123237048",
                        actualQty = 10,
                        qty = 5,
                        productName = "Test Product",
                        unitName = "ST",
                        unitFactor = 1,
                        salePrice = 100.0,
                    ),
                ),
        )

    override fun setUp() {
        super.setUp()
        val dataBarcode = testPurchaseInvoiceDetailUIModel
        every { purchaseInvoiceDetailLiveData.purchaseInvoiceDetailUIModel.value } returns dataBarcode

        dtoModelMapper = DTOModelMapper()
        useCase = PurchaseInvoiceCalculateUseCase(repository, purchaseInvoiceDetailLiveData, dtoModelMapper, resourceUtils)
    }

    @Test
    fun test_purchaseInvoiceCalculate_success_expect_result_success() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceDetailUIModel(
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                                productName = "Test Product",
                                unitName = "ST",
                                unitFactor = 1,
                                salePrice = 100.0,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    billNo = "BILL123",
                    supplierCode = "0000100917",
                    supplierName = "Test Supplier",
                )

            val mockData =
                PurchaseInvoiceCalculateModel(
                    code = "CODE1",
                    ref = "REF1",
                    message = "Success",
                    data =
                        PurchaseInvoiceCalculateDataModel(
                            piNo = dataBarcode.piNo,
                            poNo = dataBarcode.poNo,
                            supplierCode = dataBarcode.supplierCode,
                            itemCount = 1,
                            receivedItemCount = 1,
                            orderedTotalAmount = 500.0,
                            receivedTotalAmount = 450.0,
                            totalReceivedQty = 10,
                            totalOrderedQty = 10,
                        ),
                    timestamp = "2025-07-09T00:00:00Z",
                )

            val mapperData = dtoModelMapper.purchaseInvoiceCalculateDTOMapper.toDTO(dataBarcode)

            coEvery { repository.purchaseInvoiceCalculate(mapperData) } returns flowOf(Result.Success(mockData))

            // when
            val resultFlow = useCase.execute()

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Success)
                val successData = (result as Result.Success).data

                assertEquals("CODE1", successData.code)
                assertEquals("REF1", successData.ref)
                assertEquals("Success", successData.message)
                assertEquals(dataBarcode.piNo, successData.data.piNo)
                assertEquals(dataBarcode.poNo, successData.data.poNo)
                assertEquals(dataBarcode.supplierCode, successData.data.supplierCode)
                assertEquals(1, successData.data.itemCount)
                assertEquals(1, successData.data.receivedItemCount)
                assertEquals(500.0, successData.data.orderedTotalAmount, 0.001)
                assertEquals(450.0, successData.data.receivedTotalAmount, 0.001)
                assertEquals(10, successData.data.totalReceivedQty)
                assertEquals(10, successData.data.totalOrderedQty)
                assertEquals("2025-07-09T00:00:00Z", successData.timestamp)
            }
        }

    @Test
    fun test_purchaseInvoiceCalculate_generic_error_expect_result_error() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceDetailUIModel(
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                                productName = "Test Product",
                                unitName = "ST",
                                unitFactor = 1,
                                salePrice = 100.0,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    billNo = "BILL123",
                    supplierCode = "0000100917",
                    supplierName = "Test Supplier",
                )
            val mapperData = dtoModelMapper.purchaseInvoiceCalculateDTOMapper.toDTO(dataBarcode)

            coEvery { repository.purchaseInvoiceCalculate(mapperData) } returns
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
            val resultFlow = useCase.execute()

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_purchaseInvoiceCalculate_not_found_error_expect_400() =
        runTest {
            // given
            val dataBarcode =
                PurchaseInvoiceDetailUIModel(
                    entries =
                        mutableStateListOf(
                            PurchaseInvoiceEntryUIModel(
                                barcode = "8851123237048",
                                actualQty = 10,
                                qty = 5,
                                productName = "Test Product",
                                unitName = "ST",
                                unitFactor = 1,
                                salePrice = 100.0,
                            ),
                        ),
                    piNo = "PI2506M04C-000031",
                    poNo = "TESTB112233445",
                    billNo = "BILL123",
                    supplierCode = "0000100917",
                    supplierName = "Test Supplier",
                )
            val expectedException =
                BusinessException(
                    errorCode = ErrorCodes.BAD_REQUEST,
                    type = ErrorType.ERROR,
                    title = "",
                    message = "",
                )
            val mapperData = dtoModelMapper.purchaseInvoiceCalculateDTOMapper.toDTO(dataBarcode)
            coEvery {
                repository.purchaseInvoiceCalculate(mapperData)
            } returns flowOf(Result.Error(cause = expectedException))

            // when
            val resultFlow = useCase.execute()

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error

                assertEquals(ErrorType.ERROR, errorResult.cause?.type)
                assertEquals(ErrorCodes.BAD_REQUEST, errorResult.cause?.errorCode)
            }
        }
}
