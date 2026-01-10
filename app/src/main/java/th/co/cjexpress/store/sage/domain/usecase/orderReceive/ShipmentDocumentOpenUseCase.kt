package th.co.cjexpress.store.sage.domain.usecase.orderReceive

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceive.ShipmentDocumentModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class ShipmentDocumentOpenUseCase(
    private val repository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    fun execute(ldNo: String): Flow<Result<ShipmentDocumentModel>> =
        if (ldNo.isBlank()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.INVALID_PARAMETER,
                            type = ErrorType.WARNING,
                            message = resourceUtils.getString(R.string.text_please_fill_or_scan_ld),
                        ),
                ),
            )
        } else {
            repository.shipmentDocumentOpen(ldNo).map { result ->
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
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_scan_ld_not_found_please_scan_again),
                        ),
                )
            }
            ErrorCodes.DOC_ALREADY_APPROVED -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = result.cause.errorCode,
                            type = ErrorType.WARNING,
                            title = resourceUtils.getString(R.string.text_unable_to_continue),
                            message = resourceUtils.getString(R.string.text_scan_ld_not_found_please_scan_again),
                        ),
                )
            }

            else -> super.handleError(result)
        }
}
