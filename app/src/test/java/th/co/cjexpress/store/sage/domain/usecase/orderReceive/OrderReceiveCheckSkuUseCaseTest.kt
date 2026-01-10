package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.BarcodeDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuBarcodeDataModel
import th.co.cjexpress.store.sage.domain.model.orderReceive.SkuCheckModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class OrderReceiveCheckSkuUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var shipmentDocumentUseCase: ShipmentDocumentUseCase

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var orderReceiveCheckSkuUseCase: OrderReceiveCheckSkuUseCase

    override fun setUp() {
        super.setUp()
        orderReceiveCheckSkuUseCase =
            OrderReceiveCheckSkuUseCase(
                repository = repository,
                resourceUtils = resourceUtils,
            )
    }

    @Test
    fun test_skuCheck_success_expect_result_success() =
        runTest {
            // given
            val mockModel =
                SkuCheckModel(
                    code = "",
                    message = "",
                    data =
                        SkuBarcodeDataModel(
                            skuName = "Test Product",
                            barcodes =
                                listOf(
                                    BarcodeDataModel(
                                        barcode = "1234",
                                        barcodeFactor = 1,
                                        unitName = "pcs",
                                        unitCode = "",
                                    ),
                                ),
                        ),
                )
            coEvery { repository.skuCheck(any()) } returns flowOf(Result.Success(data = mockModel))

            // when
            val flow = orderReceiveCheckSkuUseCase.execute("1234")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
            }
        }

    @Test
    fun test_skuCheck_success_when_barcode_list_empty_expect_result_error() =
        runTest {
            // given
            val mockModel =
                SkuCheckModel(
                    code = "",
                    message = "",
                    data =
                        SkuBarcodeDataModel(
                            skuName = "Test Product",
                            barcodes = listOf(),
                        ),
                )
            coEvery { repository.skuCheck(any()) } returns flowOf(Result.Success(data = mockModel))
            every { shipmentDocumentUseCase.searchShipmentBatchData(any()) } returns null

            // when
            val flow = orderReceiveCheckSkuUseCase.execute("1234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_skuCheck_failed_when_error_code_NOT_FOUND_expect_warning_mapped_error() =
        runTest {
            // given
            coEvery { repository.skuCheck("1234") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.ERROR,
                                title = "Title Error",
                                message = "Message Error",
                            ),
                    ),
                )
            every { resourceUtils.getString(R.string.text_title_barcode_mismatch) } returns "Barcode mismatch"
            every { resourceUtils.getString(R.string.text_please_check_and_scan_again) } returns "Please check and scan again"

            // when
            val flow = orderReceiveCheckSkuUseCase.execute("1234")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                (result as Result.Error).cause as BusinessException
            }
        }

    @Test
    fun test_skuCheck_failed_expect_result_error() =
        runTest {
            // given
            coEvery { repository.skuCheck(any()) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "",
                                type = ErrorType.ERROR,
                                title = "",
                                message = "",
                            ),
                    ),
                )
            // when
            val flow = orderReceiveCheckSkuUseCase.execute("1234")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_skuCheck_failed_when_error_code_ERR_NOT_FOUND_expect_result_error() =
        runTest {
            // given
            coEvery { repository.skuCheck(any()) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "ERR_NOT_FOUND",
                                type = ErrorType.ERROR,
                                title = "Barcode Mismatch",
                                message = "Please check and scan again",
                            ),
                    ),
                )

            // when
            val flow = orderReceiveCheckSkuUseCase.execute("1234")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_skuCheck_failed_when_error_code_INVALID_PARAMETER_expect_warning_mapped_error() =
        runTest {
            // given
            coEvery { repository.skuCheck("1234") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.INVALID_PARAMETER,
                                type = ErrorType.ERROR,
                                title = "Title Error",
                                message = "Message Error",
                            ),
                    ),
                )
            every { resourceUtils.getString(R.string.text_title_barcode_mismatch) } returns "Barcode mismatch"
            every { resourceUtils.getString(R.string.text_please_check_and_scan_again) } returns "Please check and scan again"

            // when
            val flow = orderReceiveCheckSkuUseCase.execute("1234")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                (result as Result.Error).cause as BusinessException
            }
        }
}
