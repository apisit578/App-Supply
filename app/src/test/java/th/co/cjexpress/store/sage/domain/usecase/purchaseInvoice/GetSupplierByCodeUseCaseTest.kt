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
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class GetSupplierByCodeUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var getSupplierByCodeUseCase: GetSupplierByCodeUseCase

    override fun setUp() {
        super.setUp()
        getSupplierByCodeUseCase =
            GetSupplierByCodeUseCase(repository, resourceUtils)
    }

    @Test
    fun test_getSupplierByCode_success_expect_result_success() =
        runTest {
            // given
            val dataMock = mockk<PurchaseInvoiceSupplierModel>(relaxed = true)

            coEvery {
                repository.getSupplierByCode(any())
            } returns flowOf(Result.Success(data = dataMock))
            // when
            val flow =
                getSupplierByCodeUseCase.execute("111")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = dataMock), result)
            }
        }

    @Test
    fun test_getSupplierByCode_failed_expect_result_error() =
        runTest {
            // given
            coEvery {
                repository.getSupplierByCode(any())
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
                getSupplierByCodeUseCase.execute("111")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }
}
