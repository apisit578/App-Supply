package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteApproveMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteApproveUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(sdNo: String): Flow<Result<SdToteApproveMappingModel>> =
        sageApiRepository.sdToteApprove(sdNo = sdNo).map { result ->
            when (result) {
                is Result.Success -> Result.Success(data = result.data)
                is Result.Error -> super.handleError(result)
            }
        }
}
