package th.co.cjexpress.store.sage.data.repository

import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.Job
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.SharePrefKeys
import th.co.cjexpress.store.sage.data.config.ConfigLoader
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.data.source.local.SharedPreferencesManager
import th.co.cjexpress.store.sage.data.source.remote.PayloadApi
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig

class PayloadRepositoryImplTest {
    private lateinit var payloadApi: PayloadApi
    private lateinit var sharePrefManager: SharedPreferencesManager
    private lateinit var configLoader: ConfigLoader

    private lateinit var repository: PayloadRepositoryImpl

    @Before
    fun setUp() {
        payloadApi = mockk()
        sharePrefManager = mockk()
        configLoader = mockk()

        every { configLoader.jsonConfig.configMaxRetry } returns 3
        every { configLoader.jsonConfig.configRetryDelay } returns 3000L
        every { configLoader.jsonConfig.globalPath } returns "global_handheld"

        repository = PayloadRepositoryImpl(payloadApi, sharePrefManager, configLoader)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `loadGlobalConfig returns PayloadConfig when API response is successful`() =
        runTest {
            // given
            val payloadConfig = mockk<PayloadConfig>()
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.bodyToObject(PayloadConfig::class.java) } returns payloadConfig
            every { mockRestfulResponse.isSuccessful } returns true
            coEvery { payloadApi.loadConfigByBranch("global_handheld") } returns mockRestfulResponse

            // when
            val result = repository.loadGlobalConfig()

            // then
            assertEquals(payloadConfig, result)
            coVerify { payloadApi.loadConfigByBranch("global_handheld") }
        }

    @Test
    fun `loadGlobalConfig throw exception when API response is unsuccessful`() =
        runTest {
            // given
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.isSuccessful } returns false
            coEvery { payloadApi.loadConfigByBranch("global_handheld") } returns mockRestfulResponse

