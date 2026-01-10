package th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceItemDetailActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceListPIActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceListPOActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceSummaryActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceSupplierListActivity
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceDetailUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryFilled
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryOutlined
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonEmptyLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonListItemColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChip
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChipStatus
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceItemsViewModel
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun PurchaseInvoiceProductsScreen(viewModel: PurchaseInvoiceItemsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is PurchaseInvoiceItemsViewModel.UIState.NavToSubmit -> {
                    state.data.data?.let {
                        viewModel.setPurchaseInvoiceItem(it)
                        viewModel.resetBaseScreenState()
                        context
                            .startActivity(
                                Intent(
                                    context,
                                    PurchaseInvoiceItemDetailActivity::class.java,
                                ),
                            )
                    }
                }

                is PurchaseInvoiceItemsViewModel.UIState.NavBackToMain -> {
                    context
                        .startActivity(
                            Intent(
                                context,
                                MainMenuActivity::class.java,
                            ),
                        ).also {
                            (context as ComponentActivity).finish()
                        }
                }

                is PurchaseInvoiceItemsViewModel.UIState.NavBack -> {
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

                is PurchaseInvoiceItemsViewModel.UIState.NavToSummary -> {
                    context
                        .startActivity(
                            Intent(
                                context,
                                PurchaseInvoiceSummaryActivity::class.java,
                            ),
                        )
                }

                is PurchaseInvoiceItemsViewModel.UIState.NavToListPI -> {
                    context
                        .startActivity(
                            Intent(
                                context,
                                PurchaseInvoiceListPIActivity::class.java,
                            ),
                        )
                }

                is PurchaseInvoiceItemsViewModel.UIState.NavToListPO -> {
                    context
                        .startActivity(
                            Intent(
                                context,
                                PurchaseInvoiceListPOActivity::class.java,
                            ),
                        )
                }
            }
        }

        else -> {}
    }

    PurchaseInvoiceProductsScreenContainer(viewModel)
}

@Composable
fun PurchaseInvoiceProductsScreenContainer(viewModel: PurchaseInvoiceItemsViewModel) {
    val purchaseInvoiceDetailLiveData by viewModel.purchaseInvoiceDetailLiveData.collectAsState()
    val purchaseInvoiceSupplierLiveData by viewModel.purchaseInvoiceSupplierLiveData.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.logEventScreenView()
        viewModel.logEventGetItemsList(purchaseInvoiceDetailLiveData)
    }

    Scaffold(
        topBar = {
            TopAppBarContainer(
                onNavigationClick = {
                    viewModel.promptConfirmBack()
                },
            )
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            TachyonBarcodeInputField(
                label = stringResource(R.string.text_product_barcode),
                placeholder = stringResource(R.string.text_please_fill_or_scan),
                decodeType = DecodeType.BARCODE_CODEC,
                onValidateBarcode = { decoded ->
                    viewModel.scanBarcode(decoded.barcode)
                },
            )

            BarcodeItemListContainer(
                modifier = Modifier.weight(1f),
                data = purchaseInvoiceDetailLiveData,
                dataSupplier = purchaseInvoiceSupplierLiveData,
                onBarcodeSelected = { data ->
                    viewModel.productSelected(data)
                },
                onRemoveBarcode = { data ->
                    viewModel.promptRemoveItem(data)
                },
            )

            BottomButtonContainer(
                onBackMainMenu = {
                    viewModel.promptConfirmBackMainMenu()
                },
                onClickCheck = {
                    viewModel.logEventCheckPurchaseInvoice()
                    if (purchaseInvoiceDetailLiveData?.entries?.isEmpty() == true) {
                        viewModel.promptCheckState()
                    } else {
                        viewModel.setBaseScreenSuccessState(PurchaseInvoiceItemsViewModel.UIState.NavToSummary)
                    }
                },
            )
        }
    }
}

@Composable
private fun TopAppBarContainer(onNavigationClick: () -> Unit) {
    TachyonTopAppBar(
        titleText = stringResource(R.string.text_title_purchase_invoice),
        onNavigationIconClick = onNavigationClick,
    )
}

@Composable
private fun BarcodeItemListContainer(
    modifier: Modifier,
    data: PurchaseInvoiceDetailUIModel? = null,
    dataSupplier: PurchaseInvoiceSupplierUIModel? = null,
    onBarcodeSelected: (PurchaseInvoiceEntryUIModel) -> Unit,
    onRemoveBarcode: (PurchaseInvoiceEntryUIModel) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    SideEffect {
        if (data?.entries?.isNotEmpty() == true) {
            coroutineScope.launch {
                lazyListState.animateScrollToItem(index = 0)
            }
        }
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._md),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                text =
                    stringResource(
                        R.string.text_product_list,
                        data?.entries?.size ?: 0,
                    ),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text =
                    if (dataSupplier?.supplier?.isRefPo == true) {
                        stringResource(R.string.text_title_amount_receive)
                    } else {
                        stringResource(
                            R.string.text_purchase_invoice_summary_receive,
                        )
                    },
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }
        if (data?.entries?.isNotEmpty() == true) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
                state = lazyListState,
            ) {
                items(
                    items = data.entries,
                    key = { product -> "${product.barcode}_${product.actualQty}" },
                ) { product ->
                    PurchaseInvoiceItemListContainer(
                        product = product,
                        dataSupplier = dataSupplier,
                        onBarcodeSelected = onBarcodeSelected,
                        onRemoveBarcode = onRemoveBarcode,
                    )
                }
            }
        } else {
            TachyonEmptyLazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize(),
                painter = painterResource(R.drawable.icon_empty_document),
                title = stringResource(R.string.text_title_error_not_found_order),
                description = "",
            )
        }
    }
}

