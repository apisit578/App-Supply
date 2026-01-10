package th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceEntryUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.purchaseInvoice.PurchaseInvoiceUpdateItemState
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonNumberCounter
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryFilled
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryOutlined
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCard
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCardStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonIconWarning
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonListItemColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChip
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChipStatus
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceItemDetailViewModel
import timber.log.Timber

@Composable
fun PurchaseInvoiceItemDetailScreen(viewModel: PurchaseInvoiceItemDetailViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val confirmBackState by viewModel.confirmBackDialogState.collectAsState()
    val showNotMatchDialogState by viewModel.showNotMatchDialogState.collectAsState()
    val showQtyEqualZeroDialogState by viewModel.showQtyEqualZeroDialogState.collectAsState()
    val purchaseInvoiceItemState by viewModel.purchaseInvoiceItemState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.logEventScreenView()
    }

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is PurchaseInvoiceUpdateItemState -> {
                    viewModel.resetPurchaseInvoiceItem()
                    viewModel.resetBaseScreenState()
                    (context as ComponentActivity).finish()
                }
            }
        }

        else -> {}
    }

    if (confirmBackState) {
        purchaseInvoiceItemState?.let {
            val isRefPo = it.qty != 0
            TachyonAlertDialog(
                TachyonAlertDialogUIModel(
                    type = ErrorType.QUESTION,
                    titleText = stringResource(R.string.text_confirm_exit),
                    messageText =
                        if (isRefPo) {
                            stringResource(R.string.text_confirm_leave_page)
                        } else {
                            stringResource(R.string.text_unsaved_changes_warning_confirm_leave_page)
                        },
                    positiveTextRes = R.string.text_yes,
                    onPositiveClick = {
                        viewModel.logEventOnBackPressed()
                        viewModel.setConfirmBackDialogState(false)
                        viewModel.resetPurchaseInvoiceItem()
                        (context as ComponentActivity).finish()
                    },
                    negativeTextRes = R.string.text_no,
                    onNegativeClick = { viewModel.setConfirmBackDialogState(false) },
                ),
            )
        }
    }

    if (showNotMatchDialogState) {
        viewModel.logEventAlertDialog(stringResource(R.string.text_title_barcode_mismatch))
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.WARNING,
                titleText = stringResource(R.string.text_title_barcode_mismatch),
                messageText = stringResource(R.string.text_please_check_and_scan_again),
                onPositiveClick = { viewModel.setShowNotMatchDialogState(false) },
            ),
        )
    }

    if (showQtyEqualZeroDialogState) {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.WARNING,
                titleText = stringResource(R.string.text_unable_to_continue),
                messageText = stringResource(R.string.text_qty_received_not_equal_zero),
                onPositiveClick = { viewModel.setShowQtyEqualZeroDialogState(false) },
            ),
        )
    }

    PurchaseInvoiceProductDetailContainer(viewModel, purchaseInvoiceItemState)
}

@Composable
fun PurchaseInvoiceProductDetailContainer(
    viewModel: PurchaseInvoiceItemDetailViewModel,
    purchaseInvoiceItemState: PurchaseInvoiceEntryUIModel?,
) {
    val coroutineScope = rememberCoroutineScope()
    val canEditState by viewModel.canEditState.collectAsState()
    val actualQtyState by viewModel.inputActualQtyState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBarContainer(
                onNavigationClick = {
                    viewModel.setConfirmBackDialogState(true)
                },
            )
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            if (!canEditState) {
                TachyonCard(
                    icon = { TachyonIconWarning() },
                    text = stringResource(R.string.text_purchase_invoice_warning_please_scan_barcode),
                    style = TachyonCardStyle.WARNING,
                )
                TachyonBarcodeInputField(
                    label = stringResource(R.string.text_product_barcode),
                    placeholder = stringResource(R.string.text_please_fill_or_scan),
                    decodeType = DecodeType.BARCODE_CODEC,
                    onValidateBarcode = { decoded ->
                        val barcode = decoded.barcode

                        if (barcode.isBlank()) {
                            viewModel.showDialogBarcodeInvalid()
                        } else {
                            if (viewModel.validateBarcode(barcode)) {
                                viewModel.setCanEditState(true)
                            } else {
                                viewModel.setShowNotMatchDialogState(true)
                            }
                        }
                    },
                )
            }

            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
            ) {
                purchaseInvoiceItemState?.let {
                    ProductDetailContainer(
                        purchaseInvoiceEntryUIModel = it,
                        qty = it.qty,
                        actualQty = it.actualQty,
                    )
                }

                ActualQtyCounterContainer(
                    modifier = Modifier,
                    canEdit = canEditState,
                    actualQty = actualQtyState,
                    onActualQtyChange = {
                        viewModel.setInputActualQtyState(it)
                        viewModel.updatePurchaseInvoiceItem(it)
                    },
                )
            }

            BottomButtonContainer(
                canEdit = canEditState,
                onCancelClick = {
                    viewModel.setConfirmBackDialogState(true)
                },
                onSubmitClick = {
                    purchaseInvoiceItemState?.let {
                        coroutineScope.launch {
                            viewModel.handleCheckIsRefPO(it)
                        }
                    }
                },
            )
        }
    }
}

