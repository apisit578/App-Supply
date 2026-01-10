package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteListMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetSdToteListUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(): Flow<Result<SdToteListMappingModel>> =
        sageApiRepository.getSdToteList().map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> super.handleError(result)
            }
        }
}
