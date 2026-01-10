package th.co.cjexpress.store.sage.domain.usecase.appconfig

import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadFeatures
import th.co.cjexpress.store.sage.domain.model.payload.features.FeatureConfigs
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import java.util.Date

class FeatureConfigsUseCaseTest : BaseUnitTest() {
    private lateinit var configRepository: ConfigRepository
    private lateinit var useCase: FeatureConfigsUseCase

    @Before
    override fun setUp() {
        configRepository = mockk()
        useCase = FeatureConfigsUseCase(configRepository)
    }

    @Test
    fun `should return totePrefix from features configs when present`() {
        // Arrange
        val expectedPrefix = listOf("X", "Y", "Z")
        val featureConfigs = FeatureConfigs(totePrefix = expectedPrefix)
        val payloadFeatures = PayloadFeatures(configs = featureConfigs)
        val payloadConfig =
            PayloadConfig(
                branchCode = "MD001",
                featureEnable = null,
                features = payloadFeatures,
                oAuth = null,
                services = null,
                loginGroups = null,
                totePrefix = null,
                createdAt = Date(),
                updatedAt = Date(),
                updatedBy = "OA_apisit",
            )
        val appConfig =
            AppConfig().apply {
                updatePayloadConfig(payloadConfig)
            }
        every { configRepository.getAppConfig() } returns appConfig

        // Act
        val result = useCase.getTotePrefix()

        // Assert
        assertEquals(expectedPrefix, result)
    }

    @Test
    fun `should return default prefix when features is null`() {
        // Arrange
        val payloadConfig =
            PayloadConfig(
                branchCode = "MD0033",
                featureEnable = null,
                features = null,
                oAuth = null,
                services = null,
                loginGroups = null,
                totePrefix = null,
                createdAt = Date(),
                updatedAt = Date(),
                updatedBy = "OA_apisit",
            )
        val appConfig =
            AppConfig().apply {
                updatePayloadConfig(payloadConfig)
            }
        every { configRepository.getAppConfig() } returns appConfig

        // Act
        val result = useCase.getTotePrefix()

        // Assert
        assertEquals(listOf("T", "B"), result)
    }

    @Test
    fun `should return default prefix when configs is null`() {
        // Arrange
        val payloadFeatures = PayloadFeatures(configs = null)
        val payloadConfig =
            PayloadConfig(
                branchCode = "MD0044",
                featureEnable = null,
                features = payloadFeatures,
                oAuth = null,
                services = null,
                loginGroups = null,
                totePrefix = null,
                createdAt = Date(),
                updatedAt = Date(),
                updatedBy = "OA_apisit",
            )
        val appConfig =
            AppConfig().apply {
                updatePayloadConfig(payloadConfig)
            }
        every { configRepository.getAppConfig() } returns appConfig

        // Act
        val result = useCase.getTotePrefix()

        // Assert
        assertEquals(listOf("T", "B"), result)
    }

    @Test
    fun `should return default prefix when payloadConfig is null`() {
        // Arrange
        val appConfig =
            AppConfig().apply {
                payloadConfig = null
            }
        every { configRepository.getAppConfig() } returns appConfig

        // Act
        val result = useCase.getTotePrefix()

        // Assert
        assertEquals(listOf("T", "B"), result)
    }

    // --- เพิ่มเติม ---

    @Test
    fun `should return default prefix when totePrefix is null in configs`() {
        // Arrange
        val featureConfigs = FeatureConfigs(totePrefix = null)
        val payloadFeatures = PayloadFeatures(configs = featureConfigs)
        val payloadConfig =
            PayloadConfig(
                branchCode = "MD0055",
                featureEnable = null,
                features = payloadFeatures,
                oAuth = null,
                services = null,
                loginGroups = null,
                totePrefix = null,
                createdAt = Date(),
                updatedAt = Date(),
                updatedBy = "OA_apisit",
            )
        val appConfig =
            AppConfig().apply {
                updatePayloadConfig(payloadConfig)
            }
        every { configRepository.getAppConfig() } returns appConfig

        // Act
        val result = useCase.getTotePrefix()

        // Assert
        assertEquals(listOf("T", "B"), result)
    }

    @Test
    fun `should return empty list when totePrefix is empty`() {
        // Arrange
        val featureConfigs = FeatureConfigs(totePrefix = emptyList())
        val payloadFeatures = PayloadFeatures(configs = featureConfigs)
        val payloadConfig =
            PayloadConfig(
                branchCode = "MD0066",
                featureEnable = null,
                features = payloadFeatures,
                oAuth = null,
                services = null,
                loginGroups = null,
                totePrefix = null,
                createdAt = Date(),
                updatedAt = Date(),
                updatedBy = "OA_apisit",
            )
        val appConfig =
            AppConfig().apply {
                updatePayloadConfig(payloadConfig)
            }
        every { configRepository.getAppConfig() } returns appConfig

        // Act
        val result = useCase.getTotePrefix()

        // Assert
        assertEquals(emptyList<String>(), result)
    }
}
