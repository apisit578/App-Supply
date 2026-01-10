package th.co.cjexpress.store.sage.presentation.activity.orderReceive

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive.OrderReceiveItemDetailScreen
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemDetailViewModel

@AndroidEntryPoint
class OrderReceiveItemDetailActivity : BaseComponentActivity() {
    @Composable
    override fun BaseScreenContainer() {
        OrderReceiveItemDetailScreen(
            viewModel = hiltViewModel<OrderReceiveItemDetailViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = true,
            hasBottomAppBar = true,
            hasSuccessStateCallback = true,
        )
    }
}
