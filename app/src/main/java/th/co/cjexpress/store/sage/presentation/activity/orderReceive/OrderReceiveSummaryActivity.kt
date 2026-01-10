package th.co.cjexpress.store.sage.presentation.activity.orderReceive

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive.OrderReceiveSummaryScreen
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveSummaryViewModel

@AndroidEntryPoint
class OrderReceiveSummaryActivity : BaseComponentActivity() {
    @Composable
    override fun BaseScreenContainer() {
        OrderReceiveSummaryScreen(
            viewModel = hiltViewModel<OrderReceiveSummaryViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = true,
            hasBottomAppBar = true,
            hasSuccessStateCallback = true,
        )
    }
}
