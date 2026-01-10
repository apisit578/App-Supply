package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

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
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteApproveMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteApproveUseCaseTest {
    @RelaxedMockK
    lateinit var repository: SageApiRepository

    @RelaxedMockK
    lateinit var resourceUtils: ResourceUtils

    private lateinit var sdToteApproveUseCase: SdToteApproveUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        sdToteApproveUseCase = SdToteApproveUseCase(repository, resourceUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_sdToteApprove_success_expect_result_success() =
        runTest {
            // given
            val mockModel = mockk<SdToteApproveMappingModel>()
            coEvery { repository.sdToteApprove("123345") } returns flowOf(Result.Success(data = mockModel))
            // when
            val flow = sdToteApproveUseCase.execute(sdNo = "123345")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Success)
                assertEquals(Result.Success(data = mockModel), result)
            }
        }

    @Test
    fun test_sdToteApprove_failure_expect_result_error() =
        runTest {
            // given
            coEvery { repository.sdToteApprove("123345") } returns
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
            val flow = sdToteApproveUseCase.execute(sdNo = "123345")
            // then
            flow.collectLatest { result ->
                assertTrue(result is Result.Error)
            }
        }
}
