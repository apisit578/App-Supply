package th.co.cjexpress.store.sage.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import jakarta.inject.Inject
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.APP_OPENED_EVENT
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.LOGIN_EVENT
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PARAM_APP_VERSION
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PARAM_USER_ID
import th.co.cjexpress.store.sage.analytics.mapper.FirebaseEventMapper

class AnalyticsTrackerImpl
    @Inject
    constructor(
        private val firebaseAnalytics: FirebaseAnalytics,
        private val firebaseEventMapper: FirebaseEventMapper,
    ) : AnalyticsTracker {
        override fun logEvent(event: AnalyticsEvent) {
            // Map AnalyticsEvent -> Pair(eventName, eventParamsBundle)
            val (name, bundle) = firebaseEventMapper.map(event)

            // set property
            when (name) {
                APP_OPENED_EVENT -> {
                    firebaseAnalytics.setUserProperty(
                        PARAM_APP_VERSION,
                        bundle.getString(PARAM_APP_VERSION),
                    )
                }
                LOGIN_EVENT -> {
                    firebaseAnalytics.setUserId(bundle.getString(PARAM_USER_ID))
                }
            }

            // log event firebase
            firebaseAnalytics.logEvent(name, bundle)
        }
    }
