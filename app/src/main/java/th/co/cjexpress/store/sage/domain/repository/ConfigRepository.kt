package th.co.cjexpress.store.sage.domain.repository

import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig

interface ConfigRepository {
    fun updatePayloadAppConfig(payloadConfig: PayloadConfig)

    fun getAppConfig(): AppConfig

    fun updateBranchInfo(branchInfoModel: BranchInfoModel)

    fun updateAuthToken(auth: AuthTokenRespModel)
}
