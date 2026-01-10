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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceItemsActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceListPOActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceSupplierListActivity
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceItemPIUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.ValidateInvoiceNumberDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.ItemListScreenProvider
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryFilled
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnEmptyState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnItem
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnPullToRefreshState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TopAppBarContainerParams
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceListPIViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceListPIViewModel.UIState

class PurchaseInvoiceListPIItemListScreenUI(
    private val viewModel: PurchaseInvoiceListPIViewModel,
) : ItemListScreenProvider<PurchaseInvoiceItemPIUIModel, PurchaseInvoiceListPIViewModel>() {
    @Composable
    override fun InitUI() {
        val items by viewModel.piList.collectAsState()
        val context = LocalContext.current

        LaunchedEffect(Unit) {
            viewModel.logEventScreenView()
            viewModel.loadPurchaseInvoice()
        }

        super.ScreenContainer(
            viewModel = viewModel,
            successStateCallback = { data -> SuccessStateCallback(data) },
            topBarParams =
                TopAppBarContainerParams(
                    titleTextRes = R.string.text_title_purchase_invoice,
                    onNavigationIconClick = {
                        viewModel.logEventOnBackPressed()
                        viewModel.onBackClick {
                            context
                                .startActivity(
                                    Intent(
                                        context,
                                        PurchaseInvoiceSupplierListActivity::class.java,
                                    ),
                                ).also {
                                    (context as ComponentActivity).finish()
                                }
                        }
                    },
                ),
            bottomBar = { BottomAppBarContainer() },
            content = {
                ItemListContainer(
                    items = items,
                ) { piNo ->
                    viewModel.getPurchaseInvoiceDetail(piNo as String)
                }
            },
        )
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current

        when (data) {
            is UIState.NavToPOList -> {
                context
                    .startActivity(
                        Intent(context, PurchaseInvoiceListPOActivity::class.java),
                    ).also {
                        (context as ComponentActivity).finish()
                    }
            }

            is UIState.NavToPIDetail -> {
                context
                    .startActivity(
                        Intent(context, PurchaseInvoiceItemsActivity::class.java),
                    ).also {
                        (context as ComponentActivity).finish()
                    }
            }
        }
    }

    @Composable
    override fun ItemListContainer(
        items: List<PurchaseInvoiceItemPIUIModel>,
        onItemClick: (Any) -> Unit,
    ) {
        val pullingState by viewModel.baseScreenState.collectAsState()

        TachyonLazyColumn(
            modifier = Modifier.fillMaxSize(),
            pullToRefreshState =
                TachyonLazyColumnPullToRefreshState(
                    pullToRefreshLoadingState = pullingState is BaseScreenState.Loading,
                    pullToRefreshCallback = { viewModel.loadPurchaseInvoice() },
                ),
            rowHeaderText = {
                TachyonText(
                    text =
                        stringResource(
                            R.string.text_document_list,
                            items.size,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            },
            emptyState =
                TachyonLazyColumnEmptyState(
                    painter = painterResource(R.drawable.icon_empty_document),
                    descriptionText = stringResource(R.string.text_docs_list_empty),
                ),
            items = items,
            itemKey = { item -> item.piNo },
        ) { item ->
            ItemDetailContainer(item, onItemClick = { piNo -> onItemClick(piNo) })
        }
    }

    @Composable
    override fun ItemDetailContainer(
        item: PurchaseInvoiceItemPIUIModel,
        onItemClick: (Any) -> Unit,
    ) {
        TachyonLazyColumnItem(
            canClick = true,
            onClick = { onItemClick(item.piNo) },
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalArrangement =
                    Arrangement.SpaceBetween,
            ) {
                TachyonText(
                    text =
                        stringResource(
                            id = R.string.text_document_date_format,
                            item.createdDate,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                TachyonText(
                    text =
                        stringResource(
                            id = R.string.text_document_no_purchase_invoice_pi,
                            item.piNo,
                        ),
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
                TachyonText(
                    text =
                        stringResource(
                            id = R.string.text_document_no_purchase_invoice_po,
                            item.poNo,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    @Composable
    override fun BottomAppBarContainer() {
        val showInvoiceInputDialogState by viewModel.showInvoiceInputDialogState.collectAsState()
        if (showInvoiceInputDialogState) {
            ValidateInvoiceDialog()
        }

        TachyonButtonPrimaryFilled(
            modifier = Modifier.fillMaxWidth(),
            buttonText = stringResource(R.string.text_start_order_receive),
            onClick = {
                viewModel.onStartReceiveClick()
            },
        )
    }

    @Composable
    fun ValidateInvoiceDialog() {
        val softKeyboardController = LocalSoftwareKeyboardController.current

        ValidateInvoiceNumberDialog(
            defaultValue = "",
            piNo = "",
            supplierCode = viewModel.supplierCode,
            onDismiss = {
                softKeyboardController?.hide()
                viewModel.setShowInvoiceInputDialogState(false)
            },
            isValidCallback = { billNo ->
                viewModel.setShowInvoiceInputDialogState(false)
                viewModel.updatePurchaseInvoiceDetailBillNo(billNo)
            },
        )
    }
}
