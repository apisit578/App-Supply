package th.co.cjexpress.store.sage.utils

interface CrashReporter {
    fun setUserId(id: String)

    fun setCustomKey(
        key: String,
        value: String,
    )
}

class CrashlyticsCrashReporter : CrashReporter {
    override fun setUserId(id: String) {
        CrashlyticsLoggerUtils.setUserId(id)
    }

    override fun setCustomKey(
        key: String,
        value: String,
    ) {
        CrashlyticsLoggerUtils.setCustomKey(key, value)
    }
}
