package th.co.cjexpress.store.sage.domain.usecase

import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import io.mockk.spyk
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.repository.PayloadRepository
import th.co.cjexpress.store.sage.domain.usecase.payload.LoadBranchConfigUseCase
import th.co.cjexpress.store.sage.utils.LogUtils
import java.util.Date

class LoadBranchConfigUseCaseTest {
    private lateinit var payloadRepository: PayloadRepository
    private lateinit var configRepository: ConfigRepository
    private lateinit var logUtils: LogUtils
    private lateinit var mockAppConfig: AppConfig

    private lateinit var useCase: LoadBranchConfigUseCase

    @Before
    fun setUp() {
        payloadRepository = mockk()
        configRepository = mockk()
        mockAppConfig = mockk()
        every { mockAppConfig.getBranchInfo()?.branchCode } returns "M04C"
        logUtils =
            spyk(
                LogUtils(
                    appConfig = mockAppConfig,
                    dataSource = mockk(),
                ),
            )

        useCase =
            LoadBranchConfigUseCase(
                payloadRepository = payloadRepository,
                configRepository = configRepository,
                logUtils = logUtils,
            )
    }

    @After
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `execute should return config when loadConfigByBranch success`() =
        runTest {
            // given
            val mockGlobalConfig = mockk<PayloadConfig>(relaxed = true)
            val mockBranchConfig = mockk<PayloadConfig>(relaxed = true)
            coEvery { payloadRepository.loadConfigByBranch("0101") } returns mockBranchConfig
            coEvery { payloadRepository.saveBranchConfig(any()) } returns Unit
            coEvery { configRepository.updatePayloadAppConfig(any()) } returns Unit
            coEvery { mockAppConfig.payloadConfig } returns mockGlobalConfig
            coEvery { configRepository.getAppConfig() } returns mockAppConfig
            every {
                logUtils.logInfo(
                    action = LogAction.LOAD_BRANCHES_CONFIG,
                    message = "load branch config success",
                    username = "",
                    branchCode = "M04C",
                    url = null,
                )
            } returns Unit

            // when
            val result = useCase.execute("0101")

            // then
            assertTrue(result is Result.Success)
            assertEquals(true, (result as Result.Success).data)
            coVerify { payloadRepository.loadConfigByBranch("0101") }
            coVerify { payloadRepository.saveBranchConfig(mockBranchConfig) }
            coVerify { configRepository.updatePayloadAppConfig(any()) }
        }

    @Test
    fun `execute should return config when loadConfigByBranch success but globalConfig is null`() =
        runTest {
            // given
            val mockBranchConfig = mockk<PayloadConfig>(relaxed = true)
            coEvery { payloadRepository.loadConfigByBranch("0101") } returns mockBranchConfig
            coEvery { payloadRepository.saveBranchConfig(any()) } returns Unit
            coEvery { configRepository.updatePayloadAppConfig(any()) } returns Unit
            coEvery { mockAppConfig.payloadConfig } returns null
            coEvery { configRepository.getAppConfig() } returns mockAppConfig
            every {
                logUtils.logInfo(
                    action = LogAction.LOAD_BRANCHES_CONFIG,
                    username = "",
                    message = "load branch config success",
                    branchCode = "M04C",
                    url = null,
                )
            } returns Unit

            // when
            val result = useCase.execute("0101")

            // then
            assertTrue(result is Result.Success)
            assertEquals(true, (result as Result.Success).data)
            coVerify { payloadRepository.loadConfigByBranch("0101") }
            coVerify { payloadRepository.saveBranchConfig(mockBranchConfig) }
            coVerify { configRepository.updatePayloadAppConfig(any()) }
        }

    @Test
    fun `execute should return config when fail but loadGlobalCacheConfig success`() =
        runTest {
            // given
            coEvery { payloadRepository.loadConfigByBranch("0101") } throws Exception("Failed to load config")
            coEvery { mockAppConfig.payloadConfig } returns null
            coEvery { configRepository.getAppConfig() } returns mockAppConfig
            every {
                logUtils.logError(
                    action = LogAction.LOAD_BRANCHES_CONFIG,
                    message = "load branch config error, Failed to load config",
                    username = "",
                    branchCode = "M04C",
                    url = null,
                )
            } returns Unit

            // when
            val result = useCase.execute("0101")

            // then
            assertTrue(result is Result.Error)
            coVerify { payloadRepository.loadConfigByBranch("0101") }
        }

