package th.co.cjexpress.store.sage.domain.usecase.branch_master

import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.BranchIpModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.repository.BranchIpRepository
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.utils.CrashlyticsLoggerUtils
import th.co.cjexpress.store.sage.utils.IPV4

class CheckBranchIpUseCase(
    private val branchIpRepository: BranchIpRepository,
    private val configRepository: ConfigRepository,
) {
    suspend fun execute(): Result<BranchIpModel> =
        when (val branchIpResp = branchIpRepository.checkBranchIP(IPV4)) {
            is Result.Success -> {
                updateBranchInfo(branchIpResp.data)
                Result.Success(branchIpResp.data)
            }

            is Result.Error -> {
                updateBranchInfo(null)
                Result.Error(cause = branchIpResp.cause)
            }
        }

    fun updateBranchInfo(data: BranchIpModel? = null) {
        val branchInfo =
            BranchInfoModel(
                branchCode = data?.branchCode ?: "",
                branchName = data?.branchNameTh ?: "",
            )
        configRepository.updateBranchInfo(branchInfo)

        // set crashlytics custom keys
        val branchCode = data?.branchCode ?: "N/A"
        CrashlyticsLoggerUtils.apply {
            setCustomKey("BranchCode", branchCode)
            setCustomKey("DeviceIP", IPV4)
        }
    }
}
