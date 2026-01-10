package th.co.cjexpress.store.sage.utils

import com.google.gson.JsonElement
import com.google.gson.JsonParser
import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Before
import org.junit.Test

class CustomBooleanTypeAdapterTest {
    private lateinit var adapter: CustomBooleanTypeAdapter

    @Before
    fun setUp() {
        adapter = CustomBooleanTypeAdapter()
    }

    private fun parse(json: String): JsonElement = JsonParser.parseString(json.trim())

    @Test
    fun `deserialize boolean true primitive`() {
        val result = adapter.deserialize(parse("true"), null, null)
        assertEquals(true, result)
    }

    @Test
    fun `deserialize boolean false primitive`() {
        val result = adapter.deserialize(parse("false"), null, null)
        assertEquals(false, result)
    }

    @Test
    fun `deserialize string true (mixed case)`() {
        val result = adapter.deserialize(parse("\"TrUe\""), null, null)
        assertEquals(true, result)
    }

    @Test
    fun `deserialize string false`() {
        val result = adapter.deserialize(parse("\"false\""), null, null)
        assertEquals(false, result)
    }

    @Test
    fun `deserialize string unknown should return null`() {
        val result = adapter.deserialize(parse("\"yes\""), null, null)
        assertNull(result)
    }

    @Test
    fun `deserialize number 1 should be true`() {
        val result = adapter.deserialize(parse("1"), null, null)
        assertEquals(true, result)
    }

    @Test
    fun `deserialize number 0 should be false`() {
        val result = adapter.deserialize(parse("0"), null, null)
        assertEquals(false, result)
    }

    @Test
    fun `deserialize number other should return null`() {
        val result = adapter.deserialize(parse("2"), null, null)
        assertNull(result)
    }
}
