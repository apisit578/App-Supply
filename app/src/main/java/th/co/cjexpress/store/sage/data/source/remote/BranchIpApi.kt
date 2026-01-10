package th.co.cjexpress.store.sage.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_BRANCH_IP
import th.co.cjexpress.store.sage.data.model.branchMaster.BranchIpResponse
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.data.network.getRestfulClient

class BranchIpApi {
    suspend fun getBranchIp(ipAddress: String): RestfulResponse<BranchIpResponse>? {
        val headerKey = "X-Forwarded-For"
        val headers = hashMapOf(Pair(headerKey, ipAddress))
        return withContext(Dispatchers.IO) {
            val req = getRestfulClient<BranchIpResponse>(SERVICE_BRANCH_IP, header = headers)
            req?.requestGet()
        }
    }
}
