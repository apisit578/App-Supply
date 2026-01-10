package th.co.cjexpress.store.sage.presentation.activity.reprintQrFresh

import android.os.Bundle
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.reprintQrFresh.ReprintQrFreshItemDetailScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme
import timber.log.Timber

@AndroidEntryPoint
class ReprintQrFreshItemDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("STATE").d("ReprintQrFreshItemDetailActivity oncreate")
    }

    @Composable
    override fun BaseScreenContainer() {
        SageTheme {
            ReprintQrFreshItemDetailScreen()
        }
    }
}
