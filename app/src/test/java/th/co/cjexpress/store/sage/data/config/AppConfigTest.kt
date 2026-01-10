package th.co.cjexpress.store.sage.data.config

import android.content.ContentResolver
import android.content.Context
import android.provider.Settings
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.mockk
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.payload.OAuthRowConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.model.payload.ServiceHeaderConfig

class AppConfigTest {
    @MockK
    lateinit var context: Context

    @MockK
    lateinit var contentResolver: ContentResolver

    @MockK
    lateinit var payloadConfig: PayloadConfig

    @MockK
    lateinit var branchInfoModel: BranchInfoModel

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockkStatic(Settings.Secure::class)
        mockkObject(AppDependencyProvider)

        every { AppDependencyProvider.getProviderBuilder().getContext() } returns context
        every { context.contentResolver } returns contentResolver
        every {
            Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
        } returns "mock-android-id"
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun `androidId should return mocked value`() {
        val appConfig = AppConfig()
        assertEquals("mock-android-id", appConfig.androidId)
    }

    @Test
    fun `updatePayloadConfig should store config and reflect in featureEnable`() {
        val appConfig = AppConfig()

        val featureMap = hashMapOf("feature1" to true, "feature2" to false)
        every { payloadConfig.featureEnable } returns featureMap

        appConfig.updatePayloadConfig(payloadConfig)

        assertTrue(appConfig.featureEnable["feature1"] == true)
        assertFalse(appConfig.featureEnable["feature2"] == true)
    }

    @Test
    fun `getOAuthConfig should return correct OAuth config`() {
        val appConfig = AppConfig()
        val oauthConfig = mockk<OAuthRowConfig>()
        val map = hashMapOf("service1" to oauthConfig)
        every { payloadConfig.oAuth } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertEquals(oauthConfig, appConfig.getOAuthConfig("service1"))
    }

    @Test
    fun `getServiceConfig should return correct service config`() {
        val appConfig = AppConfig()
        val serviceConfig = mockk<ServiceHeaderConfig>()
        val map = hashMapOf("serviceX" to serviceConfig)
        every { payloadConfig.services } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertEquals(serviceConfig, appConfig.getServiceConfig("serviceX"))
    }

    @Test
    fun `updateBranchInfo and getBranchInfo should work correctly`() {
        val appConfig = AppConfig()

        appConfig.updateBranchInfo(branchInfoModel)

        assertEquals(branchInfoModel, appConfig.getBranchInfo())
    }

    @Test
    fun `isFeatureEnable should return correct boolean`() {
        val appConfig = AppConfig()
        every { payloadConfig.featureEnable } returns hashMapOf("testFeature" to true)

        appConfig.updatePayloadConfig(payloadConfig)

        assertTrue(appConfig.isFeatureEnable("testFeature"))
        assertFalse(appConfig.isFeatureEnable("nonExistingFeature"))
    }

    @Test
    fun `getOAuthConfig should return correct config when key exists`() {
        val appConfig = AppConfig()
        val oAuthRowConfig = mockk<OAuthRowConfig>()
        val map = hashMapOf("oAuthKey" to oAuthRowConfig)
        every { payloadConfig.oAuth } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertEquals(oAuthRowConfig, appConfig.getOAuthConfig("oAuthKey"))
    }

    @Test
    fun `getOAuthConfig should return null when key does not exist`() {
        val appConfig = AppConfig()
        val oAuthRowConfig = mockk<OAuthRowConfig>()
        val map = hashMapOf("oAuthKey" to oAuthRowConfig)
        every { payloadConfig.oAuth } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertNull(appConfig.getOAuthConfig("nonexistent"))
    }

    @Test
    fun `getOAuthConfig should return null when payloadConfig is null`() {
        val appConfig = AppConfig()
        val result = appConfig.getOAuthConfig("nonexistent")
        assertNull(result)
    }

    @Test
    fun `getOAuthConfig should return null when oAuth is null`() {
        val appConfig = AppConfig()
        val map = null
        every { payloadConfig.oAuth } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertNull(appConfig.getOAuthConfig("oAuthKey"))
    }

    @Test
    fun `getServiceConfig should return correct config when service exists`() {
        val appConfig = AppConfig()
        val serviceHeaderConfig = mockk<ServiceHeaderConfig>()
        val map = hashMapOf("myService" to serviceHeaderConfig)
        every { payloadConfig.services } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertEquals(serviceHeaderConfig, appConfig.getServiceConfig("myService"))
    }

    @Test
    fun `getServiceConfig should return null when service does not exist`() {
        val appConfig = AppConfig()
        val serviceHeaderConfig = mockk<ServiceHeaderConfig>()
        val map = hashMapOf("myService" to serviceHeaderConfig)
        every { payloadConfig.services } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertNull(appConfig.getServiceConfig("noneService"))
    }

    @Test
    fun `getServiceConfig should return null when payloadConfig is null`() {
        val appConfig = AppConfig()
        val result = appConfig.getServiceConfig("nonexistent")
        assertNull(result)
    }

    @Test
    fun `getServiceConfig should return null when service is null`() {
        val appConfig = AppConfig()
        val map = null
        every { payloadConfig.services } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertNull(appConfig.getServiceConfig("myService"))
    }

    @Test
    fun `isFeatureEnable should return true when feature is enabled`() {
        val appConfig = AppConfig()
        val featureName = "FEATURE_A"
        val map = hashMapOf(featureName to true)
        every { payloadConfig.featureEnable } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertTrue(appConfig.isFeatureEnable(featureName))
    }

    @Test
    fun `isFeatureEnable should return false when feature is not enabled`() {
        val appConfig = AppConfig()
        val featureName = "FEATURE_A"
        val map = hashMapOf(featureName to false)
        every { payloadConfig.featureEnable } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertFalse(appConfig.isFeatureEnable(featureName))
    }

    @Test
    fun `isFeatureEnable should return false when feature not found`() {
        val appConfig = AppConfig()
        val map = hashMapOf<String, Boolean>()
        every { payloadConfig.featureEnable } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertFalse(appConfig.isFeatureEnable("UNKNOWN_FEATURE"))
    }

    @Test
    fun `isFeatureEnable should return false when payloadConfig is null`() {
        val appConfig = AppConfig()
        val result = appConfig.isFeatureEnable("UNKNOWN_FEATURE")
        assertFalse(result)
    }

    @Test
    fun `isFeatureEnable should return false when featureEnable is null`() {
        val appConfig = AppConfig()
        val featureName = "FEATURE_A"
        val map = null
        every { payloadConfig.featureEnable } returns map

        appConfig.updatePayloadConfig(payloadConfig)

        assertFalse(appConfig.isFeatureEnable(featureName))
    }
}
