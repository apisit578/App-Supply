package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.DocumentSDModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetSdDocListUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(): Flow<Result<DocumentSDModel>> =
        sageApiRepository.getSdDocList().map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> super.handleError(result)
            }
        }
}
