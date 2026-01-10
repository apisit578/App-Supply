package th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceItemsActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceListPIActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceSupplierListActivity
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierPOUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.ValidateInvoiceNumberDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.ItemListScreenProvider
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnEmptyState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnItem
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnPullToRefreshState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TopAppBarContainerParams
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceListPOViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceListPOViewModel.UIState

class PurchaseInvoiceListPOItemListScreenUI(
    private val viewModel: PurchaseInvoiceListPOViewModel,
) : ItemListScreenProvider<PurchaseInvoiceSupplierPOUIModel, PurchaseInvoiceListPOViewModel>() {
    @Composable
    override fun InitUI() {
        val items by viewModel.poList.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.logEventScreenView()
            viewModel.loadPurchaseOrderDocument()
        }

        super.ScreenContainer(
            viewModel = viewModel,
            successStateCallback = { data -> SuccessStateCallback(data) },
            topBarParams =
                TopAppBarContainerParams(
                    titleTextRes = R.string.text_title_purchase_invoice,
                    onNavigationIconClick = {
                        viewModel.logEventOnBackPressed()
                        viewModel.setShowDialogConfirmBackState(true)
                    },
                ),
            bottomBar = {
            },
            content = {
                TachyonBarcodeInputField(
                    label = stringResource(R.string.text_search_po_document),
                    placeholder = stringResource(R.string.text_pleas_fill_up_po_document),
                    decodeType = DecodeType.NONE,
                ) { decoded ->
                    viewModel.onSearchPo(decoded.barcode)
                }
                SupplierContainer(viewModel.supplierLD)

                ItemListContainer(
                    items = items,
                ) { poNo ->
                    (poNo as? String)
                        ?.takeIf { it.isNotEmpty() }
                        ?.let(viewModel::onSearchPo)
                }

                ValidateInvoiceDialog()

                ConfirmBackDialog()
            },
        )
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current

        when (data) {
            is UIState.NavToPIList ->
                context
                    .startActivity(
                        Intent(context, PurchaseInvoiceListPIActivity::class.java),
                    ).also {
                        (context as ComponentActivity).finish()
                    }

            is UIState.NavToSupplierList ->
                context
                    .startActivity(
                        Intent(context, PurchaseInvoiceSupplierListActivity::class.java),
                    ).also {
                        (context as ComponentActivity).finish()
                    }

            is UIState.NavToPIDetail ->
                context
                    .startActivity(
                        Intent(context, PurchaseInvoiceItemsActivity::class.java),
                    ).also {
                        (context as ComponentActivity).finish()
                    }

            else -> {}
        }
    }

    @Composable
    override fun ItemDetailContainer(
        item: PurchaseInvoiceSupplierPOUIModel,
        onItemClick: (Any) -> Unit,
    ) {
        TachyonLazyColumnItem(
            onClick = {
                onItemClick(item.docNo)
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TachyonText(
                        text = stringResource(R.string.text_po_number, item.docNo),
                        style = TachyonDictionary.body._1,
                        color = TachyonDictionary.content._default,
                        modifier = Modifier.weight(2f),
                        maxLines = 2,
                    )

                    TachyonText(
                        text = item.grandTotalAmountText,
                        style = TachyonDictionary.body._1,
                        color = TachyonDictionary.content._default,
                        modifier = Modifier.weight(1f),
                        textAlign = TextAlign.End,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                Column {
                    TachyonText(
                        text = stringResource(R.string.text_po_due_date, item.shipmentDate),
                        style = TachyonDictionary.body._2,
                        color = TachyonDictionary.content._default,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }

    @Composable
    override fun ItemListContainer(
        items: List<PurchaseInvoiceSupplierPOUIModel>,
        onItemClick: (Any) -> Unit,
    ) {
        val pullingState by viewModel.baseScreenState.collectAsState()

        TachyonLazyColumn(
            modifier = Modifier.fillMaxSize(),
            pullToRefreshState =
                TachyonLazyColumnPullToRefreshState(
                    pullToRefreshLoadingState = pullingState is BaseScreenState.Loading,
                    pullToRefreshCallback = { viewModel.loadPurchaseOrderDocument() },
                ),
            rowHeaderText = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    TachyonText(
                        text =
                            stringResource(
                                R.string.text_head_line_list_po,
                                items.size,
                            ),
                        style = TachyonDictionary.body._2,
                        color = TachyonDictionary.content._default,
                    )
                    TachyonText(
                        text =
                            stringResource(R.string.text_head_line_po_summary),
                        style = TachyonDictionary.body._2,
                        color = TachyonDictionary.content._default,
                    )
                }
            },
            items = items,
            itemKey = { it.docNo },
            emptyState =
                TachyonLazyColumnEmptyState(
                    painter = painterResource(id = R.drawable.icon_empty_document),
                    descriptionText = stringResource(id = R.string.text_docs_list_empty),
                ),
        ) { item ->
            ItemDetailContainer(item, onItemClick = { poNo -> onItemClick(poNo) })
        }
    }

    @Composable
    override fun BottomAppBarContainer() {
    }

    @Composable
    private fun SupplierContainer(supplierData: PurchaseInvoiceSupplierDetailUIModel) {
        TachyonText(
            text =
                stringResource(
                    id = R.string.text_head_line_supplier,
                ),
            textAlign = TextAlign.Start,
            style =
                TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )
        TachyonLazyColumnItem(
            canClick = false,
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalArrangement =
                    Arrangement.SpaceBetween,
            ) {
                TachyonText(
                    text = supplierData.supplierCode,
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                TachyonText(
                    text = supplierData.supplierName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }

    @Composable
    private fun ValidateInvoiceDialog() {
        val context = LocalContext.current
        val showInvoiceInputDialogState by viewModel.showInvoiceInputDialogState.collectAsState()
        val selectedPo by viewModel.selectedPoNo.collectAsState()
        val supplierCode = viewModel.supplierLD.supplierCode
        val keepBillNo = viewModel.keepBillNo.collectAsState()

        if (showInvoiceInputDialogState) {
            ValidateInvoiceNumberDialog(
                defaultValue = keepBillNo.value,
                piNo = "",
                supplierCode = supplierCode,
                onDismiss = {
                    viewModel.hideKeyboard(context)
                    viewModel.clearSelectedPo()
                    viewModel.setBillNo("")
                    viewModel.setShowInvoiceInputDialogState(false)
                },
            ) { billNo ->
                viewModel.setShowInvoiceInputDialogState(false)
                viewModel.createPurchaseInvoice(
                    poNo = selectedPo,
                    billNo = billNo,
                    supplierCode = supplierCode,
                )
            }
        }
    }

    @Composable
    fun ConfirmBackDialog() {
        val showConfirmBackDialogState by viewModel.showConfirmBackDialogState.collectAsState()

        if (showConfirmBackDialogState) {
            TachyonAlertDialog(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = stringResource(R.string.text_confirm_exit),
                    messageText = stringResource(R.string.text_confirm_leave_page),
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        viewModel.setShowDialogConfirmBackState(false)
                        viewModel.navigateToPIList()
                    },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { viewModel.setShowDialogConfirmBackState(false) },
                ),
            )
        }
    }
}
