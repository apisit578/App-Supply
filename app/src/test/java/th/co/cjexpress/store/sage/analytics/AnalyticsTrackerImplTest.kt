package th.co.cjexpress.store.sage.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.analytics.mapper.FirebaseEventMapper
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class AnalyticsTrackerImplTest : BaseUnitTest() {
    private lateinit var firebaseAnalytics: FirebaseAnalytics
    private lateinit var firebaseEventMapper: FirebaseEventMapper
    private lateinit var analyticsTracker: AnalyticsTracker

    @Before
    override fun setUp() {
        firebaseAnalytics = mockk(relaxed = true)
        firebaseEventMapper = mockk()
        analyticsTracker = AnalyticsTrackerImpl(firebaseAnalytics, firebaseEventMapper)
    }

    @Test
    fun `logEvent should map LoginOpened event and log to Firebase`() {
        // Arrange
        val event =
            AnalyticsEvent.AppOpened(
                versionName = "1.5.0",
            )
        val expectedBundle = mockk<Bundle>(relaxed = true)

        every { firebaseEventMapper.map(event) } returns (AnalyticsEvent.APP_OPENED_EVENT to expectedBundle)

        // Act
        analyticsTracker.logEvent(event)

        // Assert
        verify { firebaseEventMapper.map(event) }
        verify { firebaseAnalytics.logEvent(AnalyticsEvent.APP_OPENED_EVENT, expectedBundle) }
    }
}
