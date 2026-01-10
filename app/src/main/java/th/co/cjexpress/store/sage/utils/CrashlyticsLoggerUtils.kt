package th.co.cjexpress.store.sage.utils

import com.google.firebase.crashlytics.FirebaseCrashlytics

object CrashlyticsLoggerUtils {
    private val crashlytics = FirebaseCrashlytics.getInstance()

    fun initCrashlytics() {
        crashlytics.isCrashlyticsCollectionEnabled = true
        crashlytics.sendUnsentReports()
    }

    fun setUserId(user: String) {
        crashlytics.setUserId(user)
    }

    fun setCustomKey(
        key: String,
        value: String,
    ) {
        crashlytics.setCustomKey(key, value)
    }

    fun logException(throwable: Throwable) {
        crashlytics.recordException(throwable)
    }
}
