package th.co.cjexpress.store.sage.domain.usecase.orderReceive

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
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetShipmentDocumentBySdNoUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var getShipmentDocumentBySdNoUseCase: GetShipmentDocumentBySdNoUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getShipmentDocumentBySdNoUseCase =
            GetShipmentDocumentBySdNoUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_getShipmentDocumentBySdNo_success_expect_result_success() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            val mockModel = mockk<ShipmentDocumentModel>()
            coEvery { repository.getShipmentDocumentBySDNo(sdNo) } returns
                flowOf(
                    Result.Success(
                        data = mockModel,
                    ),
                )
            // when
            val flow = getShipmentDocumentBySdNoUseCase.execute(sdNo = sdNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_failure_expect_result_error() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            coEvery { repository.getShipmentDocumentBySDNo(sdNo) } returns
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
            val flow = getShipmentDocumentBySdNoUseCase.execute(sdNo = sdNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_with_blank_toteCode_expect_error_please_scan_tote() =
        runTest {
            // given
            every { resourceUtils.getString(any()) } returns "Error"
            // when
            val flow = getShipmentDocumentBySdNoUseCase.execute(sdNo = "")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals("Error", errorResult.cause.message)
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_error_code_not_found_expect_warning_message() =
        runTest {
            // given
            val sdNo = "SD2505M04C-000000"
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.NOT_FOUND,
                    type = ErrorType.ERROR,
                    message = "Message Error",
                )
            coEvery { repository.getShipmentDocumentBySDNo(sdNo) } returns flowOf(Result.Error(cause = cause))

            // strings ที่ use case ใช้จริง
            every { resourceUtils.getString(R.string.text_data_not_found) } returns "Data not found"
            every { resourceUtils.getString(R.string.text_format_shipment_document_no) } returns "SD No: %s"

            // when
            val flow = getShipmentDocumentBySdNoUseCase.execute(sdNo = sdNo)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val err = (result as Result.Error).cause as BusinessException
                assertEquals(ErrorCodes.NOT_FOUND, err.errorCode)
                assertEquals(ErrorType.WARNING, err.type)
                assertEquals("Data not found", err.title)
                assertEquals(String.format("SD No: %s", sdNo), err.message)
            }
        }

    @Test
    fun test_getShipmentDocumentBySdNo_error_code_invalid_status_expect_warning_message() =
        runTest {
            val sdNo = ""
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_STATUS,
                    type = ErrorType.ERROR,
                    message = "Message Error",
                )
            coEvery { repository.getShipmentDocumentBySDNo(sdNo) } returns flowOf(Result.Error(cause = cause))

            every { resourceUtils.getString(R.string.text_data_not_found) } returns "Data not found"
            every { resourceUtils.getString(R.string.text_format_shipment_document_no) } returns "SD No: %s"

            // when
            val flow = getShipmentDocumentBySdNoUseCase.execute(sdNo = sdNo)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val err = (result as Result.Error).cause as BusinessException
                assertEquals(ErrorCodes.INVALID_STATUS, err.errorCode)
                assertEquals(ErrorType.WARNING, err.type)
                assertEquals("Data not found", err.title)
                assertEquals(String.format("SD No: %s", sdNo), err.message)
            }
        }
}