            // when
            try {
                repository.loadGlobalConfig()
            } catch (e: Exception) {
                // then
                assertEquals("ไม่สามารถโหลดการตั้งค่าหลักของแอปได้ในเวลาที่กำหนด", e.message)
                coVerify { payloadApi.loadConfigByBranch("global_handheld") }
            }
        }

    @Test
    fun `loadGlobalConfig throw exception when parse bodyToObject is null`() =
        runTest {
            // given
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.bodyToObject(PayloadConfig::class.java) } returns null
            every { mockRestfulResponse.isSuccessful } returns true
            coEvery { payloadApi.loadConfigByBranch("global_handheld") } returns mockRestfulResponse

            // when
            try {
                repository.loadGlobalConfig()
            } catch (e: Exception) {
                // then
                assertEquals("ข้อมูลการตั้งค่าหลักของแอปไม่ถูกต้อง", e.message)
                coVerify { payloadApi.loadConfigByBranch("global_handheld") }
            }
        }

    @Test
    fun `loadGlobalConfig throw exception when other error`() =
        runTest {
            // given
            every { configLoader.jsonConfig.configMaxRetry } returns -1
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.bodyToObject(PayloadConfig::class.java) } returns null
            every { mockRestfulResponse.isSuccessful } returns true
            coEvery { payloadApi.loadConfigByBranch("global_handheld") } returns mockRestfulResponse

            // when
            try {
                repository.loadGlobalConfig()
            } catch (e: Exception) {
                // then
                assertEquals("เกิดข้อผิดพลาดในขั้นตอนโหลดการตั้งค่าหลักของแอป", e.message)
            }
        }

    @Test
    fun `loadConfigByBranch returns PayloadConfig when API response is successful`() =
        runTest {
            // given
            val payloadConfig = mockk<PayloadConfig>()
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.bodyToObject(PayloadConfig::class.java) } returns payloadConfig
            every { mockRestfulResponse.isSuccessful } returns true
            coEvery { payloadApi.loadConfigByBranch("0101") } returns mockRestfulResponse

            // when
            val result = repository.loadConfigByBranch("0101")

            // then
            assertEquals(payloadConfig, result)
            coVerify { payloadApi.loadConfigByBranch("0101") }
        }

    @Test
    fun `loadConfigByBranch throw exception when API response is unsuccessful`() =
        runTest {
            // given
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.isSuccessful } returns false
            every { mockRestfulResponse.httpStatus } returns 400
            coEvery { payloadApi.loadConfigByBranch("0101") } returns mockRestfulResponse

            // when
            try {
                repository.loadConfigByBranch("0101")
            } catch (e: Exception) {
                // then
                assertEquals("ไม่สามารถโหลดการตั้งค่าของสาขา 0101 ได้ในเวลาที่กำหนด", e.message)
                coVerify { payloadApi.loadConfigByBranch("0101") }
            }
        }

    @Test
    fun `loadConfigByBranch throw exception when parse bodyToObject is null`() =
        runTest {
            // given
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.bodyToObject(PayloadConfig::class.java) } returns null
            every { mockRestfulResponse.isSuccessful } returns true
            coEvery { payloadApi.loadConfigByBranch("0101") } returns mockRestfulResponse

            // when
            try {
                repository.loadConfigByBranch("0101")
            } catch (e: Exception) {
                // then
                assertEquals("ข้อมูลการตั้งค่าของสาขา 0101 ไม่ถูกต้อง", e.message)
                coVerify { payloadApi.loadConfigByBranch("0101") }
            }
        }

    @Test
    fun `loadConfigByBranch throw exception when other error`() =
        runTest {
            // given
            every { configLoader.jsonConfig.configMaxRetry } returns -1
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.bodyToObject(PayloadConfig::class.java) } returns null
            every { mockRestfulResponse.isSuccessful } returns true
            coEvery { payloadApi.loadConfigByBranch("0101") } returns mockRestfulResponse

            // when
            try {
                repository.loadConfigByBranch("0101")
            } catch (e: Exception) {
                // then
                assertEquals("เกิดข้อผิดพลาดในขั้นตอนโหลดการตั้งค่าของสาขา 0101", e.message)
            }
        }

    @Test
    fun `loadConfigByBranch throw exception when error not found`() =
        runTest {
            // given
            val mockRestfulResponse: RestfulResponse<PayloadConfig> = mockk()
            every { mockRestfulResponse.isSuccessful } returns false
            every { mockRestfulResponse.httpStatus } returns 404
            coEvery { payloadApi.loadConfigByBranch("0101") } returns mockRestfulResponse

            // when
            try {
                repository.loadConfigByBranch("0101")
            } catch (e: Exception) {
                // then
                assertEquals(ErrorCodes.NOT_FOUND, e.message)
                coVerify { payloadApi.loadConfigByBranch("0101") }
            }
        }

    @Test
    fun `saveGlobalConfig call sharePrefManager saved`() =
        runTest {
            // given
            val payloadConfig = mockk<PayloadConfig>()
            coEvery { sharePrefManager.save(SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY, payloadConfig) } returns Job()
            // when
            repository.saveGlobalConfig(payloadConfig)
            // then
            coVerify { sharePrefManager.save(SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY, payloadConfig) }
        }

    @Test
    fun `saveBranchConfig call sharePrefManager saved`() =
        runTest {
            // given
            val payloadConfig = mockk<PayloadConfig>()
            coEvery { sharePrefManager.save(SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY, payloadConfig) } returns Job()
            // when
            repository.saveBranchConfig(payloadConfig)
            // then
            coVerify { sharePrefManager.save(SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY, payloadConfig) }
        }

    @Test
    fun `loadGlobalCacheConfig return config when get config from cache success`() =
        runTest {
            // given
            val payloadConfig = mockk<PayloadConfig>()
            every {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            } returns payloadConfig
            // when
            val result = repository.loadGlobalCacheConfig()
            // then
            assertEquals(payloadConfig, result)
            verify {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            }
        }

    @Test
    fun `loadGlobalCacheConfig return null when get config from cache error`() =
        runTest {
            // given
            every {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            } throws Exception("cache error")
            // when
            val result = repository.loadGlobalCacheConfig()
            // then
            assertNull(result)
            verify {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_GLOBAL_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            }
        }

    @Test
    fun `loadBranchCacheConfig return config when get config from cache success`() =
        runTest {
            // given
            val payloadConfig = mockk<PayloadConfig>()
            every {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            } returns payloadConfig
            // when
            val result = repository.loadBranchCacheConfig()
            // then
            assertEquals(payloadConfig, result)
            verify {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            }
        }

    @Test
    fun `loadBranchCacheConfig return null when get config from cache error`() =
        runTest {
            // given
            val payloadConfig = mockk<PayloadConfig>()
            every {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            } throws Exception("cache error")
            // when
            val result = repository.loadBranchCacheConfig()
            // then
            assertNull(result)
            verify {
                sharePrefManager.get(
                    SharePrefKeys.PAYLOAD_BRANCH_CONFIG_KEY,
                    PayloadConfig::class.java,
                )
            }
        }
}
