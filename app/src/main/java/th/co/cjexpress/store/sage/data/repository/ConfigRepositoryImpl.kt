package th.co.cjexpress.store.sage.data.repository

import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository

class ConfigRepositoryImpl(private val appConfig: AppConfig) : ConfigRepository {
    override fun updatePayloadAppConfig(payloadConfig: PayloadConfig) {
        appConfig.updatePayloadConfig(payloadConfig)
    }

    override fun getAppConfig(): AppConfig {
        return appConfig
    }

    override fun updateBranchInfo(branchInfoModel: BranchInfoModel) {
        appConfig.updateBranchInfo(branchInfoModel)
    }

    override fun updateAuthToken(auth: AuthTokenRespModel) {
        appConfig.authToken = auth
    }
}
