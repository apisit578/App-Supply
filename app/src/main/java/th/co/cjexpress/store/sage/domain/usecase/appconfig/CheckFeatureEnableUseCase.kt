package th.co.cjexpress.store.sage.domain.usecase.appconfig

import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class CheckFeatureEnableUseCase(private val configRepository: ConfigRepository) {
    fun execute(feature: String): Boolean {
        val config = configRepository.getAppConfig()
        return config.featureEnable[feature] ?: false
    }
}
