package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.OrderReceiveSummaryModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class OrderReceiveSummaryUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var sdNo: String = ""

    fun execute(sdNo: String): Flow<Result<OrderReceiveSummaryModel>> {
        this.sdNo = sdNo
        return sageApiRepository.getShipmentDocumentSummaryBySd(sdNo = sdNo).map { result ->
            when (result) {
                is Result.Success -> result
                is Result.Error -> handleError(result)
            }
        }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            ErrorCodes.NOT_FOUND -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.NOT_FOUND,
                            type = ErrorType.WARNING,
                            message = result.cause.message,
                        ),
                )
            }
            else -> super.handleError(result)
        }
}
