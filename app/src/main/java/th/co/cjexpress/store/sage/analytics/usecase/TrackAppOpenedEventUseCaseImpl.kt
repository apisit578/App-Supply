package th.co.cjexpress.store.sage.analytics.usecase

import jakarta.inject.Inject
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent
import th.co.cjexpress.store.sage.analytics.AnalyticsTracker

class TrackAppOpenedEventUseCaseImpl
    @Inject
    constructor(
        private val analyticsTracker: AnalyticsTracker,
    ) : TrackAppOpenedEventUseCase {
        override fun execute(versionName: String) {
            analyticsTracker.logEvent(
                event =
                    AnalyticsEvent.AppOpened(
                        versionName = versionName,
                    ),
            )
        }
    }
