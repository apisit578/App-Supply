package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorCodes.INTERNAL_SERVER_ERROR
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDetailModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

@ExperimentalCoroutinesApi
class CheckPurchaseOrderExistsUseCaseTest : BaseUnitTest() {
    private val sageApiRepository = mockk<SageApiRepository>()
    private val resourceUtils = mockk<ResourceUtils>(relaxed = true)

    private lateinit var useCase: CheckPurchaseOrderExistsUseCase

    override fun setUp() {
        super.setUp()
        useCase = CheckPurchaseOrderExistsUseCase(sageApiRepository, resourceUtils)
    }

    @Test
    fun `execute returns Success true when repository returns Success`() =
        runTest {
            val docNo = "DOC123"
            val supplierCode = "SUP001"
            val data = mockk<PurchaseOrderDetailModel>(relaxed = true)
            val successFlow = flowOf(Result.Success(data))

            coEvery {
                sageApiRepository.getPurchaseOrderDetailByDocNo(docNo, supplierCode)
            } returns successFlow

            val results = mutableListOf<Result<Boolean>>()

            useCase.execute(docNo, supplierCode).toList(results)

            assertEquals(1, results.size)
            assertTrue(results[0] is Result.Success)
            assertEquals(true, (results[0] as Result.Success).data)
        }

    @Test
    fun `execute emits Error with NOT_FOUND BusinessException with formatted message`() =
        runTest {
            val docNo = "DOC123"
            val supplierCode = "SUP001"

            // Mock repository ให้ emit Error ที่มี cause errorCode = NOT_FOUND
            coEvery {
                sageApiRepository.getPurchaseOrderDetailByDocNo(docNo, supplierCode)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.NOT_FOUND,
                                type = ErrorType.WARNING,
                                title = "",
                                message = "",
                            ),
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_error_po_not_found) } returns "PO not found: %s"

            val results = mutableListOf<Result<Boolean>>()
            useCase = CheckPurchaseOrderExistsUseCase(sageApiRepository, resourceUtils)

            useCase.execute(docNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorCodes.NOT_FOUND, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("PO not found: $docNo", cause.message)
        }

    @Test
    fun `execute emits Error with SUP_INACTIVE BusinessException with formatted message`() =
        runTest {
            val docNo = "DOC123"
            val supplierCode = "SUP001"

            coEvery {
                sageApiRepository.getPurchaseOrderDetailByDocNo(docNo, supplierCode)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.SUPPLIER_INACTIVE,
                                type = ErrorType.WARNING,
                                title = "",
                                message = "",
                            ),
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_error_supplier_disabled) } returns "Supplier disabled: %s"

            val results = mutableListOf<Result<Boolean>>()
            useCase = CheckPurchaseOrderExistsUseCase(sageApiRepository, resourceUtils)

            useCase.execute(docNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorCodes.SUPPLIER_INACTIVE, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("Supplier disabled: $supplierCode", cause.message)
        }

    @Test
    fun `execute emits Error with SUP_NOT_FOUND BusinessException with formatted message`() =
        runTest {
            val docNo = "DOC123"
            val supplierCode = "SUP001"

            coEvery {
                sageApiRepository.getPurchaseOrderDetailByDocNo(docNo, supplierCode)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.SUPPLIER_NOT_FOUND,
                                type = ErrorType.WARNING,
                                title = "",
                                message = "",
                            ),
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_error_supplier_not_found) } returns "Supplier not found: %s"

            val results = mutableListOf<Result<Boolean>>()
            useCase = CheckPurchaseOrderExistsUseCase(sageApiRepository, resourceUtils)

            useCase.execute(docNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorCodes.SUPPLIER_NOT_FOUND, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("Supplier not found: $supplierCode", cause.message)
        }

    @Test
    fun `execute emits Error with ALREADY_REFERENCED BusinessException with formatted message`() =
        runTest {
            val docNo = "DOC123"
            val supplierCode = "SUP001"

            coEvery {
                sageApiRepository.getPurchaseOrderDetailByDocNo(docNo, supplierCode)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.ALREADY_REFERENCED,
                                type = ErrorType.WARNING,
                                title = "",
                                message = "",
                            ),
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_error_po_already_received) } returns "PO already received: %s"

            val results = mutableListOf<Result<Boolean>>()
            useCase = CheckPurchaseOrderExistsUseCase(sageApiRepository, resourceUtils)

            useCase.execute(docNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorCodes.ALREADY_REFERENCED, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("PO already received: $docNo", cause.message)
        }

    @Test
    fun `execute emits Error with BAD_REQUEST BusinessException with formatted message`() =
        runTest {
            val docNo = "DOC123"
            val supplierCode = "SUP001"
            val message = "error service"
            val ref = "ABC-123"

            coEvery {
                sageApiRepository.getPurchaseOrderDetailByDocNo(docNo, supplierCode)
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.BAD_REQUEST,
                                type = ErrorType.ERROR,
                                title = "",
                                message = "error service",
                                ref = "ABC-123",
                            ),
                        canRetry = true,
                    ),
                )

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"

            val results = mutableListOf<Result<Boolean>>()
            useCase = CheckPurchaseOrderExistsUseCase(sageApiRepository, resourceUtils)

            useCase.execute(docNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(ErrorCodes.BAD_REQUEST, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals(message, cause.message)
            assertEquals(ref, cause.ref)
            assertTrue(result.canRetry)
        }

    @Test
    fun `execute emits Error with empty errorCode triggers handleDefaultError as INTERNAL_SERVER_ERROR`() =
        runTest {
            val docNo = "DOC123"
            val supplierCode = "SUP001"

            // mock repository ให้ return Error ที่ cause.errorCode = ""
            coEvery {
                sageApiRepository.getPurchaseOrderDetailByDocNo(docNo, supplierCode)
            } returns
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

            every { resourceUtils.getString(R.string.text_because) } returns "Because: %s"
            every { resourceUtils.getString(R.string.text_because_service_connection_failed) } returns "Service connection failed."

            val results = mutableListOf<Result<Boolean>>()
            useCase.execute(docNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(INTERNAL_SERVER_ERROR, cause.errorCode)
            assertEquals(ErrorType.ERROR, cause.type)
            assertEquals("Because: Service connection failed.", cause.message)
        }
}
