package th.co.cjexpress.store.sage.presentation.model.component

import th.co.cjexpress.store.sage.constants.ErrorCodes

data class TachyonDateInputFieldResultModel(
    val errorCode: ErrorCodes.DateInputFieldValidationErrorCodes? = null,
    val resultDate: String = "",
)
