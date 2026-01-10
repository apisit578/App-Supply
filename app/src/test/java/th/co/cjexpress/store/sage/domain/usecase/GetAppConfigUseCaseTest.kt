package th.co.cjexpress.store.sage.domain.usecase

import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class GetAppConfigUseCaseTest {
    private lateinit var configRepository: ConfigRepository
    private lateinit var useCase: GetAppConfigUseCase

    @Before
    fun setUp() {
        configRepository = mockk()
        useCase = GetAppConfigUseCase(configRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `execute should return appConfig`() {
        // given
        val mockAppConfig = mockk<AppConfig>()
        every { configRepository.getAppConfig() } returns mockAppConfig

        // when
        val result = useCase.getAppConfig()

        // then
        assertEquals(result, mockAppConfig)
    }

    @Test
    fun `execute should return branchInfo`() {
        // given
        val mockAppConfig = mockk<AppConfig>()
        val mockBranchInfo = mockk<BranchInfoModel>()
        every { mockAppConfig.getBranchInfo() } returns mockBranchInfo
        every { configRepository.getAppConfig() } returns mockAppConfig

        // when
        val result = useCase.getBranchInfo()

        // then
        assertEquals(result, mockBranchInfo)
    }
}
