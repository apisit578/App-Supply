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
import th.co.cjexpress.store.sage.domain.model.purchaseInvoice.PurchaseInvoiceSupplierListModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.utils.ResourceUtils
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class FilterSupplierUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var filterSupplierUseCase: FilterSupplierUseCase

    override fun setUp() {
        super.setUp()
        filterSupplierUseCase =
            FilterSupplierUseCase(repository, resourceUtils)
    }

    @Test
    fun test_getFrequencySupplier_success_expect_result_success() =
        runTest {
            // given
            val dataMock = mockk<PurchaseInvoiceSupplierListModel>(relaxed = true)

            coEvery {
                repository.filterSupplier(any())
            } returns flowOf(Result.Success(data = dataMock))
            // when
            val flow =
                filterSupplierUseCase.execute("111")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = dataMock), result)
            }
        }

    @Test
    fun test_getFrequencySupplier_failed_expect_result_error() =
        runTest {
            // given
            coEvery {
                repository.filterSupplier(any())
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
                filterSupplierUseCase.execute("111")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getFrequencySupplier_failed_when_searchKey_blank_expect_result_error() =
        runTest {
            // given
            coEvery {
                repository.filterSupplier(any())
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
                filterSupplierUseCase.execute("")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }

    @Test
    fun test_getFrequencySupplier_failed_when_ERR_NOT_FOUND_expect_result_error() =
        runTest {
            // given
            coEvery {
                repository.filterSupplier(any())
            } returns
                flowOf(
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = "ERR_NOT_FOUND",
                                type = ErrorType.ERROR,
                                title = "",
                                message = "",
                            ),
                    ),
                )
            // when
            val flow =
                filterSupplierUseCase.execute("111")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }
}
