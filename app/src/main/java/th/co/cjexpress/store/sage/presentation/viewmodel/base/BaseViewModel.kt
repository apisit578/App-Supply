package th.co.cjexpress.store.sage.presentation.viewmodel.base

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.utils.ScreenHolder
import timber.log.Timber

open class BaseViewModel : ViewModel() {
    private val _baseScreenState = MutableStateFlow<BaseScreenState>(BaseScreenState.Idle)
    val baseScreenState: StateFlow<BaseScreenState> = _baseScreenState
    val logUtils = AppDependencyProvider.getProviderBuilder().getLogManager()

    fun setBaseScreenLoadingState() {
        Timber.tag("STATE").i("setBaseScreenLoadingState")
        _baseScreenState.value = BaseScreenState.Loading
    }

    fun setBaseScreenErrorState(errorData: TachyonAlertDialogUIModel) {
        Timber
            .tag("STATE")
            .i("setBaseScreenErrorState errorData = $errorData")
        _baseScreenState.value = BaseScreenState.Error(errorData)
    }

    fun <T> setBaseScreenSuccessState(data: T?) {
        Timber.tag("STATE").i("setBaseScreenSuccessState data = $data")
        _baseScreenState.value = BaseScreenState.Success(data)
    }

    fun resetBaseScreenState() {
        Timber.tag("STATE").i("resetBaseScreenState")
        _baseScreenState.value = BaseScreenState.Idle
    }

    fun logEventScreenView(message: String? = null) {
        logUtils.logInfo(
            action = LogAction.SCREEN_VIEW,
            message = message ?: ScreenHolder.getCurrentScreen(),
        )
    }

    fun logEventOnBackPressed() {
        logUtils.logInfo(
            action = LogAction.ON_BACK_PRESSED,
            message = ScreenHolder.getCurrentScreen(),
        )
    }

    fun logEventAlertDialog(errorMessage: String) {
        logUtils.logError(
            action = LogAction.ALERT_DIALOG,
            message = errorMessage,
        )
    }

    fun logEventOnBackMainMenu() {
        logUtils.logInfo(
            action = LogAction.ON_BACK_MAIN_MENU,
            message = ScreenHolder.getCurrentScreen(),
        )
    }
}
