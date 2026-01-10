package th.co.cjexpress.store.sage.utils

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import io.mockk.every
import io.mockk.mockk
import junit.framework.TestCase.assertEquals
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class JsonUtilsTest {
    private lateinit var gson: Gson
    private lateinit var jsonUtils: JsonUtils

    @Before
    fun setUp() {
        gson = Gson()
        jsonUtils = JsonUtils(gson)
    }

    @Test
    fun `toJson should serialize object`() {
        val user = User("Alice", 30)
        val expectedJson = """{"name":"Alice","age":30}"""

        val json = jsonUtils.toJson(user)

        assertEquals(expectedJson, json)
    }

    @Test
    fun `toJson should return empty string when gson throws`() {
        val mockGson = mockk<Gson>()
        every { mockGson.toJson(any()) } throws RuntimeException("boom")

        val utils = JsonUtils(mockGson)
        val result = utils.toJson(User("Bob", 25))

        assertEquals("", result)
    }

    @Test
    fun `toJsonAsync should return correct json`() =
        runBlocking {
            val user = User("Carol", 22)
            val expectedJson = """{"name":"Carol","age":22}"""

            val json = jsonUtils.toJsonAsync(user).await()

            assertEquals(expectedJson, json)
        }

    @Test
    fun `fromJson should deserialize to object`() {
        val json = """{"name":"Dave","age":40}"""

        val user = jsonUtils.fromJson(json, User::class.java)

        assertEquals(User("Dave", 40), user)
    }

    @Test
    fun `fromJson with Type should deserialize list`() {
        val json = """[{"name":"Eve","age":20},{"name":"Frank","age":21}]"""
        val type = object : TypeToken<List<User>>() {}.type

        val list: List<User> = jsonUtils.fromJson(json, type)

        assertEquals(2, list.size)
        assertEquals("Eve", list[0].name)
        assertEquals(21, list[1].age)
    }

    @Test
    fun `fromJsonAsync Class should return object`() =
        runTest {
            val json = """{"name":"Grace","age":28}"""

            val user = jsonUtils.fromJsonAsync(json, User::class.java).await()

            assertEquals(User("Grace", 28), user)
        }

    @Test
    fun `fromJsonAsync Type should return list`() =
        runTest {
            val json = """[{"name":"Hank","age":18}]"""
            val type = object : TypeToken<List<User>>() {}.type
            val list1 =
                jsonUtils
                    .fromJsonAsync<List<User>>(json, type)
                    .await()

            assertEquals(1, list1.size)
            assertEquals("Hank", list1[0].name)

            val deferred: kotlinx.coroutines.Deferred<List<User>> =
                jsonUtils.fromJsonAsync(json, type)
            val list2 = deferred.await()

            assertEquals(list1, list2)
        }

    private data class User(
        val name: String,
        val age: Int,
    )
}
