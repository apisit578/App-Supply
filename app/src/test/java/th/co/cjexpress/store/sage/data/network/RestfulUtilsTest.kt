package th.co.cjexpress.store.sage.data.network

import org.junit.Assert.assertEquals
import org.junit.Test

class RestfulUtilsTest {
    @Test
    fun `replacePathParams should replace single path param correctly`() {
        val url = "/api/user/{id}"
        val params = mapOf("id" to "123")

        val result = url.replacePathParams(params)

        assertEquals("/api/user/123", result)
    }

    @Test
    fun `replacePathParams should replace multiple path params correctly`() {
        val url = "/api/user/{userId}/post/{postId}"
        val params = mapOf("userId" to "42", "postId" to "1001")

        val result = url.replacePathParams(params)

        assertEquals("/api/user/42/post/1001", result)
    }

    @Test
    fun `replacePathParams should return same url if no params provided`() {
        val url = "/api/test"
        val params = emptyMap<String, String>()

        val result = url.replacePathParams(params)

        assertEquals("/api/test", result)
    }

    @Test
    fun `test getSafeRequestTimeout with timeout is greater than 1000ms should subtract timeout 1000ms`() {
        assertEquals(4000, getSafeRequestTimeout(5000))
    }

    @Test
    fun `test getSafeRequestTimeout with timeout is null should use default timeout`() {
        assertEquals(179000, getSafeRequestTimeout(null)) // 180000 - 1000
    }

    @Test
    fun `test getSafeRequestTimeout with timeout is less than 1000ms should coerce timeout to 1000ms`() {
        assertEquals(1000, getSafeRequestTimeout(500))
    }

    @Test
    fun `test getSafeRequestTimeout with timeout is exactly 1000ms should return timeout 1000ms`() {
        assertEquals(1000, getSafeRequestTimeout(1000))
    }
}