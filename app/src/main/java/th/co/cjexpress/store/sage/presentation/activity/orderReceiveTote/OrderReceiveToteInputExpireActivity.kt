package th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote

import android.os.Bundle
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.orderReceiveTote.OrderReceiveToteInputExpireScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme

@AndroidEntryPoint
class OrderReceiveToteInputExpireActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
    }

    @Composable
    override fun BaseScreenContainer() {
        SageTheme {
            OrderReceiveToteInputExpireScreen()
        }
    }
}
