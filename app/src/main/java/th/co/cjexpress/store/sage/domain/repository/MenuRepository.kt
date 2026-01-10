package th.co.cjexpress.store.sage.domain.repository

import th.co.cjexpress.store.sage.domain.model.ModuleMenuItem

interface MenuRepository {
    fun getModuleMenuItems(): List<ModuleMenuItem>
}
