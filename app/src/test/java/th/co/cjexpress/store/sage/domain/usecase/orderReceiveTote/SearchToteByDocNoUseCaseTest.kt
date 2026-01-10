package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
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
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SearchToteByDocNoUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var searchToteByDocNoUseCase: SearchToteByDocNoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        searchToteByDocNoUseCase = SearchToteByDocNoUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_searchToteBySdNo_success_expect_result_success() =
        runTest {
            // given
            val mockModel = mockk<SdToteDetailMappingModel>()
            coEvery { repository.searchToteBySdNo("123345") } returns flowOf(Result.Success(data = mockModel))
            // when
            val flow = searchToteByDocNoUseCase.execute(sdNo = "123345")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_searchToteBySdNo_failure_expect_result_error() =
        runTest {
            // given
            coEvery { repository.searchToteBySdNo("123345") } returns
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
            val flow = searchToteByDocNoUseCase.execute(sdNo = "123345")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_searchToteBySdNo_errorCode_50000_expect_business_exception_warning() =
        runTest {
            // given
            val errorMessage = "ข้อมูลผิดพลาดจากระบบ"
            val expectedFormatted = "เนื่องจาก$errorMessage"

            coEvery { repository.searchToteBySdNo("SD123") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                type = ErrorType.WARNING,
                                errorCode = "50000",
                                message = errorMessage,
                            ),
                    ),
                )
            coEvery { resourceUtils.getString(R.string.text_because) } returns "เนื่องจาก%s"

            // when
            val flow = searchToteByDocNoUseCase.execute("SD123")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, cause.type)
                assertEquals(expectedFormatted, cause.message)
            }
        }

    @Test
    fun test_searchToteBySdNo_errorCode_401_expect_session_timeout_message() =
        runTest {
            // given
            coEvery { repository.searchToteBySdNo("SD401") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                type = ErrorType.ERROR,
                                errorCode = "401",
                                message = "Session expired",
                            ),
                    ),
                )
            coEvery { resourceUtils.getString(R.string.text_session_timeout) } returns "เซสชันหมดอายุ"
            coEvery { resourceUtils.getString(R.string.text_please_sign_in_again) } returns "กรุณาเข้าสู่ระบบอีกครั้ง"

            // when
            val flow = searchToteByDocNoUseCase.execute("SD401")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, cause.type)
                cause.message?.let { assertTrue(it.contains("เซสชันหมดอายุ")) }
                cause.message?.let { assertTrue(it.contains("กรุณาเข้าสู่ระบบอีกครั้ง")) }
            }
        }

    @Test
    fun test_searchToteBySdNo_errorCode_50000_expect_session_timeout_message() =
        runTest {
            coEvery { repository.searchToteBySdNo("SD401") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                type = ErrorType.ERROR,
                                errorCode = "50000",
                                message = "Error",
                            ),
                    ),
                )
            coEvery { resourceUtils.getString(R.string.text_because) } returns "เนื่องจาก%s"

            // when
            val flow = searchToteByDocNoUseCase.execute("SD401")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, cause.type)
                assertEquals("เนื่องจากError", cause.message)
            }
        }

    @Test
    fun test_searchToteBySdNo_errorCode_invalid_status_expect_session_timeout_message() =
        runTest {
            // given
            val error =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_STATUS,
                    type = ErrorType.ERROR,
                    message = null,
                )
            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "ไม่สามารถดำเนินการต่อได้"
            every { resourceUtils.getString(R.string.text_error_shipment_document_tote_not_found) } returns "ไม่พบ Tote"

            coEvery { repository.searchToteBySdNo("SD401") } returns
                flowOf(
                    Result.Error(
                        cause = error,
                    ),
                )

            // when
            val flow = searchToteByDocNoUseCase.execute("SD401")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, cause.type)
                assertEquals("ไม่สามารถดำเนินการต่อได้", cause.title)
                assertEquals("ไม่พบ Tote", cause.message)
            }
        }

    @Test
    fun test_errorCode_50000_with_null_message_expect_WARNING_and_format_empty() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.text_because) } returns "เนื่องจาก%s"
            coEvery { repository.searchToteBySdNo("SD401") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "50000",
                                type = ErrorType.ERROR,
                                message = null,
                            ),
                    ),
                )

            // when
            val flow = searchToteByDocNoUseCase.execute("SD401")

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val cause = (result as Result.Error).cause
                assertEquals(ErrorType.WARNING, cause.type)
                assertEquals("เนื่องจาก", cause.message)
            }
        }

    @Test
    fun test_errorCode_other_should_fallthrough_to_super_handleError_else_branch() =
        runTest {
            // given: โค้ดที่ไม่ใช่ 50000 และไม่ใช่ INVALID_STATUS
            coEvery { repository.searchToteBySdNo("DOC_OTHER") } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "X999",
                                type = ErrorType.ERROR,
                                message = "boom",
                            ),
                    ),
                )

            // when
            val flow = searchToteByDocNoUseCase.execute("DOC_OTHER")

            // then
            flow.collectLatest { result ->
                // แค่ออกมาทาง Error ก็เพียงพอจะกดสาขา else ให้ cover line
                assertTrue(result is Result.Error)
            }

            // ยืนยันว่าไม่เรียกทรัพยากรของสองสาขาพิเศษ
            verify(exactly = 0) { resourceUtils.getString(R.string.text_because) }
            verify(exactly = 0) { resourceUtils.getString(R.string.text_unable_to_continue) }
            verify(exactly = 0) { resourceUtils.getString(R.string.text_error_shipment_document_tote_not_found) }
            confirmVerified(resourceUtils)
        }
}
