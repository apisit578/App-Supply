package th.co.cjexpress.store.sage.presentation.ui.screen.base

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.res.stringResource
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScaffold
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel

data class TopAppBarConfigs(
    val titleTextRes: Int,
    val titleAlignment: Alignment.Horizontal = Alignment.Start,
    val showNavigationIcon: Boolean = true,
    val onNavigationIconClick: () -> Unit,
)

data class BaseScreenConfig(
    val hasTopAppBar: Boolean,
    val hasBottomAppBar: Boolean,
    val hasSuccessStateCallback: Boolean,
)

abstract class BaseScreen<VM : BaseViewModel>(
    val viewModel: VM,
) {
    @Composable
    abstract fun ScreenContent()

    @Composable
    open fun SuccessStateCallback(data: Any?) {
        // Default empty implementation
    }

    @Composable
    open fun BottomAppBarContainer() {
        // Default empty implementation
    }

    @Composable
    open fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        configs?.let {
            TachyonTopAppBar(
                titleText = stringResource(id = it.titleTextRes),
                titleAlignment = it.titleAlignment,
                showNavigationIcon = it.showNavigationIcon,
                onNavigationIconClick = it.onNavigationIconClick,
            )
        }
    }

    @Composable
    private fun BaseScreenStateObserver(
        baseScreenState: BaseScreenState,
        successStateCallback: @Composable ((Any?) -> Unit)?,
    ) {
        when (baseScreenState) {
            is BaseScreenState.Idle -> {}
            is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
            is BaseScreenState.Error -> {
                viewModel.logEventAlertDialog(baseScreenState.errorData.messageText)
                TachyonAlertDialog(dialogData = baseScreenState.errorData)
            }
            is BaseScreenState.Success<*> -> successStateCallback?.invoke(baseScreenState.data)
        }
    }

    @Composable
    fun ScreenContainer(
        hasTopAppBar: Boolean,
        hasBottomAppBar: Boolean,
        hasSuccessStateCallback: Boolean,
    ) {
        val baseScreenState by viewModel.baseScreenState.collectAsState()
        LaunchedEffect(Unit) {
            viewModel.logEventScreenView()
        }

        TachyonScaffold(
            topAppBarContainer =
                if (hasTopAppBar) {
                    { TopAppBarContainer(null) }
                } else {
                    null
                },
            bottomAppBarContainer =
                if (hasBottomAppBar) {
                    { BottomAppBarContainer() }
                } else {
                    null
                },
            content = {
                BaseScreenStateObserver(
                    baseScreenState = baseScreenState,
                    successStateCallback =
                        if (hasSuccessStateCallback) {
                            { SuccessStateCallback(it) }
                        } else {
                            null
                        },
                )
                ScreenContent()
            },
        )
    }
}
