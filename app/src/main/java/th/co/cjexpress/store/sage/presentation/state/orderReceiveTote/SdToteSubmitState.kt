package th.co.cjexpress.store.sage.presentation.state.orderReceiveTote

import th.co.cjexpress.store.sage.domain.model.orderReceiveTote.SdToteSubmitModel

data class SdToteSubmitState(
    val isLoading: Boolean = false,
    val result: SdToteSubmitModel? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
)
