package th.co.cjexpress.store.sage.presentation.activity.orderReceive

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive.OrderReceiveItemsScreen
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemsViewModel

@AndroidEntryPoint
class OrderReceiveItemsActivity : BaseComponentActivity() {
    @Composable
    override fun BaseScreenContainer() {
        OrderReceiveItemsScreen(
            viewModel = hiltViewModel<OrderReceiveItemsViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = true,
            hasBottomAppBar = true,
            hasSuccessStateCallback = true,
        )
    }
}
