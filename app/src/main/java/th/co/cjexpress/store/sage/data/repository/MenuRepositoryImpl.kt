package th.co.cjexpress.store.sage.data.repository

import th.co.cjexpress.store.sage.constants.MainMenuType
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.MenuItem
import th.co.cjexpress.store.sage.domain.model.ModuleMenuItem
import th.co.cjexpress.store.sage.domain.repository.MenuRepository

class MenuRepositoryImpl(
    private val appConfig: AppConfig,
) : MenuRepository {
    override fun getModuleMenuItems(): List<ModuleMenuItem> {
        val moduleList = arrayListOf<ModuleMenuItem>()
        moduleList.add(
            ModuleMenuItem(
                moduleName = "พิมพ์",
                listMenu =
                    arrayListOf(
                        MenuItem(
                            menuType = MainMenuType.REPRINT_QR,
                            isEnable =
                                appConfig.payloadConfig
                                    ?.features
                                    ?.menuReprintQR
                                    ?.enable
                                    ?: false,
                        ),
                    ),
            ),
        )

        moduleList.add(
            ModuleMenuItem(
                moduleName = "ขาย",
                listMenu =
                    arrayListOf(
                        MenuItem(
                            menuType = MainMenuType.REQUEST_DISCOUNT_RTC,
                            isEnable =
                                appConfig.payloadConfig
                                    ?.features
                                    ?.menuRequestDiscountRTC
                                    ?.enable
                                    ?: false,
                        ),
                    ),
            ),
        )

        moduleList.add(
            ModuleMenuItem(
                moduleName = "รับสินค้า",
                listMenu =
                    arrayListOf(
                        MenuItem(
                            menuType = MainMenuType.ORDER_RECEIVE,
                            isEnable =
                                appConfig.payloadConfig
                                    ?.features
                                    ?.menuOrderReceive
                                    ?.enable
                                    ?: false,
                        ),
                        MenuItem(
                            menuType = MainMenuType.ORDER_RECEIVE_TOTE,
                            isEnable =
                                appConfig.payloadConfig
                                    ?.features
                                    ?.menuOrderReceiveTote
                                    ?.enable
                                    ?: false,
                        ),
                        MenuItem(
                            menuType = MainMenuType.PURCHASE_INVOICE,
                            isEnable =
                                appConfig.payloadConfig
                                    ?.features
                                    ?.menuPurchaseInvoice
                                    ?.enable
                                    ?: false,
                        ),
                    ),
            ),
        )

        return moduleList
    }
}
