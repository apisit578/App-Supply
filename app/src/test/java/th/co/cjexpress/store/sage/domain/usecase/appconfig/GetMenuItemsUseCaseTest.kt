package th.co.cjexpress.store.sage.domain.usecase.appconfig

import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.MainMenuType
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.source.remote.FirestoreDataSource
import th.co.cjexpress.store.sage.domain.model.MenuItem
import th.co.cjexpress.store.sage.domain.model.ModuleMenuItem
import th.co.cjexpress.store.sage.domain.repository.MenuRepository
import th.co.cjexpress.store.sage.utils.LogUtils

class GetMenuItemsUseCaseTest {
    private lateinit var useCase: GetMenuItemsUseCase
    private lateinit var menuRepository: MenuRepository
    private lateinit var logUtils: LogUtils
    private val firestoreDataSource: FirestoreDataSource = mockk()
    private val mockAppConfig: AppConfig = mockk()

    @Before
    fun setUp() {
        menuRepository = mockk()
        every { mockAppConfig.getBranchInfo()?.branchCode } returns "M04C"
        every { firestoreDataSource.insertHandheldLog(any()) } returns Unit
        logUtils =
            spyk(
                LogUtils(
                    appConfig = mockAppConfig,
                    dataSource = firestoreDataSource,
                ),
            )
        useCase = GetMenuItemsUseCase(menuRepository, logUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `execute should return menu items from repository`() {
        // Arrange
        val expectedMenuItems =
            listOf(
                ModuleMenuItem(
                    moduleName = "moduleName1",
                    listMenu =
                        listOf(
                            MenuItem(menuType = MainMenuType.REQUEST_DISCOUNT_RTC, isEnable = true),
                            MenuItem(menuType = MainMenuType.ORDER_RECEIVE_TOTE, isEnable = false),
                        ),
                ),
                ModuleMenuItem(
                    moduleName = "moduleName2",
                    listMenu =
                        listOf(
                            MenuItem(menuType = MainMenuType.ORDER_RECEIVE_TOTE, isEnable = true),
                        ),
                ),
            )
        every { menuRepository.getModuleMenuItems() } returns expectedMenuItems

        // Act
        val result = useCase.execute()

        // Assert
        assertEquals(expectedMenuItems, result)
        verify(exactly = 1) { menuRepository.getModuleMenuItems() }
    }

    @Test
    fun `logEventGetMenuItems should logInfo`() =
        runTest {
            // when
            useCase.logEventGetPayloadMenuItems(listOf("Menu 1", "Menu 2"))

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.GET_PAYLOAD_MENU_ITEMS,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
