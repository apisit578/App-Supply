package th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice

import android.os.Bundle
import androidx.compose.runtime.Composable
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice.PurchaseInvoiceProductsScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.SageTheme

@AndroidEntryPoint
class PurchaseInvoiceItemsActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun BaseScreenContainer() {
        SageTheme {
            PurchaseInvoiceProductsScreen()
        }
    }
}
