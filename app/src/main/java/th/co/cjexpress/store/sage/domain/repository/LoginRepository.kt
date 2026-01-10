package th.co.cjexpress.store.sage.domain.repository

import th.co.cjexpress.store.sage.domain.callback.AuthTokenCallback

interface LoginRepository {
    suspend fun getAuthToken(
        username: String,
        password: String,
        callback: AuthTokenCallback,
    )
}
