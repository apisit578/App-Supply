package th.co.cjexpress.store.sage.utils

import androidx.compose.foundation.text.KeyboardActionScope
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.presentation.viewmodel.component.DebounceActionHandlerViewModel

/**
 * Makes its content clickable, with a debounce mechanism with coroutines
 * to prevent multiple rapid clicks.
 *
 * @param debounceDuration: The amount of time (in milliseconds) to wait before allowing the next click.
 * @param coroutineScope uses a Coroutine Job to cancel rapid clicks
 * if a new click comes in before the debounce interval expires.
 */

class DebounceActionHandler(
    private val debounceDuration: Long,
    private val coroutineScope: CoroutineScope,
) {
    private var debounceJob: Job? = null

    fun execute(actionToExecute: suspend () -> Unit) {
        // If a previous click job is still active, the new click is ignored.
        if (debounceJob?.isActive == true) {
            return
        }

        debounceJob =
            coroutineScope.launch {
                try {
                    actionToExecute()
                } finally {
                    // Coroutine delays for debounceDuration to prevent new clicks.
                    delay(debounceDuration)

                    // Once the delay is over, resets the debounceJob to null, allowing future clicks.
                    if (coroutineScope.isActive && debounceJob == this) {
                        debounceJob = null
                    }
                }
            }
    }
}

@Composable
fun safeClickWithDebounce(
    debounceDuration: Long? = null,
    isEnable: Boolean = true,
    action: suspend () -> Unit,
): () -> Unit {
    val viewModel: DebounceActionHandlerViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val debounceActionHandler =
        remember(debounceDuration, coroutineScope) {
            DebounceActionHandler(
                debounceDuration = debounceDuration ?: viewModel.getDefaultDebounce(),
                coroutineScope = coroutineScope,
            )
        }
    return {
        if (isEnable) {
            debounceActionHandler.execute {
                action()
            }
        } else {
            coroutineScope.launch {
                action()
            }
        }
    }
}

@Composable
fun safeKeyboardActionWithDebounce(
    debounceDuration: Long? = null,
    isEnable: Boolean = true,
    action: suspend KeyboardActionScope.() -> Unit,
): KeyboardActionScope.() -> Unit {
    val viewModel: DebounceActionHandlerViewModel = hiltViewModel()
    val coroutineScope = rememberCoroutineScope()
    val debounceActionHandler =
        remember(debounceDuration, coroutineScope) {
            DebounceActionHandler(
                debounceDuration = debounceDuration ?: viewModel.getKeyboardDoneDebounce(),
                coroutineScope = coroutineScope,
            )
        }

    return {
        if (isEnable) {
            debounceActionHandler.execute {
                delay(viewModel.getBeforeKeyboardOnDoneDelay())
                action()
            }
        } else {
            coroutineScope.launch {
                delay(viewModel.getBeforeKeyboardOnDoneDelay())
                action()
            }
        }
    }
}
