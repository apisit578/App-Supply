package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailItem
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteSubmitAndGetLatestBarcodeUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var sdToteSubmitAndGetLatestBarcodeUseCase: SdToteSubmitAndGetLatestBarcodeUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sdToteSubmitAndGetLatestBarcodeUseCase =
            SdToteSubmitAndGetLatestBarcodeUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_sdToteSubmit_failure_expect_result_error() =
        runTest {
            // given
            coEvery {
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )
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
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteSubmit_and_searchToteBySdNo_success_expect_result_success() =
        runTest {
            // given
            val mockSdToteSubmitModel = mockk<SdToteSubmitMappingModel>(relaxed = true)
            val mockSdToteDetailModel = mockk<SdToteDetailMappingModel>(relaxed = true)
            val mockSdToteDetailItem = mockk<SdToteDetailItem>(relaxed = true)
            coEvery {
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )
            } returns flowOf(Result.Success(data = mockSdToteSubmitModel))
            every { mockSdToteDetailItem.barcode } returns "barcode"
            every { mockSdToteDetailModel.data.items } returns mutableListOf(mockSdToteDetailItem)
            coEvery { repository.searchToteBySdNo(sdNo = "sdNo") } returns
                flowOf(
                    Result.Success(
                        data = mockSdToteDetailModel,
                    ),
                )
            // when
            val flow =
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
            }
        }

    @Test
    fun test_sdToteSubmit_success_but_searchToteBySdNo_failure_expect_result_error() =
        runTest {
            // given
            val mockSdToteSubmitModel = mockk<SdToteSubmitMappingModel>(relaxed = true)
            coEvery {
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )
            } returns flowOf(Result.Success(data = mockSdToteSubmitModel))
            coEvery { repository.searchToteBySdNo(sdNo = "sdNo") } returns
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
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteSubmit_and_searchToteBySdNo_success_but_barcode_mismatch_expect_result_error() =
        runTest {
            // given
            val mockSdToteSubmitModel = mockk<SdToteSubmitMappingModel>(relaxed = true)
            val mockSdToteDetailModel = mockk<SdToteDetailMappingModel>(relaxed = true)
            val mockSdToteDetailItem = mockk<SdToteDetailItem>(relaxed = true)

            coEvery {
                repository.sdToteSubmit(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )
            } returns flowOf(Result.Success(data = mockSdToteSubmitModel))

            every { mockSdToteDetailItem.barcode } returns "wrongBarcode"
            every { mockSdToteDetailModel.data.items } returns mutableListOf(mockSdToteDetailItem)
            coEvery { repository.searchToteBySdNo(sdNo = "sdNo") } returns
                flowOf(
                    Result.Success(
                        data = mockSdToteDetailModel,
                    ),
                )

            val flow =
                sdToteSubmitAndGetLatestBarcodeUseCase.execute(
                    sdNo = "sdNo",
                    toteCode = "toteCode",
                    barcode = "barcode",
                    discountType = "discountType",
                    actualQty = 1,
                    skuCode = "skuCode",
                    expList = emptyList(),
                )

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }
}
