package th.co.cjexpress.store.sage.presentation.activity.reprintQrFresh

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.reprintQrFresh.ReprintQrFreshItemsScreen
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemsViewModel

@AndroidEntryPoint
class ReprintQrFreshItemsActivity : BaseComponentActivity() {
    @Composable
    override fun BaseScreenContainer() {
        ReprintQrFreshItemsScreen(
            viewModel = hiltViewModel<ReprintQrFreshItemsViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = true,
            hasBottomAppBar = true,
            hasSuccessStateCallback = true,
        )
    }
}
