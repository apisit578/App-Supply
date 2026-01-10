package th.co.cjexpress.store.sage.utils

import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.spyk
import io.mockk.unmockkAll
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.source.remote.FirestoreDataSource
import th.co.cjexpress.store.sage.di.AppDependencyProvider

@OptIn(ExperimentalCoroutinesApi::class)
open class BaseUnitTest {
    val testDispatcher = UnconfinedTestDispatcher()

    lateinit var logUtils: LogUtils
    private val firestoreDataSource: FirestoreDataSource = mockk()
    private val mockAppConfig: AppConfig = mockk()

    @Before
    open fun setUp() {
        mockLogUtils()
        MockKAnnotations.init(this)
        Dispatchers.setMain(testDispatcher)
    }

    @After
    open fun tearDown() {
        clearAllMocks()
        unmockkAll()
        Dispatchers.resetMain()
    }

    fun mockLogUtils() {
        every { mockAppConfig.getBranchInfo()?.branchCode } returns "M04C"
        every { firestoreDataSource.insertHandheldLog(any()) } returns Unit
        logUtils =
            spyk(
                LogUtils(
                    appConfig = mockAppConfig,
                    dataSource = firestoreDataSource,
                ),
            )
        mockkObject(AppDependencyProvider)
        every { AppDependencyProvider.getProviderBuilder().getLogManager() } returns logUtils
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
class TestDispatchers : DispatcherProvider {
    val testDispatcher = UnconfinedTestDispatcher()
    override val main: CoroutineDispatcher get() = testDispatcher
    override val io: CoroutineDispatcher get() = testDispatcher
    override val default: CoroutineDispatcher get() = testDispatcher
}
