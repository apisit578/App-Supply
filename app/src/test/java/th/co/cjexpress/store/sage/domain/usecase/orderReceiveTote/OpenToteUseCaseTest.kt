package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertTrue
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class OpenToteUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var openToteUseCase: OpenToteUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        openToteUseCase = OpenToteUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_openTote_success_expect_result_success() =
        runTest {
            // given
            val mockModel = mockk<SdToteDetailMappingModel>()
            coEvery { repository.openTote("T123345") } returns flowOf(Result.Success(data = mockModel))
            // when
            val flow = openToteUseCase.execute(toteCode = "T123345")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_openTote_failure_expect_result_error() =
        runTest {
            // given
            coEvery { repository.openTote("T123345") } returns
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
            val flow = openToteUseCase.execute(toteCode = "T123345")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_openTote_with_blank_toteCode_expect_error_please_scan_tote() =
        runTest {
            // given
            every { resourceUtils.getString(any()) } returns "Error"

            // when
            val flow = openToteUseCase.execute(toteCode = "")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals("Error", errorResult.cause.message)
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
            }
        }

    @Test
    fun test_openTote_error_code_40000_expect_warning_message() =
        runTest {
            val toteCode = "T123345"
            val cause =
                BusinessException(
                    errorCode = "40000",
                    type = ErrorType.ERROR,
                    message = "Error",
                )
            every {
                String.format(
                    resourceUtils.getString(R.string.text_error_tote_not_found),
                    toteCode,
                )
            } returns "เนื่องจากไม่พบเลข Tote: $toteCode กรุณาตรวจสอบใบยืนยันการรับ สินค้า หรือ ลองใหม่อีกครั้ง"

            coEvery { repository.openTote(toteCode) } returns flowOf(Result.Error(cause = cause))

            val flow = openToteUseCase.execute(toteCode = toteCode)

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals(
                    "เนื่องจากไม่พบเลข Tote: $toteCode กรุณาตรวจสอบใบยืนยันการรับ สินค้า หรือ ลองใหม่อีกครั้ง",
                    errorResult.cause.message,
                )
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
            }
        }

    @Test
    fun test_openTote_error_code_invalid_operation_expect_warning_message() =
        runTest {
            val toteCode = "T123345"
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_OPERATION,
                    type = ErrorType.ERROR,
                    message = "Error",
                )
            every {
                String.format(
                    resourceUtils.getString(R.string.text_error_tote_not_found),
                    toteCode,
                )
            } returns "เนื่องจากไม่พบเลข Tote: $toteCode กรุณาตรวจสอบใบยืนยันการรับ สินค้า หรือ ลองใหม่อีกครั้ง"

            coEvery { repository.openTote(toteCode) } returns flowOf(Result.Error(cause = cause))

            val flow = openToteUseCase.execute(toteCode = toteCode)

            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals(
                    "เนื่องจากไม่พบเลข Tote: $toteCode กรุณาตรวจสอบใบยืนยันการรับ สินค้า หรือ ลองใหม่อีกครั้ง",
                    errorResult.cause.message,
                )
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
            }
        }
}
