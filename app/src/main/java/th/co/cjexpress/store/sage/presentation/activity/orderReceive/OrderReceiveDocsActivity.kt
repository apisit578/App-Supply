package th.co.cjexpress.store.sage.presentation.activity.orderReceive

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive.OrderReceiveDocsScreen
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveDocsViewModel

@AndroidEntryPoint
class OrderReceiveDocsActivity : BaseComponentActivity() {
    @Composable
    override fun BaseScreenContainer() {
        OrderReceiveDocsScreen(
            viewModel = hiltViewModel<OrderReceiveDocsViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = true,
            hasBottomAppBar = true,
            hasSuccessStateCallback = true,
        )
    }
}
