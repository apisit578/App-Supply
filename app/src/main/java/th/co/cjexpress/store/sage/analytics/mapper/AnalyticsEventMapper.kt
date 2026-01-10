package th.co.cjexpress.store.sage.analytics.mapper

import th.co.cjexpress.store.sage.analytics.AnalyticsEvent

interface AnalyticsEventMapper<T> {
    fun map(event: AnalyticsEvent): Pair<String, T>
}
