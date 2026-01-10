package th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice.PurchaseInvoiceListPOItemListScreenUI
import timber.log.Timber

@AndroidEntryPoint
class PurchaseInvoiceListPOActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.Forest.tag("STATE").d("PurchaseInvoiceListPOActivity onCreate")
    }

    @Composable
    override fun BaseScreenContainer() {
        PurchaseInvoiceListPOItemListScreenUI(viewModel = hiltViewModel()).InitUI()
    }
}
