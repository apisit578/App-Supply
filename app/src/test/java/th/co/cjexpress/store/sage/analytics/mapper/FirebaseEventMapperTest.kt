package th.co.cjexpress.store.sage.analytics.mapper

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNotNull
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.APP_OPENED_EVENT
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.ORDER_RECEIVE_COMPLETED_EVENT
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PARAM_APP_VERSION
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent.Companion.PARAM_DURATION

@RunWith(RobolectricTestRunner::class)
class FirebaseEventMapperTest {
    private lateinit var mapper: FirebaseEventMapper

    @Before
    fun setUp() {
        mapper = FirebaseEventMapper()
    }

    @Test
    fun `map should return correct name and bundle for AppOpened`() {
        // given
        val versionName = "1.0.0"
        val event = AnalyticsEvent.AppOpened(versionName)

        // when
        val (name, bundle) = mapper.map(event)

        // then
        assertEquals(APP_OPENED_EVENT, name)
        assertNotNull(bundle)
        assertEquals(versionName, bundle.getString(PARAM_APP_VERSION))
    }

    @Test
    fun `map should return correct name and bundle for OrderReceiveCompleted`() {
        // given
        val duration = "500ms"
        val event = AnalyticsEvent.OrderReceiveCompleted(duration)

        // when
        val (name, bundle) = mapper.map(event)

        // then
        assertEquals(ORDER_RECEIVE_COMPLETED_EVENT, name)
        assertNotNull(bundle)
        assertEquals(duration, bundle.getString(PARAM_DURATION))
    }
}
