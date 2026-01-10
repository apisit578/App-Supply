package th.co.cjexpress.store.sage.presentation.activity.requestDiscountRtc

import android.os.Bundle
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.requestDiscountRtc.RequestDiscountItemDetailScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme
import timber.log.Timber

@AndroidEntryPoint
class RequestDiscountItemDetailActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Timber.tag("STATE").d("RequestDiscountItemDetailActivity oncreate")

//        enableEdgeToEdge()
    }

    @Composable
    override fun BaseScreenContainer() {
        SageTheme {
            RequestDiscountItemDetailScreen()
        }
    }
}
