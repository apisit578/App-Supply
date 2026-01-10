package th.co.cjexpress.store.sage.analytics

import com.google.firebase.analytics.FirebaseAnalytics

enum class ProcessTimeTrackingType {
    ORDER_RECEIVE_PROCESS,
}

enum class ProcessStatusType {
    IN_PROCESS,
    DONE,
    OTHER,
}

sealed class AnalyticsEvent {
    companion object {
        // event name
        const val APP_OPENED_EVENT = "app_opened"
        const val ORDER_RECEIVE_COMPLETED_EVENT = "order_receive_completed"
        const val LOGIN_EVENT = FirebaseAnalytics.Event.LOGIN
        const val PRESS_START_RECEIVE_EVENT = "press_start_receive"

        // param
        const val PARAM_APP_VERSION = "app_version"
        const val PARAM_DURATION = "duration_ms"
        const val PARAM_USER_ID = "user_id"
    }

    data class AppOpened(
        val versionName: String,
    ) : AnalyticsEvent()

    data class OrderReceiveCompleted(
        val duration: Long,
    ) : AnalyticsEvent()

    data class Login(
        val id: String,
    ) : AnalyticsEvent()

    data class PressStartReceive(
        val id: String,
    ) : AnalyticsEvent()
}
