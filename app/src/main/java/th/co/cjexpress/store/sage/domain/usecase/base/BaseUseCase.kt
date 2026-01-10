package th.co.cjexpress.store.sage.domain.usecase.base

import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.ResourceUtils

interface UseCaseErrorHandler {
    fun <T> handleError(result: Result.Error<T>): Result.Error<T>
}

abstract class BaseUseCase(
    protected val resourceUtils: ResourceUtils,
) : UseCaseErrorHandler {
    override fun <T> handleError(result: Result.Error<T>): Result.Error<T> =
        when (result.cause.errorCode) {
            "", ErrorCodes.INTERNAL_SERVER_ERROR, ErrorCodes.STORM_SERVICE_ERROR ->
                Result.Error(
                    cause =
                        BusinessException(
                            errorCode = ErrorCodes.INTERNAL_SERVER_ERROR,
                            type = ErrorType.ERROR,
                            message = getServiceConnectionFailedMessage(),
                            ref = result.cause.ref,
                        ),
                    canRetry = true
                )

            "401" ->
                if (result.cause.errorCode == ErrorCodes.UNAUTHORIZED) {
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.UNAUTHORIZED,
                                type = ErrorType.WARNING,
                                message = getUnauthorizedMessage(),
                            ),
                    )
                } else {
                    Result.Error(
                        cause =
                            BusinessException(
                                errorCode = ErrorCodes.SESSION_TIMEOUT,
                                type = ErrorType.WARNING,
                                message = getSessionTimeoutMessage(),
                            ),
                    )
                }

            else -> result
        }

    private fun getServiceConnectionFailedMessage(): String =
        String.format(
            resourceUtils.getString(R.string.text_because),
            resourceUtils.getString(R.string.text_because_service_connection_failed),
        )

    private fun getSessionTimeoutMessage(): String =
        buildString {
            appendLine(resourceUtils.getString(R.string.text_session_timeout))
            appendLine(resourceUtils.getString(R.string.text_please_sign_in_again))
        }.trimEnd()

    private fun getUnauthorizedMessage(): String = resourceUtils.getString(R.string.text_unauthorized)
}
