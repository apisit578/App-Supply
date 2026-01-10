package th.co.cjexpress.store.sage.presentation.viewmodel.mainMenu

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.MainMenuType
import th.co.cjexpress.store.sage.domain.usecase.appconfig.GetMenuItemsUseCase
import th.co.cjexpress.store.sage.presentation.model.MenuItemUIModel
import th.co.cjexpress.store.sage.presentation.model.ModuleItemUIModel
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class MainMenuViewModel
    @Inject
    constructor(
        private val resourceUtils: ResourceUtils,
        private val getMenuItemsUseCase: GetMenuItemsUseCase,
    ) : BaseViewModel() {
        var moduleList by mutableStateOf<List<ModuleItemUIModel>>(emptyList())
            private set

        fun loadMenuItems() {
            viewModelScope.launch {
                val moduleItems = getMenuItemsUseCase.execute()
                val uiModuleItems = arrayListOf<ModuleItemUIModel>()

                moduleItems.forEach { item ->
                    uiModuleItems.add(
                        ModuleItemUIModel(
                            moduleName = item.moduleName,
                            listMenu =
                                item.listMenu.map { menu ->
                                    MenuItemUIModel(
                                        menuType = menu.menuType,
                                        name = getDisplayName(menu.menuType),
                                        icon = getIcon(menu.menuType),
                                        isEnable = menu.isEnable,
                                    )
                                },
                        ),
                    )
                }
                moduleList = uiModuleItems
                logEventGetMenuItems(
                    moduleList
                        .flatMap { module -> module.listMenu }
                        .map { menu -> menu.name.replace("\n", "") },
                )
            }
        }

        fun getDisplayName(menuType: MainMenuType): String =
            when (menuType) {
                MainMenuType.REQUEST_DISCOUNT_RTC -> resourceUtils.getString(R.string.text_menu_name_request_discount_rtc)
                MainMenuType.ORDER_RECEIVE_TOTE -> resourceUtils.getString(R.string.text_menu_name_order_receive_tote)
                MainMenuType.ORDER_RECEIVE -> resourceUtils.getString(R.string.text_menu_name_order_receive)
                MainMenuType.PURCHASE_INVOICE -> resourceUtils.getString(R.string.text_menu_name_purchase_invoice)
                MainMenuType.REPRINT_QR -> resourceUtils.getString(R.string.text_menu_name_reprint_qr)
                else -> "ชื่อเมนู"
            }

        fun getIcon(menuType: MainMenuType): Int =
            when (menuType) {
                MainMenuType.REQUEST_DISCOUNT_RTC -> R.drawable.ic_default
                MainMenuType.ORDER_RECEIVE_TOTE -> R.drawable.ic_default
                MainMenuType.ORDER_RECEIVE -> R.drawable.ic_default
                MainMenuType.PURCHASE_INVOICE -> R.drawable.ic_default
                MainMenuType.REPRINT_QR -> R.drawable.ic_default
                else -> R.drawable.ic_default
            }

        fun onMenuClick(menu: MainMenuType) {
            setBaseScreenSuccessState(menu)
        }

        fun logEventGetMenuItems(list: List<String>) {
            logUtils.logInfo(
                action = LogAction.GET_MENU_ITEMS,
                message = list.toString(),
            )
        }

        fun logEventSelectMenu(menuType: MainMenuType) {
            logUtils.logInfo(
                action = LogAction.SELECT_MENU,
                message =
                    String.format(
                        resourceUtils.getString(R.string.log_message_select_menu),
                        getDisplayName(menuType),
                    ),
            )
        }
    }
