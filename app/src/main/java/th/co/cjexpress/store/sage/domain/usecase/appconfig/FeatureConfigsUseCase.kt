package th.co.cjexpress.store.sage.domain.usecase.appconfig

import th.co.cjexpress.store.sage.constants.DELAY_BEFORE_KEYBOARD_ON_DONE
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.constants.DELAY_KEYBOARD_ON_DONE_DEBOUNCE
import th.co.cjexpress.store.sage.domain.model.payload.features.DelayConfigs
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class FeatureConfigsUseCase(
    private val configRepository: ConfigRepository,
) {
    fun getTotePrefix(): List<String> =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.configs
            ?.totePrefix
            ?: listOf("T", "B")

    fun getDelayConfig(): DelayConfigs =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.configs
            ?.delay
            ?: DelayConfigs(
                defaultDelayMs = DELAY_DEFAULT_TIME,
                keyboardOnDoneDebounceMs = DELAY_KEYBOARD_ON_DONE_DEBOUNCE,
                beforeKeyboardOnDone = DELAY_BEFORE_KEYBOARD_ON_DONE,
            )
}
