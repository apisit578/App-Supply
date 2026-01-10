package th.co.cjexpress.store.sage.domain.callback

import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.model.auth.AuthBranchListModel

interface AuthTokenCallback {
    fun onAuthSuccess(response: AuthTokenRespModel?)

    fun onAuthError(response: AuthTokenRespModel?)

    fun onRequireBranch(
        lockBranch: Boolean,
        response: AuthTokenRespModel?,
        branches: AuthBranchListModel,
    ) { }
}
