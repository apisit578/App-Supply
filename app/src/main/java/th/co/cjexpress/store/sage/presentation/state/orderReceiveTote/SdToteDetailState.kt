package th.co.cjexpress.store.sage.presentation.state.orderReceiveTote

data class SdToteDetailState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)
