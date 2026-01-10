package th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice

import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import th.co.cjexpress.store.sage.presentation.activity.base.BaseComponentActivity
import th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice.PurchaseInvoiceListPIItemListScreenUI

@AndroidEntryPoint
class PurchaseInvoiceListPIActivity : BaseComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @Composable
    override fun BaseScreenContainer() {
        PurchaseInvoiceListPIItemListScreenUI(viewModel = hiltViewModel()).InitUI()
    }
}
