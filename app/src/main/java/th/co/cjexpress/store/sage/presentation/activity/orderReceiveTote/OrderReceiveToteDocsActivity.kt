package th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote

import android.os.Bundle
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.orderReceiveTote.OrderReceiveToteDocsScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme
import timber.log.Timber

@AndroidEntryPoint
class OrderReceiveToteDocsActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("STATE").d("OrderReceiveToteDocsActivity oncreate")
//        enableEdgeToEdge()
    }

    @Composable
    override fun BaseScreenContainer() {
        SageTheme {
            OrderReceiveToteDocsScreen()
        }
    }
}
