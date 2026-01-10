package th.co.cjexpress.store.sage.domain.model

import th.co.cjexpress.store.sage.presentation.model.BusinessException

sealed class Result<out T> {
    data class Success<out T>(
        val data: T,
    ) : Result<T>()

    data class Error<out T>(
        val data: T? = null,
        val cause: BusinessException,
        val canRetry: Boolean = false,
    ) : Result<T>()
}
