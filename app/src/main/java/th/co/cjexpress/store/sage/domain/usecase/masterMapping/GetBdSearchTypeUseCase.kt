package th.co.cjexpress.store.sage.domain.usecase.masterMapping

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.MasterMapping
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.masterMapping.BdSearchTypeMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetBdSearchTypeUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private val unexpectedError =
        setOf(
            ErrorCodes.NOT_FOUND,
            ErrorCodes.INVALID_PARAMETER,
        )

    fun execute(type: MasterMapping): Flow<Result<BdSearchTypeMappingModel>> =
        sageApiRepository.bdDocumentSearchType(type).map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> handleError(result)
            }
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            in unexpectedError -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.BAD_REQUEST,
                            type = ErrorType.ERROR,
                            title = result.cause.title,
                            message = result.cause.message,
                        ),
                )
            }
            else -> super.handleError(result)
        }
}
