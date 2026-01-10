package th.co.cjexpress.store.sage.presentation.model

import th.co.cjexpress.store.sage.constants.ErrorType

class BusinessException(
    val errorCode: String,
    val type: ErrorType,
    val title: String? = null,
    val ref: String? = null,
    message: String?,
//    var data: Any? = null,
) : Exception(message)
