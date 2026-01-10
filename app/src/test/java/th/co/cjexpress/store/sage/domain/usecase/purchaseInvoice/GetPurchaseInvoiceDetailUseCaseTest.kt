package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorCodes.INTERNAL_SERVER_ERROR
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDetailModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException

class GetPurchaseInvoiceDetailUseCaseTest {
    private lateinit var useCase: GetPurchaseInvoiceDetailUseCase
    private lateinit var sageApiRepository: SageApiRepository
    private lateinit var resourceUtils: ResourceUtils

    private val piNo = "PI123"

    @Before
    fun setup() {
        sageApiRepository = mockk()
        resourceUtils = mockk()

        every { resourceUtils.getString(any()) } answers {
            when (val id = firstArg<Int>()) {
                R.string.text_unable_to_continue -> "Unable to continue"
                R.string.text_data_not_found -> "Data not found"
                R.string.text_because -> "Because: %s"
                R.string.text_because_service_connection_failed -> "Service connection failed"
                R.string.text_session_timeout -> "Session timeout"
                R.string.text_please_sign_in_again -> "Please sign in again"
                R.string.text_unauthorized -> "Unauthorized"
                else -> "mock string for id $id"
            }
        }

        every { resourceUtils.getString(any(), *anyVararg()) } answers {
            val id = firstArg<Int>()
            val formatArgs = secondArg<Array<Any>>()

            when (id) {
                R.string.text_document_no_purchase_invoice_pi -> {
                    val piNo = formatArgs.getOrNull(0)?.toString() ?: ""
                    "Data not found for purchase invoice: $piNo"
                }
                R.string.text_error_pi_status_is_not_draft -> {
                    val piNo = formatArgs.getOrNull(0)?.toString() ?: ""
                    "Status is not draft for purchase invoice: $piNo"
                }
                else -> "mock string for id $id with args ${formatArgs.joinToString()}"
            }
        }

        useCase = GetPurchaseInvoiceDetailUseCase(sageApiRepository, resourceUtils)
    }

    @Test
    fun `execute emits Success when repository returns Success`() =
        runTest {
            // Arrange
            val expectedModel = mockk<PurchaseInvoiceDetailModel>()
            coEvery { sageApiRepository.getPurchaseInvoiceDetail(piNo) } returns
                flowOf(
                    Result.Success(
                        expectedModel,
                    ),
                )

            // Act
            val results = useCase.execute(piNo).toList()

            // Assert
            assertEquals(1, results.size)
            val result = results[0]
            assertTrue(result is Result.Success)
            assertEquals(expectedModel, (result as Result.Success).data)
        }

    @Test
    fun `execute emits Error with BusinessException when NOT_FOUND`() =
        runTest {
            // Arrange
            val cause =
                BusinessException(
                    errorCode = ErrorCodes.NOT_FOUND,
                    type = ErrorType.WARNING,
                    title = "",
                    message = "",
                )
            coEvery { sageApiRepository.getPurchaseInvoiceDetail(piNo) } returns
                flowOf(
                    Result.Error(cause = cause),
                )

            // Act
            val results = useCase.execute(piNo).toList()

            // Assert
            val result = results[0]
            assertTrue(result is Result.Error)
            val error = (result as Result.Error).cause
            assertEquals(ErrorCodes.NOT_FOUND, error.errorCode)
            assertEquals("Data not found", error.title)
            assertEquals("Data not found for purchase invoice: $piNo", error.message)
        }

    @Test
    fun `execute emits Error with default handling when unknown error`() =
        runTest {
            coEvery { sageApiRepository.getPurchaseInvoiceDetail(piNo) } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "", // empty string
                                type = ErrorType.ERROR,
                                title = "",
                                message = "",
                            ),
                    ),
                )

            // Act
            val results = useCase.execute(piNo).toList()

            // Assert
            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(INTERNAL_SERVER_ERROR, cause.errorCode)
            assertEquals(ErrorType.ERROR, cause.type)
            assertEquals("Because: Service connection failed", cause.message)
        }
}
