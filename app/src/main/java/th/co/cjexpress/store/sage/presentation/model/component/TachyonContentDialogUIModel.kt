package th.co.cjexpress.store.sage.presentation.model.component

import th.co.cjexpress.store.sage.R

data class TachyonContentDialogUIModel(
    val negativeTextRes: Int? = null,
    val onNegativeClick: (() -> Unit)? = null,
    val positiveTextRes: Int? = R.string.text_close,
    val onPositiveClick: (() -> Unit)? = null,
)
