package th.co.cjexpress.store.sage.data.repository

import kotlinx.coroutines.delay
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.Logging
import th.co.cjexpress.store.sage.constants.SharePrefKeys
import th.co.cjexpress.store.sage.data.config.ConfigLoader
import th.co.cjexpress.store.sage.data.source.local.SharedPreferencesManager
import th.co.cjexpress.store.sage.data.source.remote.PayloadApi
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.repository.PayloadRepository
import timber.log.Timber
import java.io.IOException
import java.net.HttpURLConnection

class PayloadRepositoryImpl(
    private val payloadApi: PayloadApi,
    private val sharedPrefManager: SharedPreferencesManager,
    private val configLoader: ConfigLoader,
) : PayloadRepository {
    override suspend fun loadGlobalConfig(): PayloadConfig {
        val maxRetryCount = configLoader.jsonConfig.configMaxRetry
        val retryDelayMillis = configLoader.jsonConfig.configRetryDelay

        for (attempt in 0..maxRetryCount) {
            try {
                val response = payloadApi.loadConfigByBranch(configLoader.jsonConfig.globalPath)
                if (response.isSuccessful) {
                    Timber
                        .tag(Logging.TAG_PAYLOAD_CONFIG)
                        .i("load global config success")

                    return response.bodyToObject(PayloadConfig::class.java)
                        ?: throw Exception("ข้อมูลการตั้งค่าหลักของแอปไม่ถูกต้อง")
                } else {
                    Timber
                        .tag(Logging.TAG_PAYLOAD_CONFIG)
                        .e("loadGlobalConfig attempt $attempt failed")

                    throw IOException("ไม่สามารถโหลดการตั้งค่าหลักของแอปได้ในเวลาที่กำหนด")
                }
            } catch (e: Exception) {
                if (attempt == maxRetryCount) {
                    throw e
                } else {
                    delay(retryDelayMillis)
                }
            }
        }

        throw Exception("เกิดข้อผิดพลาดในขั้นตอนโหลดการตั้งค่าหลักของแอป")
    }

    override suspend fun loadConfigByBranch(branchCode: String): PayloadConfig {
        val maxRetryCount = configLoader.jsonConfig.configMaxRetry
        val retryDelayMillis = configLoader.jsonConfig.configRetryDelay

        for (attempt in 0..maxRetryCount) {
            try {
                val response = payloadApi.loadConfigByBranch(branchCode)
                if (response.isSuccessful) {
                    Timber.tag(Logging.TAG_PAYLOAD_CONFIG).i("load branch config success")
                    return response.bodyToObject(PayloadConfig::class.java)
                        ?: throw Exception("ข้อมูลการตั้งค่าของสาขา $branchCode ไม่ถูกต้อง")
                } else {
                    Timber
                        .tag(Logging.TAG_PAYLOAD_CONFIG)
                        .e("loadConfigByBranch attempt $attempt failed")
                    if (response.httpStatus == HttpURLConnection.HTTP_NOT_FOUND) {
                        throw IOException(ErrorCodes.NOT_FOUND)
                    } else {
                        throw IOException("ไม่สามารถโหลดการตั้งค่าของสาขา $branchCode ได้ในเวลาที่กำหนด")
                    }
                }
            } catch (e: Exception) {
                if (attempt == maxRetryCount) {
                    Timber
                        .tag(Logging.TAG_PAYLOAD_CONFIG)
                        .e("loadConfigByBranch : ${e.message}")
                    throw e
                } else {
                    delay(retryDelayMillis)
                }
            }
        }

        throw Exception("เกิดข้อผิดพลาดในขั้นตอนโหลดการตั้งค่าของสาขา $branchCode")
    }

    override suspend fun saveGlobalConfig(config: PayloadConfig) {
        sharedPrefManager.save(SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY, config)
    }

    override suspend fun saveBranchConfig(config: PayloadConfig) {
        sharedPrefManager.save(SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY, config)
    }

    override suspend fun loadGlobalCacheConfig(): PayloadConfig? =
        loadCacheConfig(SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY, PayloadConfig::class.java)

    override suspend fun loadBranchCacheConfig(): PayloadConfig? =
        loadCacheConfig(SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY, PayloadConfig::class.java)

    private fun <T> loadCacheConfig(
        key: String,
        clazz: Class<T>,
    ): T? =
        try {
            sharedPrefManager.get(key, clazz)
        } catch (e: Exception) {
            Timber.tag(Logging.TAG_PAYLOAD_CONFIG).e("load $key from cache error, ${e.message}")
            null
        }
}
