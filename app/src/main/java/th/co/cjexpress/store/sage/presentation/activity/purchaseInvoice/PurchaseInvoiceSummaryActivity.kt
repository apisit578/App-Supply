package th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice

import android.os.Bundle
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice.PurchaseInvoiceSummaryScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme
import timber.log.Timber

@AndroidEntryPoint
class PurchaseInvoiceSummaryActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.tag("STATE").d("PurchaseInvoiceSummaryActivity onCreate")
//        enableEdgeToEdge()
    }

    @Composable
    override fun BaseScreenContainer() {
        SageTheme {
            PurchaseInvoiceSummaryScreen()
        }
    }
}
