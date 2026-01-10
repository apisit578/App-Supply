package th.co.cjexpress.store.sage.presentation.viewmodel.login

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.FeatureConstants
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.usecase.GetAppConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.appconfig.CheckFeatureEnableUseCase
import th.co.cjexpress.store.sage.domain.usecase.branch_master.CheckBranchIpUseCase
import th.co.cjexpress.store.sage.domain.usecase.payload.LoadBranchConfigUseCase
import th.co.cjexpress.store.sage.domain.usecase.payload.LoadGlobalConfigUseCase
import th.co.cjexpress.store.sage.presentation.state.branchMapping.BranchIpState
import th.co.cjexpress.store.sage.presentation.state.payload.LoadBranchConfigState
import th.co.cjexpress.store.sage.presentation.state.payload.LoadGlobalConfigState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel
import th.co.cjexpress.store.sage.utils.ResourceUtils
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class LoginViewModel
    @Inject
    constructor(
        private val loadGlobalConfigUseCase: LoadGlobalConfigUseCase,
        private val checkFeatureEnableUseCase: CheckFeatureEnableUseCase,
        private val checkBranchIpUseCase: CheckBranchIpUseCase,
        private val loadBranchConfigUseCase: LoadBranchConfigUseCase,
        private val getAppConfigUseCase: GetAppConfigUseCase,
        private val resourceUtils: ResourceUtils,
    ) : BaseViewModel() {
        private val delayTime = 500L

        private val _loadGlobalConfigState = MutableStateFlow<LoadGlobalConfigState?>(null)
        private val _branchIpState = MutableStateFlow<BranchIpState?>(null)
        val branchIpState: StateFlow<BranchIpState?> = _branchIpState

        private val _loadBranchConfigState = MutableStateFlow<LoadBranchConfigState?>(null)
        val loadBranchConfigState: StateFlow<LoadBranchConfigState?> = _loadBranchConfigState

        val initializingIndicator =
            combine(
                _loadGlobalConfigState,
                _branchIpState,
                _loadBranchConfigState,
            ) { loadGlobalConfigState, branchIpState, loadBranchConfigState ->
                loadGlobalConfigState?.isLoading == true ||
                    branchIpState?.isLoading == true ||
                    loadBranchConfigState?.isLoading == true
            }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, false)

        val initializingMessage =
            combine(
                _loadGlobalConfigState,
                _branchIpState,
                _loadBranchConfigState,
            ) { loadGlobalConfigState, branchIpState, loadBranchConfigState ->
                when {
                    loadGlobalConfigState?.message?.isNotEmpty() == true -> loadGlobalConfigState.message
                    branchIpState?.message?.isNotEmpty() == true -> branchIpState.message
                    loadBranchConfigState?.message?.isNotEmpty() == true -> loadBranchConfigState.message
                    else -> ""
                }
            }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, "")

        val initializingErrorState =
            combine(
                _loadGlobalConfigState,
                _branchIpState,
                _loadBranchConfigState,
            ) { loadGlobalConfigState, branchIpState, loadBranchConfigState ->
                loadGlobalConfigState?.isError == true ||
                    branchIpState?.isError == true ||
                    loadBranchConfigState?.isError == true
            }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, false)

        val readyToLogin =
            combine(
                _loadGlobalConfigState,
                _branchIpState,
                _loadBranchConfigState,
            ) { loadGlobalConfigState, branchIpState, loadBranchConfigState ->
                (
                    loadGlobalConfigState?.isSuccess == true &&
                        branchIpState?.isSuccess == true
                ) &&
                    (
                        !(
                            loadBranchConfigState?.isLoading
                                ?: false
                        ) &&
                            !(loadBranchConfigState?.isSuccess ?: false)
                    )
            }.stateIn(viewModelScope, SharingStarted.Companion.Lazily, false)

        var showLoginSheet by mutableStateOf(false)
            private set

        fun showLoginSheet(state: Boolean) {
            showLoginSheet = state
        }

        fun loadGlobalConfig() {
            viewModelScope.launch {
                _loadGlobalConfigState.value =
                    LoadGlobalConfigState(
                        isLoading = true,
                        message = resourceUtils.getString(R.string.text_loading_global_config),
                    )
                delay(delayTime)

                _loadGlobalConfigState.value =
                    when (val response = loadGlobalConfigUseCase.execute()) {
                        is Result.Success -> {
                            checkBranchIP()
                            LoadGlobalConfigState(isSuccess = true)
                        }

                        is Result.Error -> {
                            LoadGlobalConfigState(
                                isError = true,
                                message =
                                    response.cause?.message
                                        ?: resourceUtils.getString(R.string.text_error_global_config),
                            )
                        }
                    }
            }
        }

        private fun checkBranchIP() {
            viewModelScope.launch {
                _branchIpState.value =
                    BranchIpState(
                        isLoading = true,
                        message = resourceUtils.getString(R.string.text_checking_branch),
                    )
                delay(delayTime)

                if (checkFeatureEnableUseCase.execute(FeatureConstants.CHECK_BRANCH_IP)) {
                    _branchIpState.value =
                        when (val result = checkBranchIpUseCase.execute()) {
                            is Result.Success -> {
                                BranchIpState(isSuccess = true, data = result.data)
                            }

                            is Result.Error -> {
                                if (checkFeatureEnableUseCase.execute(FeatureConstants.BRANCH_IP_STRICT_MODE)) {
                                    BranchIpState(
                                        isError = true,
                                        message =
                                            result.cause?.message
                                                ?: resourceUtils.getString(R.string.text_error_checking_branch),
                                    )
                                } else {
                                    BranchIpState(isSuccess = true)
                                }
                            }
                        }
                } else {
                    _branchIpState.value = BranchIpState(isSuccess = true)
                    checkBranchIpUseCase.updateBranchInfo(null)
                }
            }
        }

        fun loadBranchConfig() {
            viewModelScope.launch {
                _loadBranchConfigState.value =
                    LoadBranchConfigState(
                        isLoading = true,
                        message = resourceUtils.getString(R.string.text_loading_branch_config),
                    )
                delay(delayTime)

                val branchCode = getAppConfigUseCase.getBranchInfo()?.branchCode ?: ""
                if (branchCode.isNotEmpty()) {
                    _loadBranchConfigState.value =
                        when (val response = loadBranchConfigUseCase.execute(branchCode)) {
                            is Result.Success -> {
                                LoadBranchConfigState(isSuccess = true)
                            }

                            is Result.Error -> {
                                Timber.tag("DEV").e(
                                    "Force Success : ${response.cause.message}",
                                )
                                LoadBranchConfigState(isSuccess = true)
                            }
                        }
                } else {
                    LoadBranchConfigState(
                        message = resourceUtils.getString(R.string.text_branch_code_not_found),
                    )
                }
            }
        }
    }
