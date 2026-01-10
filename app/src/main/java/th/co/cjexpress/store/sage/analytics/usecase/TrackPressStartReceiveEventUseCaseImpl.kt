package th.co.cjexpress.store.sage.analytics.usecase

import jakarta.inject.Inject
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent
import th.co.cjexpress.store.sage.analytics.AnalyticsTracker
import th.co.cjexpress.store.sage.data.config.AppConfig

class TrackPressStartReceiveEventUseCaseImpl
    @Inject
    constructor(
        private val analyticsTracker: AnalyticsTracker,
        private val appConfig: AppConfig,
    ) : TrackPressStartReceiveEventUseCase {
        override fun execute() {
            analyticsTracker.logEvent(
                event =
                    AnalyticsEvent.PressStartReceive(
                        id = appConfig.username ?: "",
                    ),
            )
        }
    }
