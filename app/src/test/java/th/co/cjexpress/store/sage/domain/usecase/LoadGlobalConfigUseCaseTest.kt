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
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.repository.PayloadRepository
import th.co.cjexpress.store.sage.domain.usecase.payload.LoadGlobalConfigUseCase
import th.co.cjexpress.store.sage.utils.LogUtils

class LoadGlobalConfigUseCaseTest {
    private lateinit var payloadRepository: PayloadRepository
    private lateinit var configRepository: ConfigRepository
    private lateinit var logUtils: LogUtils
    private lateinit var mockAppConfig: AppConfig

    private lateinit var useCase: LoadGlobalConfigUseCase

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
            LoadGlobalConfigUseCase(
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
    fun `execute should return config when loadGlobalConfig success`() =
        runTest {
            // given
            val mockPayloadConfig = mockk<PayloadConfig>()
            coEvery { payloadRepository.loadGlobalConfig() } returns mockPayloadConfig
            coEvery { payloadRepository.saveGlobalConfig(any()) } returns Unit
            coEvery { configRepository.updatePayloadAppConfig(any()) } returns Unit
            coEvery {
                logUtils.logInfo(
                    action = LogAction.LOAD_GLOBAL_CONFIG,
                    message = "load global config success",
                    username = "",
                    branchCode = "M04C",
                    url = null,
                )
            } returns Unit

            // when
            val result = useCase.execute()

            // then
            assertTrue(result is Result.Success)
            assertEquals(mockPayloadConfig, (result as Result.Success).data)
            coVerify { payloadRepository.loadGlobalConfig() }
            coVerify { payloadRepository.saveGlobalConfig(mockPayloadConfig) }
            coVerify { configRepository.updatePayloadAppConfig(mockPayloadConfig) }
        }

    @Test
    fun `execute should return config when loadGlobalConfig fail`() =
        runTest {
            // given
            coEvery { payloadRepository.loadGlobalConfig() } throws Exception("Failed to load config")
            coEvery { payloadRepository.loadGlobalCacheConfig() } returns null
            coEvery {
                logUtils.logError(
                    action = LogAction.LOAD_GLOBAL_CONFIG,
                    message = "load global config error, Failed to load config",
                    username = "",
                    branchCode = "M04C",
                    url = null,
                )
            } returns Unit

            // when
            val result = useCase.execute()

            // then
            assertTrue(result is Result.Error)
        }
}
