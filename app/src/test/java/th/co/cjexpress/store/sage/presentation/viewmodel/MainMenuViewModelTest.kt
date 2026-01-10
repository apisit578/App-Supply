package th.co.cjexpress.store.sage.presentation.viewmodel

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.MainMenuType
import th.co.cjexpress.store.sage.domain.model.MenuItem
import th.co.cjexpress.store.sage.domain.model.ModuleMenuItem
import th.co.cjexpress.store.sage.domain.usecase.appconfig.GetMenuItemsUseCase
import th.co.cjexpress.store.sage.presentation.viewmodel.mainMenu.MainMenuViewModel
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

@OptIn(ExperimentalCoroutinesApi::class)
class MainMenuViewModelTest : BaseUnitTest() {
    private lateinit var viewModel: MainMenuViewModel

    private val resourceUtils: ResourceUtils = mockk(relaxed = true)
    private val getMenuItemsUseCase: GetMenuItemsUseCase = mockk()

    override fun setUp() {
        super.setUp()
        viewModel =
            MainMenuViewModel(
                resourceUtils = resourceUtils,
                getMenuItemsUseCase = getMenuItemsUseCase,
            )
    }

    @Test
    fun `loadMenuItems should update moduleList when menu type is REQUEST_DISCOUNT_RTC`() =
        runTest {
            val mockString = "ขอส่วนลด(RTC)"
            val mockMenuItems =
                listOf(
                    ModuleMenuItem(
                        moduleName = "Test Module",
                        listMenu =
                            listOf(
                                MenuItem(
                                    menuType = MainMenuType.REQUEST_DISCOUNT_RTC,
                                    isEnable = true,
                                ),
                            ),
                    ),
                )

            every { getMenuItemsUseCase.execute() } returns mockMenuItems
            every { resourceUtils.getString(any()) } returns mockString

            viewModel.loadMenuItems()
            advanceUntilIdle()

            val result = viewModel.moduleList
            assertEquals(1, result.size)
            val menu = result[0].listMenu[0]
            assertEquals(MainMenuType.REQUEST_DISCOUNT_RTC, menu.menuType)
            assertEquals(mockString, menu.name)
            assertTrue(menu.isEnable)
        }

    @Test
    fun `loadMenuItems should update moduleList when menu type is ORDER_RECEIVE_TOTE`() =
        runTest {
            val mockString = "ตรวจสอบสินค้า ภายใน Tote"
            val mockMenuItems =
                listOf(
                    ModuleMenuItem(
                        moduleName = "Test Module",
                        listMenu =
                            listOf(
                                MenuItem(
                                    menuType = MainMenuType.ORDER_RECEIVE_TOTE,
                                    isEnable = false,
                                ),
                            ),
                    ),
                )

            every { getMenuItemsUseCase.execute() } returns mockMenuItems
            every { resourceUtils.getString(any()) } returns mockString

            viewModel.loadMenuItems()
            advanceUntilIdle()

            val result = viewModel.moduleList
            assertEquals(1, result.size)
            val menu = result[0].listMenu[0]
            assertEquals(MainMenuType.ORDER_RECEIVE_TOTE, menu.menuType)
            assertEquals(mockString, menu.name)
            assertFalse(menu.isEnable)
        }

    @Test
    fun `loadMenuItems should update moduleList when menu type is ORDER_RECEIVE`() =
        runTest {
            val mockString = "รับสินค้าจากคลัง"
            val mockMenuItems =
                listOf(
                    ModuleMenuItem(
                        moduleName = "Test Module",
                        listMenu =
                            listOf(
                                MenuItem(
                                    menuType = MainMenuType.ORDER_RECEIVE,
                                    isEnable = false,
                                ),
                            ),
                    ),
                )

            every { getMenuItemsUseCase.execute() } returns mockMenuItems
            every { resourceUtils.getString(any()) } returns mockString

            viewModel.loadMenuItems()
            advanceUntilIdle()

            val result = viewModel.moduleList
            assertEquals(1, result.size)
            val menu = result[0].listMenu[0]
            assertEquals(MainMenuType.ORDER_RECEIVE, menu.menuType)
            assertEquals(mockString, menu.name)
            assertFalse(menu.isEnable)
        }

    @Test
    fun `loadMenuItems should update moduleList when menu type is NONE`() =
        runTest {
            val mockString = "ชื่อเมนู"
            val mockMenuItems =
                listOf(
                    ModuleMenuItem(
                        moduleName = "Test Module",
                        listMenu =
                            listOf(
                                MenuItem(
                                    menuType = MainMenuType.NONE,
                                    isEnable = false,
                                ),
                            ),
                    ),
                )

            every { getMenuItemsUseCase.execute() } returns mockMenuItems
            every { resourceUtils.getString(any()) } returns mockString

            viewModel.loadMenuItems()
            advanceUntilIdle()

            val result = viewModel.moduleList
            assertEquals(1, result.size)
            val menu = result[0].listMenu[0]
            assertEquals(mockString, menu.name)
            assertFalse(menu.isEnable)
        }

