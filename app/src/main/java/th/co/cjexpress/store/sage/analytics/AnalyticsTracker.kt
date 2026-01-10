package th.co.cjexpress.store.sage.analytics

interface AnalyticsTracker {
    fun logEvent(event: AnalyticsEvent)
}
