package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.stringResource
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.viewmodel.base.BaseViewModel

data class TopAppBarContainerParams(
    val titleTextRes: Int,
    val onNavigationIconClick: () -> Unit,
)

interface TopAppBarContainerProvider {
    @Composable
    fun TopAppBarContainer(params: TopAppBarContainerParams)
}

// data class ItemListContainerParams<T>(
//    val modifier: Modifier,
//    val pullToRefreshState: TachyonLazyColumnPullToRefreshState?,
//    val lazyColumnState: LazyListState,
//    val headerItemSizeTitleText: Int,
//    val optionalRowHeaderText: @Composable (() -> Unit)?,
//    val items: List<T>,
//    val itemKey: (T) -> Any,
//    val emptyState: TachyonLazyColumnEmptyState?,
// )

interface ItemListContainerProvider<T> {
    @Composable
    fun ItemListContainer(
        items: List<T>,
        onItemClick: (item: Any) -> Unit,
    )

    @Composable
    fun ItemDetailContainer(
        item: T,
        onItemClick: (item: Any) -> Unit,
    )
}

interface BottomAppBarContainerProvider {
    @Composable
    fun BottomAppBarContainer()
}

interface SuccessStateCallbackProvider {
    @Composable
    fun SuccessStateCallback(data: Any?)
}

// abstract class BaseScreenStateObserverProvider<VM : BaseViewModel> {
//    @Composable
//    protected fun BaseScreenStateObserver(
//        baseScreenState: BaseScreenState?,
//        successStateCallback: @Composable ((Any?) -> Unit)?,
//    ) {
//        when (baseScreenState) {
//            is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
//            is BaseScreenState.Success<*> -> successStateCallback?.invoke(baseScreenState.data)
//            is BaseScreenState.Error -> TachyonAlertDialog(dialogData = baseScreenState.errorData)
//            else -> {}
//        }
//    }
//
//    @Composable
//    abstract fun ScreenContainer(
//        viewModel: VM,
//        successStateCallback: @Composable ((data: Any?) -> Unit)?,
//        topBarParams: TopAppBarContainerParams,
//        bottomBar: @Composable (() -> Unit),
//        content: @Composable () -> Unit,
//    )
// }

abstract class ItemListScreenProvider<T, VM : BaseViewModel> :
//    BaseScreenStateObserverProvider<VM>(),
    SuccessStateCallbackProvider,
    TopAppBarContainerProvider,
    ItemListContainerProvider<T>,
    BottomAppBarContainerProvider {
    @Composable
    abstract fun InitUI()

    @Composable
    private fun BaseScreenStateObserver(
        viewModel: VM,
        baseScreenState: BaseScreenState?,
        successStateCallback: @Composable ((Any?) -> Unit)?,
    ) {
        when (baseScreenState) {
            is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
            is BaseScreenState.Success<*> -> successStateCallback?.invoke(baseScreenState.data)
            is BaseScreenState.Error -> {
                viewModel.logEventAlertDialog(baseScreenState.errorData.messageText)
                TachyonAlertDialog(dialogData = baseScreenState.errorData)
            }
            else -> {}
        }
    }

    @Composable
    fun ScreenContainer(
        viewModel: VM,
        successStateCallback: @Composable ((Any?) -> Unit)?,
        topBarParams: TopAppBarContainerParams,
        bottomBar: @Composable () -> Unit,
        content: @Composable (() -> Unit),
    ) {
        val baseScreenState by viewModel.baseScreenState.collectAsState()

        TachyonScaffold(
            topAppBarContainer = {
                TopAppBarContainer(params = topBarParams)
            },
            bottomAppBarContainer = { bottomBar() },
            content = {
                BaseScreenStateObserver(
                    viewModel = viewModel,
                    baseScreenState = baseScreenState,
                    successStateCallback = successStateCallback,
                )
                content()
            },
        )
    }

    @Composable
    override fun TopAppBarContainer(params: TopAppBarContainerParams) {
        TachyonTopAppBar(
            titleText = stringResource(id = params.titleTextRes),
            onNavigationIconClick = params.onNavigationIconClick,
        )
    }
}
