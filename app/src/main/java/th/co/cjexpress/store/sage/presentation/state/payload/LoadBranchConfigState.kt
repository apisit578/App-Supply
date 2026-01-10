package th.co.cjexpress.store.sage.presentation.state.payload

data class LoadBranchConfigState(
    val isLoading: Boolean = false,
    val message: String? = null,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
)
