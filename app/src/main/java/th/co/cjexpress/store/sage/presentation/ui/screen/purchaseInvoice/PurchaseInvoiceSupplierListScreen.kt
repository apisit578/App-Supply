package th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceListPIActivity
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierListItemUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.ItemListScreenProvider
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnEmptyState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnItem
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnPullToRefreshState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TopAppBarContainerParams
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceSupplierListViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceSupplierListViewModel.UIState

class PurchaseInvoiceSupplierListScreenUI(
    private val viewModel: PurchaseInvoiceSupplierListViewModel,
) : ItemListScreenProvider<PurchaseInvoiceSupplierListItemUIModel, PurchaseInvoiceSupplierListViewModel>() {
    @Composable
    override fun InitUI() {
        val context = LocalContext.current
        val state = viewModel.purchaseInvoiceSupplierListState.collectAsState()
        val items = state.value.data?.supplierList ?: emptyList()

        LaunchedEffect(Unit) {
            viewModel.resetPurchaseInvoiceLiveData()
            viewModel.getPurchaseInvoiceSupplierList()
            viewModel.logEventScreenView()
        }

        super.ScreenContainer(
            viewModel = viewModel,
            successStateCallback = { data ->
                SuccessStateCallback(data)
            },
            topBarParams =
                TopAppBarContainerParams(
                    titleTextRes = R.string.text_purchase_invoice_title,
                    onNavigationIconClick = {
                        viewModel.logEventOnBackPressed()
                        context.startActivity(Intent(context, MainMenuActivity::class.java)).also {
                            (context as ComponentActivity).finish()
                        }
                    },
                ),
            bottomBar = {},
            content = {
                TachyonBarcodeInputField(
                    label = stringResource(R.string.text_search_supplier),
                    placeholder = stringResource(R.string.text_please_fill_or_scan),
                    decodeType = DecodeType.NONE,
                ) { decoded ->
                    viewModel.getFilterSupplierList(decoded.barcode)
                }

                ItemListContainer(
                    items = items,
                    onItemClick = { supplierCode ->
                        viewModel.getSupplierByCode(searchCode = supplierCode.toString())
                    },
                )
            },
        )
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current
        when (data) {
            is UIState.NavToPIList -> {
                context
                    .startActivity(
                        Intent(context, PurchaseInvoiceListPIActivity::class.java),
                    ).also {
                        (context as ComponentActivity).finish()
                    }
            }
        }
    }

    @Composable
    override fun ItemDetailContainer(
        item: PurchaseInvoiceSupplierListItemUIModel,
        onItemClick: (item: Any) -> Unit,
    ) {
        TachyonLazyColumnItem(
            onClick = {
                onItemClick(item.supplierCode)
            },
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                verticalArrangement =
                    Arrangement.spacedBy(
                        space = TachyonDictionary.global.spacing._xs,
                    ),
            ) {
                TachyonText(
                    text = item.supplierCode,
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text = item.supplierName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }
    }

    @Composable
    override fun ItemListContainer(
        items: List<PurchaseInvoiceSupplierListItemUIModel>,
        onItemClick: (item: Any) -> Unit,
    ) {
        val pullingState by viewModel.baseScreenState.collectAsState()

        TachyonLazyColumn(
            modifier = Modifier.fillMaxSize(),
            pullToRefreshState =
                TachyonLazyColumnPullToRefreshState(
                    pullToRefreshLoadingState = pullingState is BaseScreenState.Loading,
                    pullToRefreshCallback = {
                        viewModel.resetPurchaseInvoiceLiveData()
                        viewModel.getPurchaseInvoiceSupplierList()
                        viewModel.clearInputPurchaseInvoiceState()
                    },
                ),
            rowHeaderText = {
                TachyonText(
                    text =
                        stringResource(
                            R.string.text_format_list_supplier,
                            items.size,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            },
            items = items,
            itemKey = { supplier -> supplier.supplierCode },
            emptyState =
                TachyonLazyColumnEmptyState(
                    painter = painterResource(id = R.drawable.icon_empty_document),
                    descriptionText = stringResource(id = R.string.text_list_supplier_empty),
                ),
        ) { item ->
            ItemDetailContainer(item, onItemClick = { supplierCode ->
                onItemClick(supplierCode)
            })
        }
    }

    @Composable
    override fun BottomAppBarContainer() {
    }
}
