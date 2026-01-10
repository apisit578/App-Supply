package th.co.cjexpress.store.sage.presentation.activity.mainMenu

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.mainMenu.MainMenuScreen
import th.co.cjexpress.store.sage.presentation.viewmodel.mainMenu.MainMenuViewModel
import timber.log.Timber

@AndroidEntryPoint
class MainMenuActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("STATE").d("MainMenuActivity onCreate")
    }

    @Composable
    override fun BaseScreenContainer() {
        MainMenuScreen(
            viewModel = hiltViewModel<MainMenuViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = true,
            hasBottomAppBar = false,
            hasSuccessStateCallback = true,
        )
    }
}
