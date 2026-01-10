package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertNotNull
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

class ShipmentDocumentOpenUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var shipmentDocumentOpenUseCase: ShipmentDocumentOpenUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        shipmentDocumentOpenUseCase = ShipmentDocumentOpenUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_shipmentDocumentOpen_success_expect_result_success() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val mockModel = mockk<ShipmentDocumentModel>()
            coEvery { repository.shipmentDocumentOpen(ldNo) } returns flowOf(Result.Success(data = mockModel))
            // when
            val flow = shipmentDocumentOpenUseCase.execute(ldNo = ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_failure_expect_result_error() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            coEvery { repository.shipmentDocumentOpen(ldNo) } returns
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
            val flow = shipmentDocumentOpenUseCase.execute(ldNo = ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_error_code_not_found_expect_warning_message() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.NOT_FOUND,
                    type = ErrorType.ERROR,
                    message = "Error",
                )
            coEvery { repository.shipmentDocumentOpen(ldNo = ldNo) } returns
                flowOf(
                    Result.Error(
                        cause = cause,
                    ),
                )
            // when
            val flow = shipmentDocumentOpenUseCase.execute(ldNo = ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error
                assertEquals(ErrorCodes.NOT_FOUND, errorResult.cause.errorCode)
                assertEquals(ErrorType.WARNING, errorResult.cause.type)
                assertEquals(
                    resourceUtils.getString(R.string.text_data_not_found),
                    errorResult.cause.title,
                )
                assertEquals(
                    resourceUtils.getString(R.string.text_format_shipment_document_no),
                    errorResult.cause.message,
                )
            }
        }

    @Test
    fun test_shipmentDocumentOpen_ldNo_blank_expect_business_exception() =
        runTest {
            // given
            val ldNo = " "
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.INVALID_PARAMETER,
                    type = ErrorType.WARNING,
                    message = resourceUtils.getString(R.string.text_please_fill_or_scan_ld),
                )
            coEvery { repository.shipmentDocumentOpen(ldNo = ldNo) } returns
                flowOf(
                    Result.Error(
                        cause = cause,
                    ),
                )
            // when
            val flow = shipmentDocumentOpenUseCase.execute(ldNo = ldNo)
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val error = result as Result.Error
                val resultCause = error.cause
                assertNotNull(cause)
                assertEquals(cause.errorCode, resultCause.errorCode)
                assertEquals(cause.type, resultCause.type)
                assertEquals(cause.message, resultCause.message)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_error_code_not_found_expect_warning() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.NOT_FOUND,
                    type = ErrorType.ERROR,
                    message = "raw",
                )
            coEvery { repository.shipmentDocumentOpen(ldNo) } returns flowOf(Result.Error(cause = cause))

            // when
            val flow = shipmentDocumentOpenUseCase.execute(ldNo = ldNo)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val err = (result as Result.Error).cause
                assertEquals(ErrorCodes.NOT_FOUND, err.errorCode)
                assertEquals(ErrorType.WARNING, err.type)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_error_code_doc_already_approved_expect_warning() =
        runTest {
            // given
            val ldNo = "LD25052790000001"
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.DOC_ALREADY_APPROVED,
                    type = ErrorType.ERROR,
                    message = "raw",
                )
            coEvery { repository.shipmentDocumentOpen(ldNo) } returns flowOf(Result.Error(cause = cause))

            // when
            val flow = shipmentDocumentOpenUseCase.execute(ldNo = ldNo)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val err = (result as Result.Error).cause
                assertEquals(ErrorCodes.DOC_ALREADY_APPROVED, err.errorCode)
                assertEquals(ErrorType.WARNING, err.type)
            }
        }

    @Test
    fun test_shipmentDocumentOpen_ldNo_blank_expect_invalid_parameter_warning() =
        runTest {
            // given
            val ldNo = ""
            // when
            val flow = shipmentDocumentOpenUseCase.execute(ldNo = ldNo)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val err = (result as Result.Error).cause
                assertEquals(ErrorCodes.INVALID_PARAMETER, err.errorCode)
                assertEquals(ErrorType.WARNING, err.type)
            }
        }
}
