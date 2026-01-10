package th.co.cjexpress.store.sage.domain.usecase

import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class GetAppConfigUseCase(
    private val configRepository: ConfigRepository,
) {
    fun getAppConfig(): AppConfig = configRepository.getAppConfig()

    fun getBranchInfo(): BranchInfoModel? {
        val config = configRepository.getAppConfig()
        return config.getBranchInfo()
    }
}
