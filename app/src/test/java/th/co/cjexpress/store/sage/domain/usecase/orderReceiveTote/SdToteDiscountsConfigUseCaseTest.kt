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
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigMappingModel
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDiscountsConfigModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteDiscountsConfigUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var sdToteDiscountsConfigUseCase: SdToteDiscountsConfigUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sdToteDiscountsConfigUseCase = SdToteDiscountsConfigUseCase(repository, resourceUtils)
        setupResourceStrings()
    }

    private fun setupResourceStrings() {
        every { resourceUtils.getString(R.string.text_title_barcode_mismatch) } returns "Barcode mismatch"
        every { resourceUtils.getString(R.string.text_please_check_and_scan_again) } returns "Check And Scan Again"
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_sdToteDiscountsConfig_success_expect_result_success() =
        runTest {
            val mockModel = mockk<SdToteDiscountsConfigMappingModel>()

            val mockDiscountConfigModel =
                SdToteDiscountsConfigModel(
                    barcode = "1234",
                    discountType = "discountType1",
                    noOfExp = 2,
                    skuName = "Test Product",
                    unitName = "Test Unit",
                    skuCode = "SKU1234",
                )

            every { mockModel.data } returns listOf(mockDiscountConfigModel)

            coEvery { repository.sdToteDiscountsConfig("1234") } returns flowOf(Result.Success(data = mockModel))

            val flow = sdToteDiscountsConfigUseCase.execute("1234")

            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockDiscountConfigModel), result)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_failure_expect_result_error() =
        runTest {
            coEvery { repository.sdToteDiscountsConfig("1234") } returns
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

            val flow = sdToteDiscountsConfigUseCase.execute("1234")

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_with_blank_barCode_expect_error() =
        runTest {
            // given
            every { resourceUtils.getString(any()) } returns "Error"

            val flow = sdToteDiscountsConfigUseCase.execute("")

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals("Error", errorResult.cause.message)
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_error_code_invalid_parameter_expect_error() =
        runTest {
            every { resourceUtils.getString(any()) } returns "Error"

            val cause =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_PARAMETER,
                    type = ErrorType.ERROR,
                    message = "",
                )

            coEvery { repository.sdToteDiscountsConfig("1234") } returns flowOf(Result.Error(cause = cause))

            val flow = sdToteDiscountsConfigUseCase.execute("1234")

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
            }
        }

    @Test
    fun `execute returns error when barcode mismatch is invalid`() =
        runTest {
            // given
            every { resourceUtils.getString(any()) } returns "Error"

            // when
            val flow =
                sdToteDiscountsConfigUseCase.execute("1234")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, error.type)
                assertEquals("Barcode mismatch", error.title)
                assertEquals("Check And Scan Again", error.message)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_should_return_error_when_barcode_does_not_match() =
        runTest {
            every { resourceUtils.getString(any()) } returns "Error"

            val mockModel = mockk<SdToteDiscountsConfigMappingModel>()

            val mockDiscountConfigModel =
                SdToteDiscountsConfigModel(
                    barcode = "5678",
                    discountType = "discountType1",
                    noOfExp = 2,
                    skuName = "Test Product",
                    unitName = "Test Unit",
                    skuCode = "SKU5678",
                )

            every { mockModel.data } returns listOf(mockDiscountConfigModel)

            coEvery { repository.sdToteDiscountsConfig("1234") } returns flowOf(Result.Success(data = mockModel))

            val flow = sdToteDiscountsConfigUseCase.execute("1234")

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
            }
        }

    @Test
    fun test_sdToteDiscountsConfig_should_return_error_not_found() =
        runTest {
            coEvery { repository.sdToteDiscountsConfig("1234") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )
            coEvery { resourceUtils.getString(any()) } returns "ไม่พบข้อมูล"

            val flow = sdToteDiscountsConfigUseCase.execute("1234")

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, error.type)
                assertEquals("ไม่พบข้อมูล", error.message)
                assertEquals(ErrorCodes.NOT_FOUND, error.errorCode)
            }
        }
}
