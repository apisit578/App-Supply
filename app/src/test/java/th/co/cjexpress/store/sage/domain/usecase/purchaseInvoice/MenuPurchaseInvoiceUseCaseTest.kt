package th.co.cjexpress.store.sage.domain.usecase.purchaseInvoice

import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import junit.framework.TestCase.assertEquals
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.BILL_NO_VALIDATION_REGEX
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadFeatures
import th.co.cjexpress.store.sage.domain.model.payload.features.BillNoValidation
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuPurchaseInvoice
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class MenuPurchaseInvoiceUseCaseTest {
    private lateinit var configRepository: ConfigRepository
    private lateinit var useCase: MenuPurchaseInvoiceUseCase
    private lateinit var appConfig: AppConfig

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        configRepository = mockk()
        appConfig = AppConfig()
        useCase = MenuPurchaseInvoiceUseCase(configRepository)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `supplierPerPage should return configured value`() {
        val expected = 20
        val menuPurchaseInvoice = MenuPurchaseInvoice(supplierPerPage = expected)
        val features = PayloadFeatures(menuPurchaseInvoice = menuPurchaseInvoice)
        val payloadConfig =
            mockk<PayloadConfig> {
                every { this@mockk.features } returns features
            }
        appConfig.updatePayloadConfig(payloadConfig)

        every { configRepository.getAppConfig() } returns appConfig

        val result = useCase.supplierPerPage()
        assertEquals(expected, result)
    }

    @Test
    fun `supplierPerPage should return default 20 when config is null`() {
        every { configRepository.getAppConfig() } returns appConfig // payloadConfig = null

        val result = useCase.supplierPerPage()
        assertEquals(20, result)
    }

    @Test
    fun `getBillNoValidationRegEx should return configured value`() {
        val expectedRegex = "^[A-Z0-9/\\\\-.]+\$"
        val billNoValidation = BillNoValidation(billNoRegEx = expectedRegex)
        val menuPurchaseInvoice = MenuPurchaseInvoice(billNoValidation = billNoValidation)
        val features = PayloadFeatures(menuPurchaseInvoice = menuPurchaseInvoice)
        val payloadConfig =
            mockk<PayloadConfig> {
                every { this@mockk.features } returns features
            }
        appConfig.updatePayloadConfig(payloadConfig)

        every { configRepository.getAppConfig() } returns appConfig

        val result = useCase.getBillNoValidationRegEx()
        assertEquals(expectedRegex, result)
    }

    @Test
    fun `getBillNoValidationRegEx should return default regex when config is null`() {
        every { configRepository.getAppConfig() } returns appConfig // payloadConfig = null

        val result = useCase.getBillNoValidationRegEx()
        assertEquals(BILL_NO_VALIDATION_REGEX, result)
    }

    @Test
    fun `getBillNoValidationPlaceholder should return configured placeholder`() {
        val expectedPlaceholder = "กรุณากรอก A-Z, 0-9, /, -, ."
        val billNoValidation = BillNoValidation(placeholder = expectedPlaceholder)
        val menuPurchaseInvoice = MenuPurchaseInvoice(billNoValidation = billNoValidation)
        val features = PayloadFeatures(menuPurchaseInvoice = menuPurchaseInvoice)
        val payloadConfig =
            mockk<PayloadConfig> {
                every { this@mockk.features } returns features
            }
        appConfig.updatePayloadConfig(payloadConfig)

        every { configRepository.getAppConfig() } returns appConfig

        val result = useCase.getBillNoValidationPlaceholder()
        assertEquals(expectedPlaceholder, result)
    }

    @Test
    fun `getBillNoValidationPlaceholder should return default when config is null`() {
        val expected = "กรุณากรอก A-Z, 0-9, /, -, ."
        every { configRepository.getAppConfig() } returns appConfig

        val result = useCase.getBillNoValidationPlaceholder()
        assertEquals(expected, result)
    }
}
