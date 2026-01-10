package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceDocumentListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class GetPurchaseInvoiceDocumentUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var useCase: GetPurchaseInvoiceDocumentUseCase

    override fun setUp() {
        super.setUp()
        useCase = GetPurchaseInvoiceDocumentUseCase(repository, resourceUtils)
    }

    @Test
    fun test_getPurchaseInvoiceDocument_success_expect_result_success() =
        runTest {
            // given
            val supplierCode = "S001"
            val dataMock = mockk<PurchaseInvoiceDocumentListModel>(relaxed = true)

            coEvery {
                repository.getPurchaseInvoiceDocument(supplierCode)
            } returns flowOf(Result.Success(data = dataMock))

            // when
            val flow = useCase.execute(supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(dataMock), result)
            }
        }

    @Test
    fun test_getPurchaseInvoiceDocument_error_expect_result_error() =
        runTest {
            // given
            val supplierCode = "S001"
            coEvery {
                repository.getPurchaseInvoiceDocument(supplierCode)
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
            val flow = useCase.execute(supplierCode)

            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }
}
