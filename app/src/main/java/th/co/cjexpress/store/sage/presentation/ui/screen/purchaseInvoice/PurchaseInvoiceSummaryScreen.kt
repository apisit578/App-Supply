package th.co.cjexpress.store.sage.presentation.ui.screen.purchaseInvoice

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceItemsActivity
import th.co.cjexpress.store.sage.presentation.activity.purchaseInvoice.PurchaseInvoiceListPIActivity
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceCalculateUIModel
import th.co.cjexpress.store.sage.presentation.model.purchaseInvoice.PurchaseInvoiceSupplierDetailUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.ValidateInvoiceNumberDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryFilled
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonButtonPrimaryOutlined
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCard
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCardStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonIconWarning
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TextSegment
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.purchaseInvoice.PurchaseInvoiceSummaryViewModel
import th.co.cjexpress.store.sage.utils.CurrencyUtils
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun PurchaseInvoiceSummaryScreen(viewModel: PurchaseInvoiceSummaryViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val confirmBackMainMenuState by viewModel.confirmBackMainMenuDialogState.collectAsState()
    val confirmBackState by viewModel.confirmBackDialogState.collectAsState()
    val confirmSaveDialogState by viewModel.showConfirmSaveDialogState.collectAsState()
    val showInvoiceInputDialogState by viewModel.showInvoiceInputDialogState.collectAsState()
    val piDetailLD by viewModel.piDetailState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.logEventScreenView()
        viewModel.calculatePurchaseInvoice()
    }

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(dialogData = state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is PurchaseInvoiceSummaryViewModel.UIState.SaveDraftSuccess -> {
                    TachyonAlertDialog(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.SUCCESS,
                            titleText = stringResource(id = R.string.text_save_success),
                            messageText =
                                stringResource(
                                    id = R.string.text_document_saved_number,
                                    state.data.piNo,
                                ),
                            onPositiveClick = {
                                context
                                    .startActivity(
                                        Intent(
                                            context,
                                            PurchaseInvoiceListPIActivity::class.java,
                                        ),
                                    ).also {
                                        (context as ComponentActivity).finish()
                                    }
                            },
                        ),
                    )
                }

                is PurchaseInvoiceSummaryViewModel.UIState.NavBack -> {
                    context
                        .startActivity(
                            Intent(
                                context,
                                PurchaseInvoiceItemsActivity::class.java,
                            ),
                        ).also {
                            (context as ComponentActivity).finish()
                        }
                }
            }
        }

        else -> {}
    }

    if (showInvoiceInputDialogState) {
        ValidateInvoiceDialog(
            viewModel = viewModel,
            supplierCode = piDetailLD.supplierCode,
            piNo = piDetailLD.piNo,
            billNo = piDetailLD.billNo,
        )
    }

    if (confirmSaveDialogState) {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.QUESTION,
                titleText = stringResource(R.string.text_confirm_to_save_document),
                messageText =
                    if (viewModel.isRefPo) {
                        stringResource(R.string.text_summary_confirm_dialog_message)
                    } else {
                        stringResource(R.string.text_summary_confirm_dialog_message_without_po)
                    },
                negativeTextRes = R.string.text_no,
                onNegativeClick = {
                    viewModel.logEventSavePurchaseInvoice(false)
                    viewModel.setConfirmSaveDialogState(false)
                },
                positiveTextRes = R.string.text_yes,
                onPositiveClick = {
                    viewModel.logEventSavePurchaseInvoice(true)
                    viewModel.setConfirmSaveDialogState(false)
                    viewModel.validatePurchaseInvoiceBill(
                        supplierCode = piDetailLD.supplierCode,
                        billNo = piDetailLD.billNo,
                        piNo = piDetailLD.piNo,
                    )
                },
            ),
        )
    }

    if (confirmBackMainMenuState) {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.QUESTION,
                titleText = stringResource(R.string.text_title_confirm_back_to_main_menu),
                messageText =
                    if (viewModel.isRefPo) {
                        stringResource(R.string.text_confirm_back_to_main_menu)
                    } else {
                        stringResource(R.string.text_unsaved_changes_warning_confirm_to_main)
                    },
                positiveTextRes = R.string.text_yes,
                onPositiveClick = {
                    viewModel.logEventOnBackMainMenu()
                    viewModel.setConfirmBackMainMenuDialogState(false)
                    context
                        .startActivity(
                            Intent(
                                context,
                                MainMenuActivity::class.java,
                            ),
                        ).also {
                            (context as ComponentActivity).finish()
                        }
                },
                negativeTextRes = R.string.text_no,
                onNegativeClick = { viewModel.setConfirmBackMainMenuDialogState(false) },
            ),
        )
    }

    if (confirmBackState) {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.QUESTION,
                titleText = stringResource(R.string.text_confirm_exit),
                messageText = stringResource(R.string.text_confirm_leave_page),
                positiveTextRes = R.string.text_yes,
                onPositiveClick = {
                    viewModel.logEventOnBackPressed()
                    viewModel.setConfirmBackDialogState(false)
                    context
                        .startActivity(
                            Intent(
                                context,
                                PurchaseInvoiceItemsActivity::class.java,
                            ),
                        ).also {
                            (context as ComponentActivity).finish()
                        }
                },
                negativeTextRes = R.string.text_no,
                onNegativeClick = { viewModel.setConfirmBackDialogState(false) },
            ),
        )
    }

    PurchaseInvoiceSummaryScreenContainer(viewModel)
}

