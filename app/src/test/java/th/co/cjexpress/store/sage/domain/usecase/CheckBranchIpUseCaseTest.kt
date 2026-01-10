package th.co.cjexpress.store.sage.domain.usecase

import com.google.firebase.crashlytics.FirebaseCrashlytics
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.BranchIpModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.repository.BranchIpRepository
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.usecase.branch_master.CheckBranchIpUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.CrashlyticsLoggerUtils

class CheckBranchIpUseCaseTest {
    private lateinit var branchIpRepository: BranchIpRepository
    private lateinit var configRepository: ConfigRepository

    private lateinit var useCase: CheckBranchIpUseCase

    @Before
    fun setUp() {
        branchIpRepository = mockk()
        configRepository = mockk()

        mockkStatic(FirebaseCrashlytics::class)
        every { FirebaseCrashlytics.getInstance() } returns mockk(relaxed = true)
        CrashlyticsLoggerUtils.initCrashlytics()

        useCase = CheckBranchIpUseCase(branchIpRepository, configRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `execute should return result success when checkBranchIP success`() =
        runTest {
            // given
            val mockBranchIpModel = BranchIpModel("0101", "branch-0101")
            coEvery { branchIpRepository.checkBranchIP(any()) } returns
                Result.Success(
                    mockBranchIpModel,
                )
            coEvery { configRepository.updateBranchInfo(any()) } returns Unit

            // when
            val result = useCase.execute()

            // then
            assertTrue(result is Result.Success)
            assertEquals(mockBranchIpModel, (result as Result.Success).data)
            coVerify { branchIpRepository.checkBranchIP(any()) }
            coVerify { configRepository.updateBranchInfo(any()) }
        }

    @Test
    fun `execute should return result error when checkBranchIP error`() =
        runTest {
            // given
            coEvery { branchIpRepository.checkBranchIP(any()) } returns
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "UNKNOWN_ERROR",
                            type = ErrorType.ERROR,
                            message = null,
                        ),
                )
            coEvery { configRepository.updateBranchInfo(any()) } returns Unit

            // when
            val result = useCase.execute()

            // then
            assertTrue(result is Result.Error)
            coVerify { branchIpRepository.checkBranchIP(any()) }
            coVerify { configRepository.updateBranchInfo(any()) }
        }

    @Test
    fun `update branch info with null`() {
        coEvery { configRepository.updateBranchInfo(any()) } returns Unit
        useCase.updateBranchInfo()
        coVerify { configRepository.updateBranchInfo(any()) }
    }
}
