package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SearchToteByDocNoUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(sdNo: String): Flow<Result<SdToteDetailMappingModel>> =
        sageApiRepository.searchToteBySdNo(sdNo).map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> handleError(result)
            }
        }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            "50000" ->
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            message = getMessage(result.cause.message.orEmpty()),
                        ),
                )

            ErrorCodes.INVALID_STATUS ->
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_error_shipment_document_tote_not_found),
                        ),
                )

            else -> super.handleError(result)
        }

    private fun getMessage(msg: String): String =
        String.format(
            resourceUtils.getString(R.string.text_because),
            msg,
        )
}
