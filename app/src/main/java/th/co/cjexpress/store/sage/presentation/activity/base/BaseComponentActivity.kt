package th.co.cjexpress.store.sage.presentation.activity.base

import android.annotation.SuppressLint
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.WindowInsetsController
import androidx.activity.ComponentActivity
import androidx.activity.OnBackPressedCallback
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.toArgb
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.presentation.state.auth.AuthState
import th.co.cjexpress.store.sage.presentation.ui.component.LoginSheet
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.ScreenHolder
import th.co.cjexpress.store.sage.utils.UUIDUtils

abstract class BaseComponentActivity : ComponentActivity() {
    private val preventBackPress = true
    open val statusBarColor: Color = TachyonDictionary.surface.static._default2

    @Composable
    abstract fun BaseScreenContainer()

    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        UUIDUtils.setCurrentMenuType(this.javaClass.simpleName)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        onBackPressedDispatcher.addCallback(
            owner = this,
            onBackPressedCallback =
                object : OnBackPressedCallback(preventBackPress) {
                    override fun handleOnBackPressed() {
                        // do nothing to prevent back press from working.
                    }
                },
        )

        setContent {
            SageTheme {
                LaunchedEffect(Unit) {
                    setStatusBarAppearance(statusBarColor)
                }

                UnauthorizedStateObserver()
                BaseScreenContainer()
            }
        }
    }

    @Composable
    private fun UnauthorizedStateObserver() {
        val unauthorizedState by AuthState.unauthorizedState.collectAsState()
        if (unauthorizedState) {
            LoginSheet(
                username = AppDependencyProvider.getProviderBuilder().getAppConfig().username,
                onDismissRequest = { },
            ) {
                AuthState.emitUnauthorizedEvent(false)
            }
        }
    }

    private fun setStatusBarAppearance(color: Color) {
        val isLightBackground = color.luminance() > 0.5f
        window.statusBarColor = color.toArgb()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.setSystemBarsAppearance(
                if (isLightBackground) WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS else 0,
                WindowInsetsController.APPEARANCE_LIGHT_STATUS_BARS,
            )
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                if (isLightBackground) {
                    View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR
                } else {
                    0
                }
        }
    }

    override fun onResume() {
        super.onResume()
        ScreenHolder.setScreen(this.javaClass.simpleName)
    }
}
