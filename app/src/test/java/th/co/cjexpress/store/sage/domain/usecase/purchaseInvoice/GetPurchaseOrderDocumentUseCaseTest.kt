package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseOrderDocumentListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetPurchaseOrderDocumentUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: GetPurchaseOrderDocumentUseCase

    override fun setUp() {
        super.setUp()
        useCase = GetPurchaseOrderDocumentUseCase(repository, resourceUtils)
    }

    @Test
    fun test_getPurchaseOrderDocument_success_expect_result_success() =
        runTest {
            // given
            val mockData = mockk<PurchaseOrderDocumentListModel>(relaxed = true)
            coEvery { repository.getPurchaseOrderDocument("SUP123") } returns
                flowOf(
                    Result.Success(
                        mockData,
                    ),
                )

            // when
            val resultFlow = useCase.execute("SUP123")

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(mockData), result)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_generic_error_expect_result_error() =
        runTest {
            // given
            coEvery { repository.getPurchaseOrderDocument("SUP123") } returns
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
            val resultFlow = useCase.execute("SUP123")

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_not_found_error_expect_warning() =
        runTest {
            // given
            val expectedException =
                BusinessException(
                    errorCode = ErrorCodes.NOT_FOUND,
                    type = ErrorType.ERROR,
                    title = "",
                    message = "",
                )

            coEvery {
                repository.getPurchaseOrderDocument("SUP123")
            } returns flowOf(Result.Error(cause = expectedException))

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_no_pending_purchase_order) } returns "No pending PO"

            // when
            val resultFlow = useCase.execute("SUP123")

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error

                assertEquals(ErrorType.WARNING, errorResult.cause?.type)
                assertEquals(ErrorCodes.NOT_FOUND, errorResult.cause?.errorCode)
                assertEquals("Unable to continue", errorResult.cause?.title)
                assertEquals("No pending PO", errorResult.cause?.message)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_supplier_inactive_error_expect_warning() =
        runTest {
            // given
            val expectedException =
                BusinessException(
                    errorCode = ErrorCodes.SUPPLIER_INACTIVE,
                    type = ErrorType.ERROR,
                    title = "",
                    message = "",
                )

            coEvery {
                repository.getPurchaseOrderDocument("SUP123")
            } returns flowOf(Result.Error(cause = expectedException))

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_error_supplier_disabled) } returns "Supplier %s is disabled"

            // when
            val resultFlow = useCase.execute("SUP123")

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error

                assertEquals(ErrorType.WARNING, errorResult.cause?.type)
                assertEquals(ErrorCodes.SUPPLIER_INACTIVE, errorResult.cause?.errorCode)
                assertEquals("Unable to continue", errorResult.cause?.title)
                assertEquals("Supplier SUP123 is disabled", errorResult.cause?.message)
            }
        }

    @Test
    fun test_getPurchaseOrderDocument_supplier_not_found_error_expect_warning() =
        runTest {
            // given
            val expectedException =
                BusinessException(
                    errorCode = ErrorCodes.SUPPLIER_NOT_FOUND,
                    type = ErrorType.ERROR,
                    title = "",
                    message = "",
                )

            coEvery {
                repository.getPurchaseOrderDocument("SUP123")
            } returns flowOf(Result.Error(cause = expectedException))

            every { resourceUtils.getString(R.string.text_unable_to_continue) } returns "Unable to continue"
            every { resourceUtils.getString(R.string.text_error_supplier_not_found) } returns "Supplier %s not found"

            // when
            val resultFlow = useCase.execute("SUP123")

            // then
            resultFlow.collectLatest { result ->
                assertTrue(result is Result.Error)
                val errorResult = result as Result.Error

                assertEquals(ErrorType.WARNING, errorResult.cause?.type)
                assertEquals(ErrorCodes.SUPPLIER_NOT_FOUND, errorResult.cause?.errorCode)
                assertEquals("Unable to continue", errorResult.cause?.title)
                assertEquals("Supplier SUP123 not found", errorResult.cause?.message)
            }
        }
}
