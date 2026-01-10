package th.co.cjexpress.store.sage.domain.usecase.requestDiscountRtc

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.requestDiscountRtc.BdDocumentNoDetailMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class GetBdDocumentNoDetailUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private val unexpectedError =
        setOf(
            ErrorCodes.INVALID_PARAMETER,
            ErrorCodes.INVALID_DOC,
        )

    fun execute(docNo: String): Flow<Result<BdDocumentNoDetailMappingModel>> =
        sageApiRepository.bdDocumentNoDetail(docNo).map { result ->
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
            ErrorCodes.INVALID_STATUS -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_changed_status_document),
                        ),
                )
            }
            ErrorCodes.DOC_NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_not_found_document),
                        ),
                )
            }
            else -> super.handleError(result)
        }
}
