package th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.orderReceiveTote.OrderReceiveToteSummaryScreen
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteSummaryViewModel

@AndroidEntryPoint
class OrderReceiveToteSummaryActivity : BaseComponentActivity() {
    @Composable
    override fun BaseScreenContainer() {
        OrderReceiveToteSummaryScreen(
            viewModel = hiltViewModel<OrderReceiveToteSummaryViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = true,
            hasSuccessStateCallback = true,
            hasBottomAppBar = true,
        )
    }
}
