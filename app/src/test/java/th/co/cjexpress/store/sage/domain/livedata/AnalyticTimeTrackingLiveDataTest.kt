package th.co.cjexpress.store.sage.domain.livedata

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertFalse
import junit.framework.TestCase.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import th.co.cjexpress.store.sage.analytics.AnalyticsEvent
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.domain.livedata.analytics.AnalyticTimeTrackingLiveData

class AnalyticTimeTrackingLiveDataTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private lateinit var analyticLiveData: AnalyticTimeTrackingLiveData

    @Before
    fun setUp() {
        analyticLiveData = AnalyticTimeTrackingLiveData()
    }

    @Test
    fun `addTrackProcess should add new process`() {
        // given
        val processName = AnalyticsEvent.ORDER_RECEIVE_COMPLETED_EVENT
        val status = ProcessStatusType.IN_PROCESS

        // when
        analyticLiveData.addTrackProcess(processName, status)

        // then
        val tracked = analyticLiveData.getStatus(processName)
        assertEquals(status, tracked)
        val startTime = analyticLiveData.getStartTime(processName)
        assertTrue(startTime > 0)
    }

    @Test
    fun `updateProcessStatus should change status of existing process`() {
        // given
        val processName = AnalyticsEvent.ORDER_RECEIVE_COMPLETED_EVENT

        // when
        analyticLiveData.addTrackProcess(processName, ProcessStatusType.IN_PROCESS)
        analyticLiveData.updateProcessStatus(processName, ProcessStatusType.DONE)

        // then
        val status = analyticLiveData.getStatus(processName)
        assertEquals(ProcessStatusType.DONE, status)
    }

    @Test
    fun `removeTracking should remove process`() {
        // given
        val processName = AnalyticsEvent.ORDER_RECEIVE_COMPLETED_EVENT
        analyticLiveData.addTrackProcess(processName, ProcessStatusType.IN_PROCESS)

        // when
        analyticLiveData.removeTracking(processName)

        // then
        val status = analyticLiveData.getStatus(processName)
        assertEquals(ProcessStatusType.OTHER, status)
    }

    @Test
    fun `getStartTime should return current time if process not found`() {
        // given
        val currentTimeBefore = System.currentTimeMillis()
        val startTime = analyticLiveData.getStartTime("UNKNOWN")
        val currentTimeAfter = System.currentTimeMillis()

        // then
        assertTrue(startTime in currentTimeBefore..currentTimeAfter)
    }

    @Test
    fun `isDone should return true when status is DONE`() {
        // given
        val processName = AnalyticsEvent.ORDER_RECEIVE_COMPLETED_EVENT

        // when
        analyticLiveData.addTrackProcess(processName, ProcessStatusType.DONE)

        // then
        assertTrue(analyticLiveData.isDone(processName))
    }

    @Test
    fun `isDone should return false when status is not DONE`() {
        // given
        val processName = AnalyticsEvent.ORDER_RECEIVE_COMPLETED_EVENT

        // when
        analyticLiveData.addTrackProcess(processName, ProcessStatusType.IN_PROCESS)

        // then
        assertFalse(analyticLiveData.isDone(processName))
    }

    @Test
    fun `addTrackProcess should update status if process already exists`() {
        // given
        val processName = AnalyticsEvent.ORDER_RECEIVE_COMPLETED_EVENT
        analyticLiveData.addTrackProcess(processName, ProcessStatusType.IN_PROCESS)

        // when: add same process again with different status
        analyticLiveData.addTrackProcess(processName, ProcessStatusType.DONE)

        // then
        val status = analyticLiveData.getStatus(processName)
        assertEquals(ProcessStatusType.DONE, status)
    }

    @Test
    fun `getStatus should return OTHER when trackedProcesses is empty`() {
        val status = analyticLiveData.getStatus("UNKNOWN_PROCESS")
        assertEquals(ProcessStatusType.OTHER, status)
    }

    @Test
    fun `getStartTime should return current time when process not found`() {
        val startTimeBefore = System.currentTimeMillis()
        val startTime = analyticLiveData.getStartTime("UNKNOWN_PROCESS")
        val startTimeAfter = System.currentTimeMillis()

        assertTrue(startTime in startTimeBefore..startTimeAfter)
    }
}
