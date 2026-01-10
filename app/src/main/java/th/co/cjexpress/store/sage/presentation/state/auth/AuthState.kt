package th.co.cjexpress.store.sage.presentation.state.auth

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

object AuthState {
    private val _unauthorizedState = MutableStateFlow(false)
    val unauthorizedState: StateFlow<Boolean> = _unauthorizedState

    fun emitUnauthorizedEvent(state: Boolean) {
        _unauthorizedState.value = state
    }
}