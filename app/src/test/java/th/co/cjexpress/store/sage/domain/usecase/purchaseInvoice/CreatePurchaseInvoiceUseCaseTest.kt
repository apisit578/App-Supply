package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorCodes.INTERNAL_SERVER_ERROR
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceCreateModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException

class CreatePurchaseInvoiceUseCaseTest {
    private lateinit var sageApiRepository: SageApiRepository
    private lateinit var resourceUtils: ResourceUtils
    private lateinit var useCase: CreatePurchaseInvoiceUseCase

    private val poNo = "PO123"
    private val billNo = "BILL123"
    private val supplierCode = "SUP001"

    @Before
    fun setup() {
        sageApiRepository = mockk()
        resourceUtils = mockk()

        // ตัวอย่าง mock string resources สำหรับแต่ละข้อความที่ใช้
        every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
        every { resourceUtils.getString(R.string.text_error_po_already_received) } returns "PO already received: %s"
        every { resourceUtils.getString(R.string.text_error_duplicate_bill_no) } returns "Duplicate bill no"
        every { resourceUtils.getString(R.string.text_error_check_information) } returns "Check your information"
        every { resourceUtils.getString(R.string.text_error_supplier_disabled) } returns "Supplier disabled: %s"
        every { resourceUtils.getString(R.string.text_error_data_not_found) } returns "Data not found"

        useCase = CreatePurchaseInvoiceUseCase(sageApiRepository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `execute emits Success when repository returns Success`() =
        runTest {
            val expectedModel = mockk<PurchaseInvoiceCreateModel>()
            coEvery {
                sageApiRepository.createPurchaseInvoice(
                    poNo,
                    billNo,
                    supplierCode,
                )
            } returns flowOf(Result.Success(expectedModel))

            val results = mutableListOf<Result<PurchaseInvoiceCreateModel>>()
            useCase.execute(poNo, billNo, supplierCode).toList(results)

            assertEquals(1, results.size)
            val result = results[0]
            assertTrue(result is Result.Success)
            assertEquals(expectedModel, (result as Result.Success).data)
        }

    @Test
    fun `execute emits Error ALREADY_REFERENCED with BusinessException`() =
        runTest {
            val errorCode = ErrorCodes.ALREADY_REFERENCED
            val errorCause = BusinessException(errorCode, ErrorType.WARNING, "", "", "")
            coEvery {
                sageApiRepository.createPurchaseInvoice(
                    poNo,
                    billNo,
                    supplierCode,
                )
            } returns flowOf(Result.Error(cause = errorCause))

            val results = mutableListOf<Result<PurchaseInvoiceCreateModel>>()
            useCase.execute(poNo, billNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException

            assertEquals(errorCode, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("PO already received: $poNo", cause.message)
        }

    @Test
    fun `execute emits Error ALREADY_EXISTS with BusinessException`() =
        runTest {
            val errorCode = ErrorCodes.ALREADY_EXISTS
            val errorCause = BusinessException(errorCode, ErrorType.WARNING, "", "", "")
            coEvery {
                sageApiRepository.createPurchaseInvoice(
                    poNo,
                    billNo,
                    supplierCode,
                )
            } returns flowOf(Result.Error(cause = errorCause))

            val results = mutableListOf<Result<PurchaseInvoiceCreateModel>>()
            useCase.execute(poNo, billNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException

            assertEquals(errorCode, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("PO already received: $poNo", cause.message)
        }

    @Test
    fun `execute emits Error DUPLICATE_DATA with BusinessException`() =
        runTest {
            val errorCode = ErrorCodes.DUPLICATE_DATA
            val errorCause = BusinessException(errorCode, ErrorType.WARNING, "", "", "")
            coEvery {
                sageApiRepository.createPurchaseInvoice(
                    poNo,
                    billNo,
                    supplierCode,
                )
            } returns flowOf(Result.Error(cause = errorCause))

            val results = mutableListOf<Result<PurchaseInvoiceCreateModel>>()
            useCase.execute(poNo, billNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException

            assertEquals(errorCode, cause.errorCode)
            assertEquals("Duplicate bill no", cause.title)
            assertEquals("Check your information", cause.message)
        }

    @Test
    fun `execute emits Error SUP_INACTIVE with BusinessException`() =
        runTest {
            val errorCode = ErrorCodes.SUPPLIER_INACTIVE
            val errorCause = BusinessException(errorCode, ErrorType.WARNING, "", "", "")
            coEvery {
                sageApiRepository.createPurchaseInvoice(
                    poNo,
                    billNo,
                    supplierCode,
                )
            } returns flowOf(Result.Error(cause = errorCause))

            val results = mutableListOf<Result<PurchaseInvoiceCreateModel>>()
            useCase.execute(poNo, billNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException

            assertEquals(errorCode, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("Supplier disabled: $supplierCode", cause.message)
        }

    @Test
    fun `execute emits Error NOT_FOUND with BusinessException`() =
        runTest {
            val errorCode = ErrorCodes.NOT_FOUND
            val errorCause = BusinessException(errorCode, ErrorType.WARNING, "", "", "")
            coEvery {
                sageApiRepository.createPurchaseInvoice(
                    poNo,
                    billNo,
                    supplierCode,
                )
            } returns flowOf(Result.Error(cause = errorCause))

            val results = mutableListOf<Result<PurchaseInvoiceCreateModel>>()
            useCase.execute(poNo, billNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException

            assertEquals(errorCode, cause.errorCode)
            assertEquals("Unable to continue", cause.title)
            assertEquals("Data not found", cause.message)
        }

    @Test
    fun `execute emits Error with unknown errorCode returns default handled error`() =
        runTest {
            coEvery {
                sageApiRepository.createPurchaseInvoice(
                    poNo,
                    billNo,
                    supplierCode,
                )
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

            // mock default error messages used inside handleDefaultError()
            every { resourceUtils.getString(R.string.text_because) } returns "Because: %s"
            every { resourceUtils.getString(R.string.text_because_service_connection_failed) } returns "Service connection failed."

            val results = mutableListOf<Result<PurchaseInvoiceCreateModel>>()
            useCase.execute(poNo, billNo, supplierCode).toList(results)

            val result = results.first()
            assertTrue(result is Result.Error)
            val cause = (result as Result.Error).cause as BusinessException
            assertEquals(INTERNAL_SERVER_ERROR, cause.errorCode)
            assertEquals(ErrorType.ERROR, cause.type)
            assertEquals("Because: Service connection failed.", cause.message)
        }
}
