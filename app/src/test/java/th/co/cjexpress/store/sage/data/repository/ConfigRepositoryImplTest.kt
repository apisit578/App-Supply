package th.co.cjexpress.store.sage.data.repository

import io.mockk.mockk
import io.mockk.unmockkAll
import io.mockk.verify
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig

class ConfigRepositoryImplTest {
    private lateinit var appConfig: AppConfig
    private lateinit var repository: ConfigRepositoryImpl

    @Before
    fun setUp() {
        appConfig = mockk(relaxed = true)
        repository = ConfigRepositoryImpl(appConfig)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `updatePayloadAppConfig call appConfig updatePayloadConfig`() {
        // given
        val payloadConfig = mockk<PayloadConfig>()

        // when
        repository.updatePayloadAppConfig(payloadConfig)

        // then
        verify { appConfig.updatePayloadConfig(payloadConfig) }
    }

    @Test
    fun `getAppConfig return appConfig`() {
        assertEquals(appConfig, repository.getAppConfig())
    }

    @Test
    fun `updateBranchInfo call appConfig updateBranchInfo`() {
        // given
        val branchInfoModel = mockk<BranchInfoModel>()

        // when
        repository.updateBranchInfo(branchInfoModel)

        // then
        verify { appConfig.updateBranchInfo(branchInfoModel) }
    }

    @Test
    fun `updateAuthToken call appConfig update authToken`() {
        // given
        val authTokenRespModel = mockk<AuthTokenRespModel>()

        // when
        repository.updateAuthToken(authTokenRespModel)

        // then
        assertNotNull(appConfig.authToken)
    }
}
