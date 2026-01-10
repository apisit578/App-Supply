package th.co.cjexpress.store.sage.data.config

import android.content.Context
import android.content.res.Resources
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.domain.model.PosJSONConfig
import th.co.cjexpress.store.sage.utils.JsonUtils
import java.io.InputStream

class ConfigLoaderTest {
    private lateinit var context: Context
    private lateinit var resources: Resources
    private lateinit var inputStream: InputStream
    private lateinit var jsonUtils: JsonUtils
    private lateinit var configLoader: ConfigLoader

    private val mockJson = """{ "someKey": "someValue" }"""
    private val mockConfig = mockk<PosJSONConfig>()

    @Before
    fun setUp() {
        context = mockk()
        resources = mockk()
        inputStream = mockk()
        jsonUtils = mockk()

        every { context.resources } returns resources
        every { resources.openRawResource(any()) } returns inputStream

        every { inputStream.available() } returns mockJson.toByteArray().size
        every { inputStream.read(any<ByteArray>()) } answers {
            val buffer = it.invocation.args[0] as ByteArray
            System.arraycopy(mockJson.toByteArray(), 0, buffer, 0, mockJson.length)
            mockJson.length
        }

        every { jsonUtils.fromJson(mockJson, PosJSONConfig::class.java) } returns mockConfig

        configLoader = ConfigLoader(context, jsonUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `jsonConfig should load and parse JSON successfully`() {
        val result = configLoader.jsonConfig

        assertEquals(mockConfig, result)
        verify(exactly = 1) { jsonUtils.fromJson(mockJson, PosJSONConfig::class.java) }
    }

    @Test
    fun `jsonConfig should cache result after first load`() {
        val firstCall = configLoader.jsonConfig
        val secondCall = configLoader.jsonConfig

        assertSame(firstCall, secondCall)
        verify(exactly = 1) { jsonUtils.fromJson(mockJson, PosJSONConfig::class.java) }
    }

    @Test
    fun `jsonConfig should return null if exception occurs`() {
        every { resources.openRawResource(any()) } throws Resources.NotFoundException("Not found")

        val loaderWithError = ConfigLoader(context, jsonUtils)
        val result = runCatching { loaderWithError.jsonConfig }.getOrNull()

        assertNull(result)
    }
}
