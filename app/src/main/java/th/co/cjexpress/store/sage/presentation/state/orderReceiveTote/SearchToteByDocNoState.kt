package th.co.cjexpress.store.sage.presentation.state.orderReceiveTote

import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailUIModel

data class SearchToteByDocNoState(
    val isLoading: Boolean = false,
    val sdToteDocDetail: SdToteDocsDetailUIModel? = null,
    val errorMessage: String? = null,
    val isSuccess: Boolean = false,
    val warningMessage: String? = null,
)
