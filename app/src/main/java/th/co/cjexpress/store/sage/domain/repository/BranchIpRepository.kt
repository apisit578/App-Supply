package th.co.cjexpress.store.sage.domain.repository

import th.co.cjexpress.store.sage.domain.model.BranchIpModel
import th.co.cjexpress.store.sage.domain.model.Result

interface BranchIpRepository {
    suspend fun checkBranchIP(ipAddress: String): Result<BranchIpModel>
}
