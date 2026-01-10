package th.co.cjexpress.store.sage.utils

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import th.co.cjexpress.store.sage.constants.ErrorCodes
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.data.model.BasePosAdminResponse
import th.co.cjexpress.store.sage.data.model.BaseSageBffResponse
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import java.io.IOException

fun <T, M> getApiResponse(
    apiCall: suspend () -> RestfulResponse<T>?,
    mapper: (T) -> M,
    modelClass: Class<T>,
): Flow<Result<M>> =
    flow {
        try {
            val resp = apiCall()

            if (resp != null) {
                if (resp.isSuccessful) {
                    val respModel =
                        resp.bodyToObject(modelClass) ?: throw Exception("parse response error")
                    val model = mapper(respModel)
                    emit(Result.Success(model))
                } else {
                    val respModel = resp.bodyToObject(modelClass)
                    if (respModel == null && resp.t != null) {
                        resp.t?.let { throw it }
                    } else {
                        when (respModel) {
                            is BaseSageBffResponse -> {
                                if (resp.httpStatus >= 500 || resp.httpStatus < 200) {
                                    emit(
                                        handleInternalServerError(
                                            httpMessage = resp.httpMessage,
                                            respRef = respModel.ref
                                        )
                                    )
                                } else {
                                    emit(
                                        handleResponseError(
                                            respCode = respModel.code,
                                            respMessage = respModel.message,
                                            respRef = respModel.ref
                                        )
                                    )
                                }
                            }
                            is BasePosAdminResponse -> {
                                if (resp.httpStatus >= 500 || resp.httpStatus < 200) {
                                    emit(
                                        handleInternalServerError(
                                            httpMessage = resp.httpMessage,
                                            respRef = respModel.ref ?: ""
                                        )
                                    )
                                } else {
                                    emit(
                                        handleResponseError(
                                            respCode = respModel.code.toString(),
                                            respMessage = respModel.message,
                                            respRef = respModel.ref
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                emit(handleNullResponse())
            }
        } catch (e: IOException) {
            emit(handleIOException(e))
        } catch (e: Exception) {
            emit(handleException(e))
        }
    }

private fun handleInternalServerError(httpMessage: String, respRef: String?) =
    Result.Error(
        data = null,
        cause =
            BusinessException(
                errorCode = ErrorCodes.INTERNAL_SERVER_ERROR,
                type = ErrorType.ERROR,
                message = httpMessage,
                ref = respRef,
            ),
    )

private fun handleException(e: Exception) =
    Result.Error(
        data = null,
        cause =
            BusinessException(
                errorCode = "",
                type = ErrorType.ERROR,
                message = "Unexpected error: ${e.message}",
            ),
    )

private fun handleIOException(e: Exception) =
    Result.Error(
        data = null,
        cause =
            BusinessException(
                errorCode = "",
                type = ErrorType.ERROR,
                message = "Network error: ${e.message}",
            ),
    )

private fun handleNullResponse() =
    Result.Error(
        data = null,
        cause =
            BusinessException(
                errorCode = "",
                type = ErrorType.ERROR,
                message = "Null response from API",
            ),
    )

fun handleResponseError(
    respCode: String?,
    respMessage: String?,
    respRef: String?,
) = Result.Error(
    data = null,
    cause =
        BusinessException(
            errorCode = respCode ?: "",
            type = ErrorType.ERROR,
            message = respMessage,
            ref = respRef,
        ),
    canRetry = true,
)
