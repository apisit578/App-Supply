package th.co.cjexpress.store.sage.domain.repository

import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig

interface PayloadRepository {
    suspend fun loadGlobalConfig(): PayloadConfig

    suspend fun loadConfigByBranch(branchCode: String): PayloadConfig

    suspend fun saveGlobalConfig(config: PayloadConfig)

    suspend fun saveBranchConfig(config: PayloadConfig)

    suspend fun loadGlobalCacheConfig(): PayloadConfig?

    suspend fun loadBranchCacheConfig(): PayloadConfig?
}
