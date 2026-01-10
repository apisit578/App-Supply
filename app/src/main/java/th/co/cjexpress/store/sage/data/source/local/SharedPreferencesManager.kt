package th.co.cjexpress.store.sage.data.source.local

import android.content.Context
import androidx.core.content.edit
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.utils.JsonUtils
import java.io.Serializable
import java.lang.reflect.Type

class SharedPreferencesManager(
    context: Context,
    private val jsonUtils: JsonUtils,
) {
    private val masterKeyAlias =
        MasterKey
            .Builder(context, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()

    private val sharedPreferences =
        EncryptedSharedPreferences.create(
            context,
            context.packageName + "_preferences",
            masterKeyAlias,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM,
        )

    fun get(key: String): String? {
        println("ShareManager : Get Key=[$key]")
        if (sharedPreferences.contains(key)) {
            return sharedPreferences.getString(key, null)
        }
        return null
    }

    fun <T> get(
        key: String,
        clazz: Class<T>,
    ): T? {
        println("ShareManager : Get Key=[$key]")
        if (sharedPreferences.contains(key)) {
            val jSon = sharedPreferences.getString(key, "")
            if (!jSon.isNullOrEmpty()) {
                return jsonUtils.fromJson(jSon, clazz)
            }
        }
        return null
    }

    fun <T> getAsync(
        key: String,
        clazz: Class<T>,
    ): Deferred<T?> =
        CoroutineScope(Dispatchers.Unconfined).async {
            if (sharedPreferences.contains(key)) {
                val jSon = sharedPreferences.getString(key, "")
                if (!jSon.isNullOrEmpty()) {
                    return@async jsonUtils.fromJson(jSon, clazz)
                }
            }
            return@async null
        }

    fun <T> get(
        key: String,
        typeToken: Type,
    ): T? {
        if (sharedPreferences.contains(key)) {
            val jSon = sharedPreferences.getString(key, "")
            if (!jSon.isNullOrEmpty()) {
                return jsonUtils.fromJson(jSon, typeToken)
            }
        }
        return null
    }

    fun <T> getAsync(
        key: String,
        typeToken: Type,
    ): Deferred<T?> =
        CoroutineScope(Dispatchers.Unconfined).async {
            if (sharedPreferences.contains(key)) {
                val jSon = sharedPreferences.getString(key, "")
                if (!jSon.isNullOrEmpty()) {
                    return@async jsonUtils.fromJson(jSon, typeToken)
                }
            }
            return@async null
        }

    fun save(
        key: String,
        data: Serializable,
    ) = CoroutineScope(Dispatchers.Unconfined).launch {
        println("ShareManager : Write Key=[$key]")
        try {
            sharedPreferences.edit {
                if (sharedPreferences.contains(key)) {
                    remove(key)
                }
                val jsonString = jsonUtils.toJson(data)
                putString(key, jsonString)
                apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // logError(e)
        }
    }

    fun save(
        key: String,
        data: String,
    ) = CoroutineScope(Dispatchers.Unconfined).launch {
        println("ShareManager : Write Key=[$key]")
        try {
            sharedPreferences.edit {
                if (sharedPreferences.contains(key)) {
                    remove(key)
                }
                putString(key, data)
                apply()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            // logError(e)
        }
    }

    fun delete(key: String) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            sharedPreferences.edit {
                remove(key)
                apply()
            }
        }

    fun removeKeyIfExists(key: String) =
        CoroutineScope(Dispatchers.Unconfined).launch {
            if (sharedPreferences.contains(key)) {
                sharedPreferences.edit {
                    remove(key)
                    apply()
                }
            }
        }
}
