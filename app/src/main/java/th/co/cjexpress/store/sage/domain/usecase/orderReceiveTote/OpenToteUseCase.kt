package th.co.cjexpress.store.sage.domain.usecase.orderReceiveTote

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes.ALREADY_CHECKED
import th.co.cjexpress.store.sage.constants.ErrorCodes.ALREADY_FILLED
import th.co.cjexpress.store.sage.constants.ErrorCodes.INVALID_OPERATION
import th.co.cjexpress.store.sage.constants.ErrorCodes.INVALID_STATUS
import th.co.cjexpress.store.sage.constants.ErrorCodes.NOT_FOUND
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteDetailMappingModel
import th.co.cjexpress.store.sage.domain.repository.SageApiRepository
import th.co.cjexpress.store.sage.domain.usecase.base.BaseUseCase
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

class OpenToteUseCase(
    private val sageApiRepository: SageApiRepository,
    resourceUtils: ResourceUtils,
) : BaseUseCase(resourceUtils) {
    private var toteCode: String = ""

    fun execute(toteCode: String): Flow<Result<SdToteDetailMappingModel>> {
        this.toteCode = toteCode
        return if (toteCode.isBlank()) {
            flowOf(
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = "",
                            type = ErrorType.WARNING,
                            message = getPleaseScanToteMessage(),
                        ),
                ),
            )
        } else {
            sageApiRepository.openTote(toteCode).map { result ->
                when (result) {
                    is Result.Success -> result
                    is Result.Error -> handleError(result)
                }
            }
        }
    }

    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            "40000", NOT_FOUND, INVALID_OPERATION -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = NOT_FOUND,
                            type = ErrorType.WARNING,
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_error_tote_not_found),
                                    toteCode,
                                ),
                        ),
                )
            }

            ALREADY_FILLED -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ALREADY_FILLED,
                            type = ErrorType.WARNING,
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_info_tote_already_filled),
                                    toteCode,
                                ),
                        ),
                )
            }

            ALREADY_CHECKED, INVALID_STATUS -> {
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ALREADY_CHECKED,
                            type = ErrorType.WARNING,
                            message =
                                String.format(
                                    resourceUtils.getString(R.string.text_tote_already_checked),
                                    toteCode,
                                ),
                        ),
                )
            }
            else -> super.handleError(result)
        }

    private fun getPleaseScanToteMessage(): String = resourceUtils.getString(R.string.text_please_scan_tote)
}