    @Test
    fun `execute should return config when http status not found but loadGlobalCacheConfig success`() =
        runTest {
            // given
            coEvery { payloadRepository.loadConfigByBranch("0101") } throws Exception(ErrorCodes.NOT_FOUND)
            coEvery { mockAppConfig.payloadConfig } returns null
            coEvery { configRepository.getAppConfig() } returns mockAppConfig
            every {
                logUtils.logError(
                    action = LogAction.LOAD_BRANCHES_CONFIG,
                    message = "load branch config error, Failed to load config",
                    username = "",
                    branchCode = "M04C",
                    url = null,
                )
            } returns Unit

            // when
            val result = useCase.execute("0101")

            // then
            assertTrue(result is Result.Success)
            coVerify { payloadRepository.loadConfigByBranch("0101") }
        }

    @Test
    fun `mergeConfig should return merged PayloadConfig`() =
        runTest {
            // given
            val globalConfig =
                PayloadConfig(
                    branchCode = "global",
                    featureEnable =
                        hashMapOf(
                            "feature1" to true,
                            "feature2" to false,
                        ),
                    features = null,
                    oAuth = null,
                    services = null,
                    loginGroups = null,
                    totePrefix = arrayListOf("TOTE1", "TOTE2"),
                    createdAt = Date(),
                    updatedAt = Date(),
                    updatedBy = "TestUser",
                )
            val branchConfig =
                PayloadConfig(
                    branchCode = "BR001",
                    featureEnable =
                        hashMapOf(
                            "feature1" to true,
                            "feature2" to false,
                        ),
                    features = null,
                    oAuth = null,
                    services = null,
                    loginGroups = null,
                    totePrefix = arrayListOf("TOTE1", "TOTE2"),
                    createdAt = Date(),
                    updatedAt = Date(),
                    updatedBy = "TestUser",
                )

            val spyUseCase = spyk(useCase)

            every { spyUseCase.mergeDataClassValues(any(), any(), any()) } returns branchConfig

            // when
            val result = useCase.mergeConfig(globalConfig, branchConfig)

            // then
            assertEquals(branchConfig, result)
        }

    @Test
    fun `mergeConfig should return branchConfig when globalConfig is null`() =
        runTest {
            // given
            val branchConfig = mockk<PayloadConfig>(relaxed = true)

            // when
            val result = useCase.mergeConfig(null, branchConfig)

            // then
            assertEquals(branchConfig, result)
        }

    @Test
    fun `mergeField should return branchValue when globalValue is null`() =
        runTest {
            // given
            val branchValue = "branchValue"

            // when
            val result = useCase.mergeField(null, branchValue)

            // then
            assertEquals(branchValue, result)
        }

    @Test
    fun `mergeField should return globalValue when branchValue is null`() =
        runTest {
            // given
            val globalValue = "globalValue"

            // when
            val result = useCase.mergeField(globalValue, null)

            // then
            assertEquals(globalValue, result)
        }

    @Test
    fun `mergeField should return branchValue when branchValue and globalValue is not null`() =
        runTest {
            // given
            val branchValue = "branchValue"
            val globalValue = "globalValue"

            // when
            val result = useCase.mergeField(globalValue, branchValue)

            // then
            assertEquals(branchValue, result)
        }

    @Test
    fun `mergeField should return branchValue when branchValue and globalValue is hashmap`() =
        runTest {
            // given
            val branchValue = mapOf("key1" to "branchValue1", "key3" to "branchValue3")
            val globalValue =
                mapOf("key1" to "globalValue1", "key2" to "globalValue2", "key4" to "globalValue4")

            // when
            val result = useCase.mergeField(globalValue, branchValue)

            // then
            assertEquals(
                mapOf(
                    "key1" to "branchValue1",
                    "key2" to "globalValue2",
                    "key3" to "branchValue3",
                    "key4" to "globalValue4",
                ),
                result,
            )
        }

    @Test
    fun `mergeField should return branchValue when branchValue and globalValue is data class`() =
        runTest {
            // given
            data class TestData(
                val enable: Boolean,
            )

            val branchValue = TestData(false)
            val globalValue = TestData(true)

            // when
            val result = useCase.mergeField(globalValue, branchValue)

            // then
            assertEquals(TestData(false), result)
        }

    @Test
    fun `mergeDataClassValues should merge data class properties`() =
        runTest {
            // given
            data class TestData(
                val field1: String,
                val field2: Int,
            )

            val globalObj = TestData("global", 1)
            val branchObj = TestData("branch", 2)

            // when
            val result = useCase.mergeDataClassValues(TestData::class, globalObj, branchObj)

            // then
            assertEquals("branch", result?.field1)
            assertEquals(2, result?.field2)
        }
}
