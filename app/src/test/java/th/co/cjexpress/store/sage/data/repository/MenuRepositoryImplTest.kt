package th.co.cjexpress.store.sage.data.repository

import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.MainMenuType
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadFeatures
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuOrderReceive
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuOrderReceiveTote
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuReprintQR
import th.co.cjexpress.store.sage.domain.model.payload.features.MenuRequestDiscountRTC
import th.co.cjexpress.store.sage.domain.repository.MenuRepository

class MenuRepositoryImplTest {
    private lateinit var appConfig: AppConfig
    private lateinit var repository: MenuRepository
    private val payloadConfig = mockk<PayloadConfig>(relaxed = true)
    private val features = mockk<PayloadFeatures>(relaxed = true)
    private val menuRequestDiscountRTC = mockk<MenuRequestDiscountRTC>(relaxed = true)
    private val menuOrderReceive = mockk<MenuOrderReceive>(relaxed = true)
    private val menuOrderReceiveTote = mockk<MenuOrderReceiveTote>(relaxed = true)
    private val menuReprintQR = mockk<MenuReprintQR>(relaxed = true)

    @Before
    fun setUp() {
        appConfig = mockk()
        every { appConfig.payloadConfig } returns payloadConfig
        every { payloadConfig.features } returns features
        every { features.menuRequestDiscountRTC } returns menuRequestDiscountRTC
        every { features.menuOrderReceive } returns menuOrderReceive
        every { features.menuOrderReceiveTote } returns menuOrderReceiveTote
        every { features.menuReprintQR } returns menuReprintQR
        repository = MenuRepositoryImpl(appConfig)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    // featureMenuRequestDiscountRTC //
    @Test
    fun `should return isEnable true for REQUEST_DISCOUNT_RTC when feature is enabled`() {
        every { menuRequestDiscountRTC.enable } returns true

        val result = repository.getModuleMenuItems()
        val sellModule = result.find { it.moduleName == "ขาย" }
        val menu = sellModule?.listMenu?.find { it.menuType == MainMenuType.REQUEST_DISCOUNT_RTC }

        menu?.isEnable?.let {
            assertTrue(it)
        }
    }

    @Test
    fun `should return isEnable false for REQUEST_DISCOUNT_RTC when feature is disabled`() {
        every { menuRequestDiscountRTC.enable } returns false

        val result = repository.getModuleMenuItems()
        val sellModule = result.find { it.moduleName == "ขาย" }
        val menu = sellModule?.listMenu?.find { it.menuType == MainMenuType.REQUEST_DISCOUNT_RTC }

        menu?.isEnable?.let {
            assertFalse(it)
        }
    }

    // featureMenuOrderReceive //
    @Test
    fun `should return isEnable true for ORDER_RECEIVE when feature is enabled`() {
        every { menuOrderReceive.enable } returns true

        val result = repository.getModuleMenuItems()
        val receiveModule = result.find { it.moduleName == "รับสินค้า" }
        val menu = receiveModule?.listMenu?.find { it.menuType == MainMenuType.ORDER_RECEIVE }

        menu?.isEnable?.let {
            assertTrue(it)
        }
    }

    @Test
    fun `should return isEnable false for ORDER_RECEIVE when feature is disabled`() {
        every { menuOrderReceive.enable } returns false

        val result = repository.getModuleMenuItems()
        val receiveModule = result.find { it.moduleName == "รับสินค้า" }
        val menu = receiveModule?.listMenu?.find { it.menuType == MainMenuType.ORDER_RECEIVE }

        menu?.isEnable?.let {
            assertFalse(it)
        }
    }

    // featureMenuOrderReceiveTote //
    @Test
    fun `should return isEnable true for ORDER_RECEIVE_TOTE when feature is enabled`() {
        every { menuOrderReceiveTote.enable } returns true

        val result = repository.getModuleMenuItems()
        val receiveModule = result.find { it.moduleName == "รับสินค้า" }
        val menu = receiveModule?.listMenu?.find { it.menuType == MainMenuType.ORDER_RECEIVE_TOTE }

        menu?.isEnable?.let {
            assertTrue(it)
        }
    }

    @Test
    fun `should return isEnable false for ORDER_RECEIVE_TOTE when feature is disabled`() {
        every { menuOrderReceiveTote.enable } returns false

        val result = repository.getModuleMenuItems()
        val receiveModule = result.find { it.moduleName == "รับสินค้า" }
        val menu = receiveModule?.listMenu?.find { it.menuType == MainMenuType.ORDER_RECEIVE_TOTE }

        menu?.isEnable?.let {
            assertFalse(it)
        }
    }

    @Test
    fun `should return isEnable false for REPRINT_QR when feature is disabled`() {
        every { menuReprintQR.enable } returns false

        val result = repository.getModuleMenuItems()
        val receiveModule = result.find { it.moduleName == "รับสินค้า" }
        val menu = receiveModule?.listMenu?.find { it.menuType == MainMenuType.REPRINT_QR }

        menu?.isEnable?.let {
            assertFalse(it)
        }
    }
}
