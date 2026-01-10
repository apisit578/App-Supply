package th.co.cjexpress.store.sage.analytics

import io.mockk.mockk
import io.mockk.verify
import org.junit.Test
import th.co.cjexpress.store.sage.analytics.usecase.TrackAppOpenedEventUseCaseImpl

class TrackAppOpenedEventUseCaseTest {
    private val mockTracker = mockk<AnalyticsTracker>(relaxed = true)
    private val useCase = TrackAppOpenedEventUseCaseImpl(mockTracker)

    @Test
    fun `execute should log AppOpened event with correct version`() {
        // Given
        val versionName = "1.5.3.5.6.7.7.7"

        // When
        useCase.execute(versionName)

        // Then
        verify {
            mockTracker.logEvent(AnalyticsEvent.AppOpened(versionName))
        }
    }
}
