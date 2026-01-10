package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.mapper.DTOModelMapper
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitAdjustModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.utils.ResourceUtils

class SdToteSubmitAdjustUseCase(
    private val sageApiRepository: SageApiRepository,
    private val dtoModelMapper: DTOModelMapper,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(receivingBarcode: SdToteSubmitUIModel): Flow<Result<SdToteSubmitAdjustModel>> =
        sageApiRepository
            .shipmentToteDocumentSubmitAdjust(
                data = dtoModelMapper.sdToteSubmitAdjustDTOMapper.toRequest(receivingBarcode),
            ).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> handleError(result)
                }
            }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.INVALID_BODY -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.INVALID_BODY,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_actual_qty_mismatch),
                        ),
                )
            }
            else -> super.handleError(result)
        }
}
