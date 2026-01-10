package th.co.cjexpress.store.sage.domain.usecase.appconfig

import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.model.ModuleMenuItem
import th.co.cjexpress.store.sage.domain.repository.MenuRepository
import th.co.cjexpress.store.sage.utils.LogUtils

class GetMenuItemsUseCase(
    private val menuRepository: MenuRepository,
    private val logUtils: LogUtils,
) {
    fun execute(): List<ModuleMenuItem> =
        menuRepository.getModuleMenuItems().let { moduleList ->
            logEventGetPayloadMenuItems(
                moduleList
                    .flatMap { module -> module.listMenu }
                    .map { menu -> menu.menuType.name },
            )
            moduleList
        }

    fun logEventGetPayloadMenuItems(list: List<String>) {
        logUtils.logInfo(
            action = LogAction.GET_PAYLOAD_MENU_ITEMS,
            message = list.toString(),
        )
    }
}
