package th.co.cjexpress.store.sage.data.config

import android.annotation.SuppressLint
import android.provider.Settings
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.payload.OAuthRowConfig
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.model.payload.ServiceHeaderConfig

@SuppressLint("HardwareIds")
class AppConfig {
    val androidId: String by lazy {
        Settings.Secure.getString(
            AppDependencyProvider.getProviderBuilder().getContext().contentResolver,
            Settings.Secure.ANDROID_ID,
        )
    }

    private var branchInfo: BranchInfoModel? = null
    var payloadConfig: PayloadConfig? = null
    val featureEnable: HashMap<String, Boolean> get() = payloadConfig?.featureEnable ?: hashMapOf()

    var authToken: AuthTokenRespModel? = null
    var username: String? = null

    fun updatePayloadConfig(config: PayloadConfig) {
        this.payloadConfig = config
    }

    fun updateBranchInfo(branchInfoModel: BranchInfoModel) {
        branchInfo = branchInfoModel
    }

    fun getBranchInfo(): BranchInfoModel? {
        return branchInfo
    }

    fun getOAuthConfig(key: String): OAuthRowConfig? {
        return payloadConfig?.oAuth?.get(key)
    }

    fun getServiceConfig(serviceName: String): ServiceHeaderConfig? {
        return payloadConfig?.services?.get(serviceName)
    }

    fun isFeatureEnable(featureName: String): Boolean {
        return payloadConfig?.featureEnable?.get(featureName) ?: false
    }
}
