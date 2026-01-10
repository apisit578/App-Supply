package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import io.mockk.coEvery
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadFeatures
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuOrderReceive
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class MenuOrderReceiveUseCaseTest : BaseUnitTest() {
    @RelaxedMockK
    private lateinit var configRepository: ConfigRepository

    @RelaxedMockK
    private lateinit var useCase: MenuOrderReceiveUseCase

    @Before
    override fun setUp() {
        configRepository = mockk()
        useCase = MenuOrderReceiveUseCase(configRepository)
    }

    @Test
    fun `allowExtraItems returns true when payloadConfig is null`() {
        // Arrange
        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns null
        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.allowExtraItems()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `allowExtraItems returns true when features is null`() {
        // Arrange
        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns null
        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig
        coEvery { configRepository.getAppConfig() } returns mockAppConfig
        // Act
        val result = useCase.allowExtraItems()
        // Assert
        assertTrue(result)
    }

    @Test
    fun `allowExtraItems returns false when config is false`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.allowExtraItems } returns false

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.allowExtraItems()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `allowExtraItems returns true when config is true`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.allowExtraItems } returns true

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.allowExtraItems()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `allowOverReceivingQty returns true when payloadConfig is null`() {
        // Arrange
        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns null
        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.allowOverReceivingQty()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `allowOverReceivingQty returns false when config is false`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.allowOverReceivingQty } returns false

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.allowOverReceivingQty()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `allowOverReceivingQty returns true when config is true`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.allowOverReceivingQty } returns true

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.allowOverReceivingQty()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `maxBatchQty returns 20 when config is 20`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.maxBatchQty } returns 20

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.maxBatchQty()

        // Assert
        assertTrue(result == 20)
    }

    @Test
    fun `maxBatchQty returns 0 when config is null`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.maxBatchQty } returns null

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.maxBatchQty()

        // Assert
        assertTrue(result == 0)
    }

    @Test
    fun `allowOverReceivingQty returns true when features is null`() {
        // Arrange
        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns null

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.allowOverReceivingQty()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `maxBatchQty returns 0 when payloadConfig is null`() {
        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns null
        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        val result = useCase.maxBatchQty()
        assertTrue(result == 0)
    }

    @Test
    fun `maxBatchQty returns 0 when features is null`() {
        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns null

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        val result = useCase.maxBatchQty()
        assertTrue(result == 0)
    }

    @Test
    fun `maxBatchQty returns 0 when menuOrderReceive is null`() {
        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns null

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig

        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        val result = useCase.maxBatchQty()
        assertTrue(result == 0)
    }

    @Test
    fun `allowExtraItems returns true when menuOrderReceive is null`() {
        // Arrange
        val features = mockk<PayloadFeatures>()
        coEvery { features.menuOrderReceive } returns null

        val payload = mockk<PayloadConfig>()
        coEvery { payload.features } returns features

        val app = mockk<AppConfig>()
        coEvery { app.payloadConfig } returns payload

        coEvery { configRepository.getAppConfig() } returns app

        // Act
        val result = useCase.allowExtraItems()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `allowExtraItems returns true when allowExtraItems is null`() {
        // Arrange
        val menu = mockk<MenuOrderReceive>()
        coEvery { menu.allowExtraItems } returns null

        val features = mockk<PayloadFeatures>()
        coEvery { features.menuOrderReceive } returns menu

        val payload = mockk<PayloadConfig>()
        coEvery { payload.features } returns features

        val app = mockk<AppConfig>()
        coEvery { app.payloadConfig } returns payload

        coEvery { configRepository.getAppConfig() } returns app

        // Act
        val result = useCase.allowExtraItems()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `allowOverReceivingQty returns true when menuOrderReceive is null`() {
        // Arrange
        val features = mockk<PayloadFeatures>()
        coEvery { features.menuOrderReceive } returns null

        val payload = mockk<PayloadConfig>()
        coEvery { payload.features } returns features

        val app = mockk<AppConfig>()
        coEvery { app.payloadConfig } returns payload

        coEvery { configRepository.getAppConfig() } returns app

        // Act
        val result = useCase.allowOverReceivingQty()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `allowOverReceivingQty returns true when allowOverReceivingQty is null`() {
        // Arrange
        val menu = mockk<MenuOrderReceive>()
        coEvery { menu.allowOverReceivingQty } returns null

        val features = mockk<PayloadFeatures>()
        coEvery { features.menuOrderReceive } returns menu

        val payload = mockk<PayloadConfig>()
        coEvery { payload.features } returns features

        val app = mockk<AppConfig>()
        coEvery { app.payloadConfig } returns payload

        coEvery { configRepository.getAppConfig() } returns app

        // Act
        val result = useCase.allowOverReceivingQty()

        // Assert
        assertTrue(result)
    }

    @Test
    fun `enableAnalyticsTimeTracking returns false when payloadConfig is null`() {
        // Arrange
        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns null
        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.enableAnalyticsTimeTracking()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `enableAnalyticsTimeTracking returns false when config is false`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.enableAnalyticsTimeTracking } returns false

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig
        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.enableAnalyticsTimeTracking()

        // Assert
        assertFalse(result)
    }

    @Test
    fun `enableAnalyticsTimeTracking returns true when config is true`() {
        // Arrange
        val mockMenuOrderReceive = mockk<MenuOrderReceive>()
        coEvery { mockMenuOrderReceive.enableAnalyticsTimeTracking } returns true

        val mockFeatures = mockk<PayloadFeatures>()
        coEvery { mockFeatures.menuOrderReceive } returns mockMenuOrderReceive

        val mockPayloadConfig = mockk<PayloadConfig>()
        coEvery { mockPayloadConfig.features } returns mockFeatures

        val mockAppConfig = mockk<AppConfig>()
        coEvery { mockAppConfig.payloadConfig } returns mockPayloadConfig
        coEvery { configRepository.getAppConfig() } returns mockAppConfig

        // Act
        val result = useCase.enableAnalyticsTimeTracking()

        // Assert
        assertTrue(result)
    }
}
