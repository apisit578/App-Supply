package th.co.cjexpress.store.sage.presentation.state.login

data class LoginState(
    val isLoggingIn: Boolean = false,
    val error: String? = null,
    val isSuccess: Boolean = false,
)