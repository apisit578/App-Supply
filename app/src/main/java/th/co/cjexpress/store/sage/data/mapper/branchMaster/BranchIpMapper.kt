package th.co.cjexpress.store.sage.data.mapper.branchMaster

import th.co.cjexpress.store.sage.data.model.branchMaster.BranchIpResponse
import th.co.cjexpress.store.sage.domain.model.BranchIpModel

class BranchIpMapper {
    fun map(apiResponse: BranchIpResponse): BranchIpModel =
        BranchIpModel(
            branchCode = apiResponse.branchCode,
            branchNameTh = apiResponse.branchNameTh,
        )
}