@Composable
private fun PurchaseInvoiceItemListContainer(
    product: PurchaseInvoiceEntryUIModel,
    dataSupplier: PurchaseInvoiceSupplierUIModel?,
    onBarcodeSelected: (PurchaseInvoiceEntryUIModel) -> Unit,
    onRemoveBarcode: (PurchaseInvoiceEntryUIModel) -> Unit,
) {
    if (dataSupplier?.supplier?.isRefPo == true) {
        PurchaseInvoiceListItemByRefPo(
            product,
            onBarcodeSelected,
        )
    } else {
        PurchaseInvoiceListItem(
            product,
            onBarcodeSelected,
            onRemoveBarcode,
        )
    }
}

@Composable
private fun PurchaseInvoiceListItemByRefPo(
    product: PurchaseInvoiceEntryUIModel,
    onBarcodeSelected: (PurchaseInvoiceEntryUIModel) -> Unit,
) {
    TachyonListItemColumn(
        canClick = product.barcode.isNotEmpty(),
        onClick = {
            onBarcodeSelected(product)
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            // Column Left
            Column(
                modifier = Modifier.weight(1f),
            ) {
                if (product.actualQty > 0) {
                    if (product.actualQty < product.qty) {
                        TachyonSolidChip(
                            modifier =
                                Modifier
                                    .wrapContentWidth(),
                            status = TachyonSolidChipStatus.ERROR,
                            chipText =
                                stringResource(
                                    R.string.text_chip_under,
                                    product.qty - product.actualQty,
                                ),
                        )
                    } else {
                        TachyonSolidChip(
                            modifier =
                                Modifier
                                    .wrapContentWidth(),
                            status = TachyonSolidChipStatus.SUCCESS,
                            chipText = stringResource(R.string.text_chip_complete),
                        )
                    }
                }
                TachyonText(
                    text = product.barcode,
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )
            }

            // Column Right
            Row(
                modifier =
                    Modifier
                        .wrapContentHeight()
                        .wrapContentWidth()
                        .align(Alignment.Top),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._2xs),
            ) {
                TachyonText(
                    text = "${product.actualQty}/${product.qty}",
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )
                TachyonText(
                    text = product.unitName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }
        // Product Name
        TachyonText(
            text = product.productName,
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
            maxLines = 2,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

@Composable
private fun PurchaseInvoiceListItem(
    product: PurchaseInvoiceEntryUIModel,
    onBarcodeSelected: (PurchaseInvoiceEntryUIModel) -> Unit,
    onRemoveBarcode: (PurchaseInvoiceEntryUIModel) -> Unit,
) {
    TachyonListItemColumn(
        canClick = product.barcode.isNotEmpty(),
        onClick = {
            onBarcodeSelected(product)
        },
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonText(
                text = product.barcode,
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._2xs),
            ) {
                TachyonText(
                    text = product.actualQty.toString(),
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )
                TachyonText(
                    text = product.unitName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Top,
        ) {
            TachyonText(
                text = product.productName,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            IconButton(
                onClick =
                    safeClickWithDebounce { onRemoveBarcode(product) },
                modifier =
                    Modifier
                        .background(
                            shape = RoundedCornerShape((8 / LocalDensity.current.density).dp),
                            color = TachyonDictionary.content.danger._default,
                        ).size((32 / LocalDensity.current.density).dp),
            ) {
                Icon(
                    painter = painterResource(R.drawable.icons_delete),
                    contentDescription = "ลบ barcode",
                    tint = TachyonDictionary.content._inversed,
                    modifier =
                        Modifier
                            .width((16 / LocalDensity.current.density).dp)
                            .height((18 / LocalDensity.current.density).dp),
                )
            }
        }
    }
}

@Composable
private fun BottomButtonContainer(
    onClickCheck: () -> Unit,
    onBackMainMenu: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonButtonPrimaryOutlined(
            modifier = Modifier.weight(1f),
            buttonText = stringResource(R.string.text_back_to_main_menu),
            onClick = { onBackMainMenu() },
        )
        TachyonButtonPrimaryFilled(
            modifier = Modifier.weight(1f),
            buttonText = stringResource(R.string.text_check),
            onClick = { onClickCheck() },
        )
    }
}
