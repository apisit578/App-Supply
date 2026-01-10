package th.co.cjexpress.store.sage.presentation.state.branchMapping

import th.co.cjexpress.store.sage.domain.model.BranchIpModel

data class BranchIpState(
    val isLoading: Boolean = false,
    val data: BranchIpModel? = null,
    val message: String? = null,
    val isSuccess: Boolean = false,
    val isError: Boolean = false,
)