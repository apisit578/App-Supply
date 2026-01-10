package th.co.cjexpress.store.sage.utils

import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import java.lang.reflect.Type

class JsonUtils(private val gson: Gson) {
    fun toJson(clazz: Any): String {
        return try {
            gson.toJson(clazz)
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
    }

    fun toJsonAsync(clazz: Any): Deferred<String> =
        CoroutineScope(Dispatchers.Unconfined).async {
            return@async gson.toJson(clazz)
        }

    fun <T> fromJson(
        jsonString: String,
        clazz: Class<T>,
    ): T {
        return gson.fromJson<T>(jsonString, clazz)
    }

    fun <T> fromJson(
        jsonString: String,
        type: Type,
    ): T {
        return gson.fromJson<T>(jsonString, type)
    }

    fun <T> fromJsonAsync(
        jsonString: String,
        clazz: Class<T>,
    ): Deferred<T> =
        CoroutineScope(Dispatchers.Unconfined).async {
            return@async gson.fromJson<T>(jsonString, clazz)
        }

    fun <T> fromJsonAsync(
        jsonString: String,
        type: Type,
    ): Deferred<T> =
        CoroutineScope(Dispatchers.Unconfined).async {
            return@async gson.fromJson<T>(jsonString, type)
        }
}
