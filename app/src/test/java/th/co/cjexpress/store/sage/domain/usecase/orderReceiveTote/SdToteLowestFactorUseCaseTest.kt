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
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteLowestFactorMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteLowestFactorUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var sdToteLowestFactorUseCase: SdToteLowestFactorUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sdToteLowestFactorUseCase = SdToteLowestFactorUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_sdToteLowestFactor_success_expect_result_success() =
        runTest {
            // given
            val mockModel = mockk<SdToteLowestFactorMappingModel>()
            every { mockModel.data?.barcode } returns "1234"
            coEvery {
                repository.sdToteLowestFactor(
                    "123345",
                    "1234",
                )
            } returns flowOf(Result.Success(data = mockModel))
            // when
            val flow =
                sdToteLowestFactorUseCase.execute(
                    sdNo = "123345",
                    itemBarcode = "1234",
                    barcode = "1234",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_sdToteLowestFactor_failure_expect_result_error() =
        runTest {
            // given
            coEvery {
                repository.sdToteLowestFactor(
                    "123345",
                    "1234",
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
                sdToteLowestFactorUseCase.execute(
                    sdNo = "123345",
                    itemBarcode = "1234",
                    barcode = "1234",
                )
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_sdToteLowestFactor_with_blank_toteCode_expect_error() =
        runTest {
            // given
            every { resourceUtils.getString(any()) } returns "Error"

            // when
            val flow =
                sdToteLowestFactorUseCase.execute(
                    sdNo = "123345",
                    itemBarcode = "1234",
                    barcode = "",
                )

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals("Error", errorResult.cause?.message)
                assertEquals(ErrorType.WARNING, errorResult.cause?.type)
            }
        }

    @Test
    fun test_sdToteLowestFactor_error_code_40004_expect_error() =
        runTest {
            every { resourceUtils.getString(any()) } returns "Error"

            val cause =
                BusinessException(
                    errorCode = "40004",
                    type = ErrorType.ERROR,
                    message = "",
                )
            coEvery { repository.sdToteLowestFactor("123345", "1234") } returns
                flowOf(
                    Result.Error(
                        cause = cause,
                    ),
                )

            val flow =
                sdToteLowestFactorUseCase.execute(
                    sdNo = "123345",
                    itemBarcode = "1234",
                    barcode = "1234",
                )

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals("Error", errorResult.cause?.message)
                assertEquals(ErrorType.WARNING, errorResult.cause?.type)
            }
        }

    @Test
    fun test_sdToteLowestFactor_should_return_error_when_barcode_does_not_match() =
        runTest {
            every { resourceUtils.getString(any()) } returns "Error"

            // given
            val mockModel = mockk<SdToteLowestFactorMappingModel>()
            every { mockModel.data?.barcode } returns "5678"
            coEvery { repository.sdToteLowestFactor("123345", "1234") } returns
                flowOf(
                    Result.Success(
                        data = mockModel,
                    ),
                )
            // when
            val flow =
                sdToteLowestFactorUseCase.execute(
                    sdNo = "123345",
                    itemBarcode = "5678",
                    barcode = "1234",
                )

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals(ErrorType.WARNING, errorResult.cause?.type)
            }
        }
}
