package th.co.cjexpress.store.sage.utils

import junit.framework.TestCase.assertSame
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class DefaultDispatchersTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val dispatchers: DispatcherProvider = DefaultDispatchers()

    @Test
    fun `main should return Dispatchers_Main`() {
        assertSame(Dispatchers.Main, dispatchers.main)
    }

    @Test
    fun `io should return Dispatchers_IO`() {
        assertSame(Dispatchers.IO, dispatchers.io)
    }

    @Test
    fun `default should return Dispatchers_Default`() {
        assertSame(Dispatchers.Default, dispatchers.default)
    }
}