@Composable
private fun PurchaseInvoiceSummaryScreenContainer(viewModel: PurchaseInvoiceSummaryViewModel) {
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
            DetailPurchaseInvoiceSummaryContainer(modifier = Modifier.weight(1f), viewModel)

            BottomButtonContainer(
                onBackToMainMenuClick = {
                    viewModel.setConfirmBackMainMenuDialogState(true)
                },
                onSaveClick = {
                    viewModel.setConfirmSaveDialogState(true)
                },
            )
        }
    }
}

@Composable
private fun TopAppBarContainer(onNavigationClick: () -> Unit) {
    TachyonTopAppBar(
        titleText = stringResource(R.string.text_title_purchase_invoice_summary),
        onNavigationIconClick = onNavigationClick,
    )
}

@Composable
private fun DetailPurchaseInvoiceSummaryContainer(
    modifier: Modifier,
    viewModel: PurchaseInvoiceSummaryViewModel,
) {
    val supplierData = viewModel.supplierLD
    val isRefPo = viewModel.isRefPo
    val billNo by viewModel.billNoState.collectAsState()
    val purchaseInvoiceCalculatedState by viewModel.purchaseInvoiceCalculatedState.collectAsState()

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier)
                .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._md),
    ) {
        purchaseInvoiceCalculatedState?.let {
            if (isRefPo) {
                if (it.totalReceivedQty < it.totalOrderedQty) {
                    TachyonCard(
                        icon = { TachyonIconWarning() },
                        text = stringResource(R.string.text_purchase_invoice_summary_error),
                        style = TachyonCardStyle.WARNING,
                    )
                } else {
                    TachyonCard(
                        icon = {
                            TachyonIconWarning(
                                tint = TachyonDictionary.content.success._default,
                            )
                        },
                        text = stringResource(R.string.text_purchase_invoice_summary_accept),
                        style = TachyonCardStyle.SUCCESS,
                    )
                }
            }
        }

        TachyonText(
            text =
                stringResource(
                    R.string.text_title_supplier,
                ),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )

        DetailSupplierPIContainer(
            supplierData = supplierData,
            billNo = billNo,
            onEditBillNoClick = {
                viewModel.logEventEditInvoiceNumber(billNo)
                viewModel.setShowInvoiceInputDialogState(true)
            },
        )

        purchaseInvoiceCalculatedState?.let {
            if (isRefPo) {
                DetailPOContainer(it)
            } else {
                DetailNotRefPOContainer(it)
            }
        }
    }
}

