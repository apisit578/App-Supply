package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetBdDocumentUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(): Flow<Result<BdDocumentMappingModel>> =
        sageApiRepository.bdDocument().map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> handleError(result)
            }
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.INVALID_PARAMETER -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.ERROR,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = result.cause.message,
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