    @Test
    fun `loadMenuItems should update moduleList when menu type is PURCHASE_INVOICE`() =
        runTest {
            val mockString = "รับสินค้าจาก ผู้จำหน่าย"
            val mockMenuItems =
                listOf(
                    ModuleMenuItem(
                        moduleName = "Test Module",
                        listMenu =
                            listOf(
                                MenuItem(
                                    menuType = MainMenuType.PURCHASE_INVOICE,
                                    isEnable = true,
                                ),
                            ),
                    ),
                )

            every { getMenuItemsUseCase.execute() } returns mockMenuItems
            every { resourceUtils.getString(any()) } returns mockString

            viewModel.loadMenuItems()
            advanceUntilIdle()

            val result = viewModel.moduleList
            assertEquals(1, result.size)
            val menu = result[0].listMenu[0]
            assertEquals(MainMenuType.PURCHASE_INVOICE, menu.menuType)
            assertEquals(mockString, menu.name)
            assertTrue(menu.isEnable)
        }

    @Test
    fun `loadMenuItems should update moduleList when menu type is PURCHASE_INVOICE_false`() =
        runTest {
            val mockString = "รับสินค้าจาก ผู้จำหน่าย"
            val mockMenuItems =
                listOf(
                    ModuleMenuItem(
                        moduleName = "Test Module",
                        listMenu =
                            listOf(
                                MenuItem(
                                    menuType = MainMenuType.PURCHASE_INVOICE,
                                    isEnable = false,
                                ),
                            ),
                    ),
                )

            every { getMenuItemsUseCase.execute() } returns mockMenuItems
            every { resourceUtils.getString(any()) } returns mockString

            viewModel.loadMenuItems()
            advanceUntilIdle()

            val result = viewModel.moduleList
            assertEquals(1, result.size)
            val menu = result[0].listMenu[0]
            assertEquals(MainMenuType.PURCHASE_INVOICE, menu.menuType)
            assertEquals(mockString, menu.name)
            assertFalse(menu.isEnable)
        }

    @Test
    fun `getDisplayName should return correct string for each supported menu type`() =
        runTest {
            val rtcName = "ขอส่วนลด(RTC)"
            val toteName = "ตรวจสอบสินค้า\\nภายใน Tote"
            val receiveName = "รับสินค้า\\nจากคลัง"
            val invoiceName = "รับสินค้าจาก\\nผู้จำหน่าย"
            val reprintQRName = "พิมพ์ QR สินค้า"

            every { resourceUtils.getString(R.string.text_menu_name_request_discount_rtc) } returns rtcName
            every { resourceUtils.getString(R.string.text_menu_name_order_receive_tote) } returns toteName
            every { resourceUtils.getString(R.string.text_menu_name_order_receive) } returns receiveName
            every { resourceUtils.getString(R.string.text_menu_name_purchase_invoice) } returns invoiceName
            every { resourceUtils.getString(R.string.text_menu_name_reprint_qr) } returns reprintQRName

            assertEquals(rtcName, viewModel.getDisplayName(MainMenuType.REQUEST_DISCOUNT_RTC))
            assertEquals(toteName, viewModel.getDisplayName(MainMenuType.ORDER_RECEIVE_TOTE))
            assertEquals(receiveName, viewModel.getDisplayName(MainMenuType.ORDER_RECEIVE))
            assertEquals(invoiceName, viewModel.getDisplayName(MainMenuType.PURCHASE_INVOICE))
            assertEquals(reprintQRName, viewModel.getDisplayName(MainMenuType.REPRINT_QR))
        }

    @Test
    fun `getDisplayName should return default when menu type is not handled`() =
        runTest {
            val unknownType =
                MainMenuType.entries
                    .first {
                        it !in
                            listOf(
                                MainMenuType.REQUEST_DISCOUNT_RTC,
                                MainMenuType.ORDER_RECEIVE_TOTE,
                                MainMenuType.ORDER_RECEIVE,
                                MainMenuType.PURCHASE_INVOICE,
                                MainMenuType.REPRINT_QR,
                            )
                    }

            assertEquals("ชื่อเมนู", viewModel.getDisplayName(unknownType))
        }

    @Test
    fun `getIcon should return ic_default for every menu type`() {
        val expectedIcon = R.drawable.ic_default

        assertEquals(expectedIcon, viewModel.getIcon(MainMenuType.REQUEST_DISCOUNT_RTC))
        assertEquals(expectedIcon, viewModel.getIcon(MainMenuType.ORDER_RECEIVE_TOTE))
        assertEquals(expectedIcon, viewModel.getIcon(MainMenuType.ORDER_RECEIVE))
        assertEquals(expectedIcon, viewModel.getIcon(MainMenuType.PURCHASE_INVOICE))
        assertEquals(expectedIcon, viewModel.getIcon(MainMenuType.REPRINT_QR))

        val unknownType =
            MainMenuType.entries
                .first {
                    it !in
                        listOf(
                            MainMenuType.REQUEST_DISCOUNT_RTC,
                            MainMenuType.ORDER_RECEIVE_TOTE,
                            MainMenuType.ORDER_RECEIVE,
                            MainMenuType.PURCHASE_INVOICE,
                            MainMenuType.REPRINT_QR,
                        )
                }

        assertEquals(expectedIcon, viewModel.getIcon(unknownType))
    }

    @Test
    fun `logEventGetMenuItems should logInfo`() =
        runTest {
            // when
            viewModel.logEventGetMenuItems(listOf("Menu 1", "Menu 2"))

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.GET_MENU_ITEMS,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventSelectMenu should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_select_menu) } returns "Selected Menu : "

            // when
            viewModel.logEventSelectMenu(MainMenuType.REQUEST_DISCOUNT_RTC)

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.SELECT_MENU,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
