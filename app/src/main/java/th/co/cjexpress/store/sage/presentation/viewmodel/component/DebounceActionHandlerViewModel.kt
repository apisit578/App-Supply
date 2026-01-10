package th.co.cjexpress.store.sage.presentation.viewmodel.component

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import th.co.cjexpress.store.sage.domain.usecase.appconfig.FeatureConfigsUseCase
import javax.inject.Inject

@HiltViewModel
class DebounceActionHandlerViewModel
    @Inject
    constructor(
        private val featureConfigsUseCase: FeatureConfigsUseCase,
    ) : ViewModel() {
        fun getBeforeKeyboardOnDoneDelay(): Long = featureConfigsUseCase.getDelayConfig().beforeKeyboardOnDone

        fun getKeyboardDoneDebounce(): Long = featureConfigsUseCase.getDelayConfig().keyboardOnDoneDebounceMs

        fun getDefaultDebounce(): Long = featureConfigsUseCase.getDelayConfig().defaultDelayMs
    }
