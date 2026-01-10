package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetBdDocumentUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    private lateinit var resourceUtils: ResourceUtils

    private lateinit var getBdListUseCase: GetBdDocumentUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        getBdListUseCase = GetBdDocumentUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_getBdList_success_expect_result_success() =
        runTest {
            // given
            val mockModel = mockk<BdDocumentMappingModel>()
            val expectedResult = flowOf(Result.Success(mockModel))
            coEvery { repository.bdDocument() } returns expectedResult
            // when
            val result = getBdListUseCase.execute()
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_getBdList_failure_expect_result_error() =
        runTest {
            // given
            val mockModel = mockk<BdDocumentMappingModel>()
            val expectedError =
                BusinessException(
                    message = "Error",
                    errorCode = "123",
                    type = ErrorType.ERROR,
                )
            val expectedResult = flowOf(Result.Error(mockModel, expectedError))

            coEvery { repository.bdDocument() } returns expectedResult
            // when
            val result = getBdListUseCase.execute()
            // then
            result.collectLatest { result ->
                assertTrue(result is Result.Error)
                assertEquals(Result.Error(data = mockModel, expectedError), result)
            }
        }
}
