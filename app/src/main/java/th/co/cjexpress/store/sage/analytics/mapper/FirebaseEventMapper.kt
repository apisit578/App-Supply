package th.co.cjexpress.store.sage.analytics.mapper

import android.os.Bundle
import jakarta.inject.Inject
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.APP_OPENED_EVENT
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.LOGIN_EVENT
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.ORDER_RECEIVE_COMPLETED_EVENT
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PARAM_APP_VERSION
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PARAM_DURATION
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PARAM_USER_ID
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PRESS_START_RECEIVE_EVENT

class FirebaseEventMapper
    @Inject
    constructor() : AnalyticsEventMapper<Bundle> {
        override fun map(event: AnalyticsEvent): Pair<String, Bundle> {
            val eventName = getEventName(event)
            val paramsBundle = mapEventParams(event)
            return eventName to paramsBundle
        }

        private fun getEventName(event: AnalyticsEvent): String =
            when (event) {
                is AnalyticsEvent.AppOpened -> APP_OPENED_EVENT
                is AnalyticsEvent.OrderReceiveCompleted -> ORDER_RECEIVE_COMPLETED_EVENT
                is AnalyticsEvent.Login -> LOGIN_EVENT
                is AnalyticsEvent.PressStartReceive -> PRESS_START_RECEIVE_EVENT
            }

        private fun mapEventParams(event: AnalyticsEvent): Bundle =
            when (event) {
                is AnalyticsEvent.AppOpened -> {
                    Bundle().apply {
                        putString(PARAM_APP_VERSION, event.versionName)
                    }
                }

                is AnalyticsEvent.OrderReceiveCompleted -> {
                    Bundle().apply {
                        putLong(PARAM_DURATION, event.duration)
                    }
                }

                is AnalyticsEvent.Login -> {
                    Bundle().apply {
                        putString(PARAM_USER_ID, event.id)
                    }
                }

                is AnalyticsEvent.PressStartReceive -> {
                    Bundle().apply {
                        putString(PARAM_USER_ID, event.id)
                    }
                }
            }
    }
