package th.co.cjexpress.store.sage.presentation.model

import th.co.cjexpress.store.sage.constants.MainMenuType

data class ModuleItemUIModel(
    val moduleName: String,
    val listMenu: List<MenuItemUIModel>,
)

data class MenuItemUIModel(
    val name: String,
    val menuType: MainMenuType,
    val icon: Int,
    val isEnable: Boolean,
)
