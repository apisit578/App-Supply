package th.co.cjexpress.store.sage.utils

import junit.framework.TestCase.assertEquals
import junit.framework.TestCase.assertNull
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorType

class ApiUtilsTest {
    @Test
    fun `return Error with code and message`() {
        val result = handleResponseError("401", "Unauthorized", "123")

        val cause = result.cause!!
        assertEquals("401", cause.errorCode)
        assertEquals(ErrorType.ERROR, cause.type)
        assertEquals("Unauthorized", cause.message)
        assertNull(result.data)
    }

    @Test
    fun `null code should become empty string`() {
        val result = handleResponseError(null, "Bad Request", "123")

        val cause = result.cause!!
        assertEquals("", cause.errorCode)
        assertEquals("Bad Request", cause.message)
    }

    @Test
    fun `null message is allowed`() {
        val result = handleResponseError("500", null, "123")

        val cause = result.cause!!
        assertEquals("500", cause.errorCode)
        assertNull(cause.message)
    }
}
