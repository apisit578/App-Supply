package th.co.cjexpress.store.sage.presentation.model.component

import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType

data class TachyonAlertDialogUIModel(
    val type: ErrorType,
    val messageText: String,
    val cancelable: Boolean = false,
    val titleText: String,
    val showF1Message: Boolean = false,
    val negativeTextRes: Int? = null,
    val onNegativeClick: (() -> Unit)? = null,
    val positiveTextRes: Int? = R.string.text_close,
    val onPositiveClick: (() -> Unit)? = null,
    val ref: String? = null,
)
