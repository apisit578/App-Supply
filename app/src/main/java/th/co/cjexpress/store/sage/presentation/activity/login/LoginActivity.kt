package th.co.cjexpress.store.sage.presentation.activity.login

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.login.LoginScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.login.LoginViewModel
import th.co.cjexpress.store.sage.utils.CrashlyticsLoggerUtils
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity : BaseComponentActivity() {
    override val statusBarColor = TachyonDictionary.surface.static._default1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.tag("STATE").d("LoginActivity onCreate")
        CrashlyticsLoggerUtils.initCrashlytics()

        enableEdgeToEdge()
    }

    @Composable
    override fun BaseScreenContainer() {
        LoginScreen(
            viewModel = hiltViewModel<LoginViewModel>(),
        ).ScreenContainer(
            hasTopAppBar = false,
            hasBottomAppBar = true,
            hasSuccessStateCallback = false,
        )
    }
}
