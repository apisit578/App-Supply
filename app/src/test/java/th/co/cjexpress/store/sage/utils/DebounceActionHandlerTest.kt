package th.co.cjexpress.store.sage.utils

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DebounceActionHandlerTest : BaseUnitTest() {

    private var actionCallCount = 0

    @Before
    fun setup() {
        actionCallCount = 0
    }

    private suspend fun mockAction() {
        actionCallCount++
        delay(50)
    }

    @Test
    fun `execute should execute action immediately on first call`() = runTest {
        val debounceDuration = 300L
        val handler = DebounceActionHandler(
            debounceDuration = debounceDuration,
            coroutineScope = this
        )

        handler.execute { mockAction() }

        advanceUntilIdle()
        assertEquals(1, actionCallCount)

    }

    @Test
    fun `execute should ignore rapid calls within debounce duration`() = runTest {
        val debounceDuration = 300L
        val handler = DebounceActionHandler(
            debounceDuration = debounceDuration,
            coroutineScope = this
        )

        handler.execute { mockAction() } // Call 1 (Action executes)
        handler.execute { mockAction() } // Call 2 (Should be ignored)
        handler.execute { mockAction() } // Call 3 (Should be ignored)
        advanceUntilIdle()
        assertEquals(1, actionCallCount)

        // Fast-forward time but not over debounceDuration
        advanceTimeBy(debounceDuration - 100) // 200ms
        advanceUntilIdle()
        assertEquals(1, actionCallCount)
    }

    @Test
    fun `execute should execute action again after debounce duration has passed`() = runTest {
        val debounceDuration = 300L
        val handler = DebounceActionHandler(
            debounceDuration = debounceDuration,
            coroutineScope = this
        )

        handler.execute { mockAction() } // Call 1 (Action executes)
        advanceUntilIdle()
        assertEquals(1, actionCallCount)

        // Fast-forward time beyond debounceDuration
        advanceTimeBy(debounceDuration + 50)
        handler.execute { mockAction() } // Call 2
        advanceUntilIdle()
        assertEquals(2, actionCallCount)

        // Fast-forward time beyond debounceDuration
        advanceTimeBy(debounceDuration + 50)
        handler.execute { mockAction() } // Call 3
        advanceUntilIdle()
        assertEquals(3, actionCallCount)
    }
}