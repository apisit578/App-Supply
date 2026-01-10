package th.co.cjexpress.store.sage.presentation.state.base

import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel

sealed class BaseScreenState {
    data object Idle : BaseScreenState()

    data object Loading : BaseScreenState()

    data class Error(
        val errorData: TachyonAlertDialogUIModel,
    ) : BaseScreenState()

    data class Success<T>(
        val data: T?,
    ) : BaseScreenState()
}
