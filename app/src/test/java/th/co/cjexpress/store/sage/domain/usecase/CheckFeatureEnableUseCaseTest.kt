package th.co.cjexpress.store.sage.domain.usecase

import io.mockk.every
import io.mockk.mockk
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.usecase.appconfig.CheckFeatureEnableUseCase

class CheckFeatureEnableUseCaseTest {
    private lateinit var configRepository: ConfigRepository
    private lateinit var useCase: CheckFeatureEnableUseCase

    @Before
    fun setUp() {
        configRepository = mockk()

        useCase = CheckFeatureEnableUseCase(configRepository)
    }

    @After
    fun tearDown() {
    }

    @Test
    fun `execute should return true when featureName is exist in config`() {
        // given
        val mockFeatureEnable = hashMapOf(Pair("loginAD", true))
        val mockAppConfig = mockk<AppConfig>()
        every { mockAppConfig.featureEnable } returns mockFeatureEnable
        every { configRepository.getAppConfig() } returns mockAppConfig

        // when
        val result = useCase.execute("loginAD")

        // then
        assertTrue(result)
    }

    @Test
    fun `execute should return false when featureName is exist in config but feature is false`() {
        // given
        val mockFeatureEnable = hashMapOf(Pair("loginAD", false))
        val mockAppConfig = mockk<AppConfig>()
        every { mockAppConfig.featureEnable } returns mockFeatureEnable
        every { configRepository.getAppConfig() } returns mockAppConfig

        // when
        val result = useCase.execute("loginAD")

        // then
        assertFalse(result)
    }

    @Test
    fun `execute should return false when featureName not exist in config`() {
        // given
        val mockFeatureEnable = hashMapOf(Pair("loginAD", true))
        val mockAppConfig = mockk<AppConfig>()
        every { mockAppConfig.featureEnable } returns mockFeatureEnable
        every { configRepository.getAppConfig() } returns mockAppConfig

        // when
        val result = useCase.execute("test")

        // then
        assertFalse(result)
    }
}
