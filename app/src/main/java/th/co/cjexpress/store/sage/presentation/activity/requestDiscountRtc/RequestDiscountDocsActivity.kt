package th.co.cjexpress.store.sage.presentation.activity.requestDiscountRtc

import android.os.Bundle
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.requestDiscountRtc.RequestDiscountDocsScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme
import timber.log.Timber

@AndroidEntryPoint
class RequestDiscountDocsActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.tag("STATE").d("RequestDiscountDocsActivity oncreate")

//        enableEdgeToEdge()
    }

    @Composable
    override fun BaseScreenContainer() {
        SageTheme {
            RequestDiscountDocsScreen()
        }
    }
}
