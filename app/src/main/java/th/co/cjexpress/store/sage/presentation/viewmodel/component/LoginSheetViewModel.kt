package th.co.cjexpress.store.sage.presentation.viewmodel.component

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.usecase.TrackLoginEventUseCase
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.domain.usecase.login.LoginUseCase
import th.co.cjexpress.store.sage.presentation.state.login.LoginState
import th.co.cjexpress.store.sage.utils.LogUtils
import th.co.cjexpress.store.sage.utils.ResourceUtils
import javax.inject.Inject

@HiltViewModel
class LoginSheetViewModel
    @Inject
    constructor(
        private val loginUseCase: LoginUseCase,
        private val loginEventUseCase: TrackLoginEventUseCase,
        private val resourceUtils: ResourceUtils,
        private val logUtils: LogUtils,
    ) : ViewModel() {
        private val _loginState = MutableStateFlow(LoginState())
        val loginState: StateFlow<LoginState> = _loginState

        fun login(
            username: String,
            password: String,
        ) {
            _loginState.value = LoginState(isLoggingIn = true)

            viewModelScope.launch {
                val tokenResp =
                    loginUseCase
                        .execute(
                            username = username,
                            password = password,
                            isRegenToken = false,
                        ).await()

//            Timber.tag("STATE").d("tokenResp -> $tokenResp")

                if (tokenResp?.error?.isEmpty() == true) {
                    _loginState.value = LoginState(isSuccess = true)
                    logUtils.logInfo(
                        action = LogAction.LOGIN_SUCCESS,
                        message =
                            String.format(
                                resourceUtils.getString(R.string.log_message_login_success),
                                username,
                            ),
                    )
                    loginEventUseCase.execute()
                } else {
                    _loginState.value = LoginState(error = tokenResp!!.description)
                    logUtils.logError(
                        action = LogAction.LOGIN_FAILED,
                        message = tokenResp.description,
                    )
                }
            }
        }

        fun resetLoginState() {
            _loginState.value = LoginState()
        }

        fun logEventOpenSheet() {
            logUtils.logInfo(
                action = LogAction.OPEN_LOGIN_SHEET,
                message = resourceUtils.getString(R.string.log_message_open_login_sheet),
            )
        }

        fun logEventClearUsername() {
            logUtils.logInfo(
                action = LogAction.CLEAR_INPUT_USERNAME,
                message = resourceUtils.getString(R.string.log_message_clear_input_username),
            )
        }

        fun logEventLoginFailed(
            isUsernameError: Boolean,
            isPasswordError: Boolean,
        ) {
            logUtils.logError(
                action = LogAction.LOGIN_FAILED,
                message =
                    if (isUsernameError && isPasswordError) {
                        "${
                            resourceUtils.getString(
                                R.string.text_username_required,
                            )
                        }/${resourceUtils.getString(R.string.text_password_required)}"
                    } else if (isUsernameError) {
                        resourceUtils.getString(R.string.text_username_required)
                    } else {
                        resourceUtils.getString(R.string.text_password_required)
                    },
            )
        }
    }
