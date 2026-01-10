package th.co.cjexpress.store.sage.analytics

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.analytics.usecase.TrackTimeEventUseCaseImpl
import th.co.cjexpress.store.sage.domain.livedata.analytics.AnalyticTimeTrackingLiveData
import th.co.cjexpress.store.sage.domain.usecase.orderReceive.MenuOrderReceiveUseCase
import th.co.cjexpress.store.sage.utils.BaseUnitTest

class TrackTimeEventUseCaseImplTest : BaseUnitTest() {
    private lateinit var useCase: TrackTimeEventUseCaseImpl

    private val mockMenuOrderReceiveUseCase = mockk<MenuOrderReceiveUseCase>()
    private val mockAnalyticTimeTrackingLiveData = mockk<AnalyticTimeTrackingLiveData>(relaxed = true)
    private val mockAnalyticsTracker = mockk<AnalyticsTracker>(relaxed = true)

    @Before
    override fun setUp() {
        useCase =
            TrackTimeEventUseCaseImpl(
                mockMenuOrderReceiveUseCase,
                mockAnalyticTimeTrackingLiveData,
                mockAnalyticsTracker,
            )
    }

    @Test
    fun `track should add track process when enabled for all statuses`() {
        // Given
        every { mockMenuOrderReceiveUseCase.enableAnalyticsTimeTracking() } returns true

        // When
        ProcessStatusType.entries.forEach { status ->
            useCase.track(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS, status)
        }

        // Then
        ProcessStatusType.entries.forEach { status ->
            verify {
                mockAnalyticTimeTrackingLiveData.addTrackProcess(
                    processName = ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS.name,
                    status = status,
                )
            }
        }
    }

    @Test
    fun `track should not add track process when disabled`() {
        // Given
        every { mockMenuOrderReceiveUseCase.enableAnalyticsTimeTracking() } returns false

        // When
        useCase.track(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS, ProcessStatusType.IN_PROCESS)

        // Then
        verify(exactly = 0) {
            mockAnalyticTimeTrackingLiveData.addTrackProcess(any(), any())
        }
    }

    @Test
    fun `stop should log event and remove tracking when process is done`() {
        // Given
        val startTime = 1_000L
        every { mockAnalyticTimeTrackingLiveData.isDone(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS.name) } returns true
        every { mockAnalyticTimeTrackingLiveData.getStartTime(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS.name) } returns startTime

        // When
        useCase.stop(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS)

        // Then
        verify {
            mockAnalyticsTracker.logEvent(
                match {
                    (it as AnalyticsEvent.OrderReceiveCompleted).duration.isNotEmpty()
                },
            )
            mockAnalyticTimeTrackingLiveData.removeTracking(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS.name)
        }
    }

    @Test
    fun `stop should not log event when process is not done`() {
        // Given
        every { mockAnalyticTimeTrackingLiveData.isDone(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS.name) } returns false

        // When
        useCase.stop(ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS)

        // Then
        verify(exactly = 0) {
            mockAnalyticsTracker.logEvent(any())
            mockAnalyticTimeTrackingLiveData.removeTracking(any())
        }
    }
}
