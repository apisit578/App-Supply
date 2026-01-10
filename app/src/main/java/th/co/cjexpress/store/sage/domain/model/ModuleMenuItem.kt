package th.co.cjexpress.store.sage.domain.model

import th.co.cjexpress.store.sage.constants.MainMenuType

data class ModuleMenuItem(
    val moduleName: String,
    val listMenu: List<MenuItem>,
)

data class MenuItem(
    val menuType: MainMenuType,
    val isEnable: Boolean,
)