@Composable
private fun TopAppBarContainer(onNavigationClick: () -> Unit) {
    TachyonTopAppBar(
        titleText = stringResource(id = R.string.text_title_set_receive_qty),
        onNavigationIconClick = onNavigationClick,
    )
}

@Composable
private fun ProductDetailContainer(
    purchaseInvoiceEntryUIModel: PurchaseInvoiceEntryUIModel,
    actualQty: Int = 0,
    qty: Int = 0,
) {
    val purchaseInvoiceEntryUIModel = remember { purchaseInvoiceEntryUIModel }
    val currentActualQty = remember { actualQty }
    val isRefPo = qty != 0
    val isShowCompleteChip = qty > 0 && qty == currentActualQty
    val isShowWarningChip = currentActualQty > 0
    Column(
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
    ) {
        Row {
            TachyonText(
                text = stringResource(id = R.string.text_product_info),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }
        TachyonListItemColumn {
            if (isRefPo) {
                Row {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                space = TachyonDictionary.space.padding._xs,
                            ),
                    ) {
                        if (isShowCompleteChip) {
                            TachyonSolidChip(
                                modifier =
                                    Modifier
                                        .wrapContentWidth(),
                                status = TachyonSolidChipStatus.SUCCESS,
                                chipText = stringResource(R.string.text_chip_complete),
                            )
                        } else if (isShowWarningChip) {
                            TachyonSolidChip(
                                modifier =
                                    Modifier
                                        .wrapContentWidth(),
                                status = TachyonSolidChipStatus.ERROR,
                                chipText =
                                    stringResource(
                                        R.string.text_chip_under,
                                        (qty - currentActualQty),
                                    ),
                            )
                        }

                        TachyonText(
                            text = purchaseInvoiceEntryUIModel.barcode,
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )
                    }

                    Row(
                        modifier =
                            Modifier
                                .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                space = TachyonDictionary.space.padding._xs,
                            ),
                    ) {
                        TachyonText(
                            text = "$currentActualQty/$qty",
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )
                        TachyonText(
                            text = purchaseInvoiceEntryUIModel.unitName,
                            style = TachyonDictionary.body._2,
                            color = TachyonDictionary.content._default,
                        )
                    }
                }

                TachyonText(
                    text = purchaseInvoiceEntryUIModel.productName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text =
                        stringResource(
                            id = R.string.text_format_unit_per_factor,
                            purchaseInvoiceEntryUIModel.unitName,
                            purchaseInvoiceEntryUIModel.unitFactor,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            } else {
                Row {
                    Column(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .weight(1f),
                        verticalArrangement =
                            Arrangement.spacedBy(
                                space = TachyonDictionary.space.padding._xs,
                            ),
                    ) {
                        TachyonText(
                            text = purchaseInvoiceEntryUIModel.barcode,
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )
                    }

                    Row(
                        modifier =
                            Modifier
                                .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                space = TachyonDictionary.space.padding._2xs,
                            ),
                    ) {
                        TachyonText(
                            text = currentActualQty.toString(),
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )
                        TachyonText(
                            text = purchaseInvoiceEntryUIModel.unitName,
                            style = TachyonDictionary.body._2,
                            color = TachyonDictionary.content._default,
                        )
                    }
                }

                TachyonText(
                    text = purchaseInvoiceEntryUIModel.productName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text =
                        stringResource(
                            id = R.string.text_format_unit_per_factor,
                            purchaseInvoiceEntryUIModel.unitName,
                            purchaseInvoiceEntryUIModel.unitFactor,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }
    }
}

@Composable
private fun ActualQtyCounterContainer(
    modifier: Modifier,
    canEdit: Boolean,
    actualQty: Int,
    onActualQtyChange: (Int) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._md),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TachyonDictionary.surface.static._default2)
                    .padding(TachyonDictionary.global.spacing._md),
        ) {
            TachyonText(
                text = stringResource(id = R.string.text_number_receive),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
            TachyonNumberCounter(
                modifier = Modifier.fillMaxWidth(),
                enabled = canEdit,
                initialNumber = actualQty,
                onIncrement = {
                    Timber.tag("STATE").d("TachyonNumberCounter on ValueChange = $it")
                    onActualQtyChange(it)
                },
                onDecrement = {
                    Timber.tag("STATE").d("TachyonNumberCounter on ValueChange = $it")
                    onActualQtyChange(it)
                },
            )
        }
    }
}

@Composable
private fun BottomButtonContainer(
    canEdit: Boolean,
    onCancelClick: () -> Unit,
    onSubmitClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonButtonPrimaryOutlined(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            buttonText = stringResource(id = R.string.text_cancel),
            onClick = onCancelClick,
        )

        TachyonButtonPrimaryFilled(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            enabled = canEdit,
            buttonText = stringResource(id = R.string.text_save),
            onClick = onSubmitClick,
        )
    }
}
