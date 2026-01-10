package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class MenuOrderReceiveUseCase(
    private val configRepository: ConfigRepository,
) {
    fun allowExtraItems() =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.menuOrderReceive
            ?.allowExtraItems
            ?: true

    fun allowOverReceivingQty() =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.menuOrderReceive
            ?.allowOverReceivingQty
            ?: true

    fun maxBatchQty() =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.menuOrderReceive
            ?.maxBatchQty
            ?: 0

    fun enableAnalyticsTimeTracking() =
        configRepository
            .getAppConfig()
            .payloadConfig
            ?.features
            ?.menuOrderReceive
            ?.enableAnalyticsTimeTracking
            ?: false
}