@Composable
private fun DetailSupplierPIContainer(
    supplierData: PurchaseInvoiceSupplierDetailUIModel,
    billNo: String,
    onEditBillNoClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .background(TachyonDictionary.surface.static._default2)
                .fillMaxWidth()
                .padding(
                    start = TachyonDictionary.global.spacing._md,
                    top = TachyonDictionary.global.spacing._md,
                    end = TachyonDictionary.global.spacing._md,
                    bottom = TachyonDictionary.global.spacing._md,
                ),
    ) {
        TachyonText(
            text = supplierData.supplierCode,
            style = TachyonDictionary.body._1,
            color = TachyonDictionary.content._default,
        )

        TachyonText(
            text = supplierData.supplierName,
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                modifier = Modifier.weight(1f),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
                segmentsText =
                    listOf(
                        TextSegment.Normal(
                            stringResource(R.string.text_purchase_invoice_summary_bill_no),
                        ),
                        TextSegment.Underlined(billNo),
                    ),
            )
            Box(
                modifier =
                    Modifier
                        .align(Alignment.Bottom)
                        .size((32 / LocalDensity.current.density).dp),
            ) {
                Box(
                    modifier =
                        Modifier
                            .matchParentSize()
                            .background(
                                color = TachyonDictionary.surface.static._default2,
                                shape = RoundedCornerShape(8.dp),
                            ).border(
                                width = 1.dp,
                                color = TachyonDictionary.content._default,
                                shape = RoundedCornerShape(8.dp),
                            ),
                )

                IconButton(
                    onClick =
                        safeClickWithDebounce {
                            onEditBillNoClick()
                        },
                    modifier =
                        Modifier
                            .matchParentSize(),
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icons_edit),
                        contentDescription = stringResource(R.string.text_edit),
                        tint = TachyonDictionary.content._default,
                        modifier =
                            Modifier
                                .size((18 / LocalDensity.current.density).dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun DetailPOContainer(purchaseInvoiceCalculatedData: PurchaseInvoiceCalculateUIModel) {
    Column(
        modifier =
            Modifier
                .background(TachyonDictionary.surface.static._default2)
                .fillMaxWidth()
                .padding(
                    start = TachyonDictionary.global.spacing._md,
                    top = TachyonDictionary.global.spacing._md,
                    end = TachyonDictionary.global.spacing._md,
                    bottom = TachyonDictionary.global.spacing._md,
                ),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                text = stringResource(R.string.text_document_no_purchase_invoice_summary_po),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )
            Spacer(
                modifier = Modifier.width(TachyonDictionary.space.padding._md),
            )
            TachyonText(
                text = purchaseInvoiceCalculatedData.poNo,
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                text = stringResource(R.string.text_purchase_invoice_summary_amount),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )
            TachyonText(
                segmentsText =
                    listOf(
                        TextSegment.Highlight(
                            purchaseInvoiceCalculatedData.receivedItemCount.toString(),
                            if (purchaseInvoiceCalculatedData.receivedItemCount <
                                purchaseInvoiceCalculatedData.itemCount
                            ) {
                                TachyonDictionary.content.danger._default
                            } else {
                                TachyonDictionary.content._default
                            },
                        ),
                        TextSegment.Normal(
                            " / ${purchaseInvoiceCalculatedData.itemCount}",
                        ),
                    ),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                text = stringResource(R.string.text_purchase_invoice_summary_receive),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )
            TachyonText(
                segmentsText =
                    listOf(
                        TextSegment.Highlight(
                            purchaseInvoiceCalculatedData.totalReceivedQty.toString(),
                            if (purchaseInvoiceCalculatedData.totalReceivedQty <
                                purchaseInvoiceCalculatedData.totalOrderedQty
                            ) {
                                TachyonDictionary.content.danger._default
                            } else {
                                TachyonDictionary.content._default
                            },
                        ),
                        TextSegment.Normal(
                            " / ${purchaseInvoiceCalculatedData.totalOrderedQty}",
                        ),
                    ),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                text = stringResource(R.string.text_purchase_invoice_summary_ordered_value),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )
            TachyonText(
                text = CurrencyUtils.formatCurrency(purchaseInvoiceCalculatedData.orderedTotalAmount),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
        }
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                text = stringResource(R.string.text_purchase_invoice_summary_received_value),
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )
            TachyonText(
                text = CurrencyUtils.formatCurrency(purchaseInvoiceCalculatedData.receivedTotalAmount),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
        }
    }
}

@Composable
private fun DetailNotRefPOContainer(purchaseInvoiceCalculatedData: PurchaseInvoiceCalculateUIModel) {
    Column(
        modifier =
            Modifier
                .background(TachyonDictionary.surface.static._default2)
                .fillMaxWidth()
                .padding(TachyonDictionary.global.spacing._md),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
    ) {
        val summaryList =
            with(purchaseInvoiceCalculatedData) {
                listOf(
                    stringResource(R.string.text_document_no_purchase_invoice_summary_po) to
                        stringResource(
                            R.string.text_none,
                        ),
                    stringResource(R.string.text_purchase_invoice_summary_amount) to receivedItemCount.toString(),
                    stringResource(R.string.text_purchase_invoice_summary_receive) to totalReceivedQty.toString(),
                    stringResource(R.string.text_head_line_po_summary) to
                        CurrencyUtils.formatCurrency(
                            receivedTotalAmount,
                        ),
                )
            }
        summaryList.forEachIndexed { _, (label, value) ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                TachyonText(
                    text = label,
                    style = TachyonDictionary.caption._1,
                    color = TachyonDictionary.content._default,
                )
                TachyonText(
                    text = value,
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )
            }
        }
    }
}

@Composable
private fun BottomButtonContainer(
    onBackToMainMenuClick: () -> Unit,
    onSaveClick: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonButtonPrimaryOutlined(
            modifier = Modifier.weight(1f),
            buttonText = stringResource(R.string.text_back_to_main_menu),
            onClick = { onBackToMainMenuClick() },
        )
        TachyonButtonPrimaryFilled(
            modifier = Modifier.weight(1f),
            buttonText = stringResource(R.string.text_save),
            onClick = { onSaveClick() },
        )
    }
}

@Composable
private fun ValidateInvoiceDialog(
    viewModel: PurchaseInvoiceSummaryViewModel,
    supplierCode: String,
    billNo: String,
    piNo: String,
) {
    val context = LocalContext.current

    ValidateInvoiceNumberDialog(
        defaultValue = billNo,
        piNo = piNo,
        supplierCode = supplierCode,
        onDismiss = {
            viewModel.hideKeyboard(context)
            viewModel.setShowInvoiceInputDialogState(false)
        },
    ) { billNo ->
        viewModel.setShowInvoiceInputDialogState(false)
        viewModel.updatePurchaseInvoiceDetailBillNo(billNo)
    }
}
