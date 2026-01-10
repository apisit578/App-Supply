package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceiveTote

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.constants.OrderReceiveToteDiscountType
import th.co.cjexpress.store.sage.constants.OrderReceiveToteLotType
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteInputExpireActivity
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.ISdToteExpiryDateModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteSubmitUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteLowestFactorState
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonNumberCounter
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBottomAppBar
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCard
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCardStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonEmptyLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonIconWarning
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonListItemColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonListItemRow
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChip
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChipStatus
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteItemDetailViewModel
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import timber.log.Timber
import java.util.Date

@Composable
fun OrderReceiveToteItemDetailScreen(viewModel: OrderReceiveToteItemDetailViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val removingExpiryDateState by viewModel.removingExpiryDateState.collectAsState()

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is SdToteLowestFactorState -> {
                    viewModel.setCanEditState(state.data.isSuccess)
                    viewModel.resetBaseScreenState()
                }

                is SdToteBarcodeEditingSubmitState -> {
                    viewModel.resetSubmitBarcodeLD()
                    (context as ComponentActivity).finish()
                }
            }
        }

        else -> {}
    }

    if (removingExpiryDateState.first != null && removingExpiryDateState.second != null) {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.ERROR,
                titleText =
                    if (removingExpiryDateState.second == true) {
                        stringResource(R.string.text_confirm_to_delete_barcode)
                    } else {
                        stringResource(R.string.text_confirm_to_delete_qr)
                    },
                messageText =
                    String.format(
                        stringResource(R.string.text_confirm_to_remove_expiry_lot),
                        removingExpiryDateState.first!!.toStringDDMMYYYYFormat(),
                    ),
                negativeTextRes = R.string.text_no,
                onNegativeClick = {
                    viewModel.setRemovingExpiryDateState(null, null)
                },
                positiveTextRes = R.string.text_yes,
                onPositiveClick = {
                    viewModel.removeSubmitExpiryDateLD(
                        removingExpiryDateState.first!!,
                        removingExpiryDateState.second!!,
                    )
                    viewModel.setRemovingExpiryDateState(null, null)
                },
            ),
        )
    }

    OrderReceiveToteItemDetailContainer(viewModel)
}

@Composable
private fun OrderReceiveToteItemDetailContainer(viewModel: OrderReceiveToteItemDetailViewModel) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val submitBarcodeLDState by viewModel.submitBarcodeFlowLD.collectAsState()
    val canEditState by viewModel.canEditState.collectAsState()
    val actualQtyState by viewModel.inputActualQtyState.collectAsState()
    var showConfirmDialog by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.logEventScreenView(
            message =
                String.format(
                    context.getString(R.string.log_message_item_detail),
                    submitBarcodeLDState.productName,
                    submitBarcodeLDState.barcode,
                    submitBarcodeLDState.discountType,
                    submitBarcodeLDState.expList.toString(),
                ),
        )
    }

    Scaffold(
        topBar = {
            TopAppBarContainer(
                onNavigationClick = {
                    viewModel.logEventOnBackPressed()
                    if (canEditState) {
                        showConfirmDialog = true
                    } else {
                        viewModel.resetSubmitBarcodeLD()
                        (context as ComponentActivity).finish()
                    }
                },
            )
        },
        bottomBar = {
            TachyonBottomAppBar {
                BottomButtonContainer(
                    canEdit = canEditState,
                    onCancelClick = {
                        viewModel.logEventOnBackPressed()
                        if (canEditState) {
                            showConfirmDialog = true
                        } else {
                            viewModel.resetSubmitBarcodeLD()
                            (context as ComponentActivity).finish()
                        }
                    },
                    onSubmitClick = {
                        if (submitBarcodeLDState.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name) {
                            viewModel.sdToteSubmitAdjust(submitBarcodeLDState)
                        } else {
                            viewModel.submitBarcode(submitBarcodeLDState)
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            if (!canEditState) {
                TachyonCard(
                    style = TachyonCardStyle.WARNING,
                    icon = { TachyonIconWarning() },
                    text = stringResource(R.string.text_warning_please_scan_barcode),
                )
                TachyonBarcodeInputField(
                    label = stringResource(R.string.title_barcode_qr_code),
                    placeholder = stringResource(R.string.text_please_fill_or_scan),
                    decodeType = DecodeType.BARCODE_CODEC,
                    onValidateBarcode = { decoded ->
                        viewModel.logEventSubmitBarcode(decoded.barcode)
                        val barcodeState =
                            when (decoded.data) {
                                is QRFreshDecodeDataModel -> {
                                    decoded.data.barcode
                                }

                                else -> {
                                    decoded.barcode
                                }
                            }

                        if (viewModel.validateBarcode(barcode = barcodeState)) {
                            viewModel.setBaseScreenSuccessState(
                                SdToteLowestFactorState(
                                    isSuccess = true,
                                ),
                            )
                        } else {
                            coroutineScope.launch {
                                val sdNo = viewModel.getSdToteDocNoLD()
                                viewModel.getLowestFactor(
                                    sdNo = sdNo,
                                    itemBarcode = submitBarcodeLDState.barcode,
                                    barcode = barcodeState,
                                )
                            }
                        }
                    },
                )
            }

            if (submitBarcodeLDState.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name) {
                ExpiryDateListContainer(
                    modifier = Modifier.weight(1f),
                    canEdit = canEditState,
                    quantityAdjustDate = submitBarcodeLDState.quantityAdjustDate,
                    expiryDateList = submitBarcodeLDState.expList,
                    submitBarcodeLDState = submitBarcodeLDState,
                    onAddExpiryDateClick = {
                        viewModel.logEventAddNewExpiryDate(submitBarcodeLDState)
                        viewModel.updateIsAddLotStateLD(true)
                        context.startActivity(
                            Intent(context, OrderReceiveToteInputExpireActivity::class.java),
                        )
                    },
                    onIncrement = { expiryDate, isLotDummy ->
                        viewModel.incrementFreshLifeExpiryDate(expiryDate, isLotDummy)
                    },
                    onDecrement = { expiryDate, isLotDummy ->
                        viewModel.decrementFreshLifeExpiryDate(expiryDate, isLotDummy)
                    },
                )
            } else {
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
                ) {
                    BarcodeDetailsItemContainer(
                        submitBarcodeLDState = submitBarcodeLDState,
                        qty = submitBarcodeLDState.qty,
                        actualQty = submitBarcodeLDState.actualQty,
                    )

                    ActualQtyCounterContainer(
                        modifier = Modifier,
                        canEdit = canEditState,
                        actualQty = actualQtyState,
                        onActualQtyChange = {
                            viewModel.logEventAdjustItemQuantity(submitBarcodeLDState)
                            viewModel.setInputActualQtyState(it)
                            viewModel.updateSubmitActualQtyLD(it)
                        },
                    )
                }
            }

            if (showConfirmDialog) {
                TachyonAlertDialog(
                    TachyonAlertDialogUIModel(
                        type = ErrorType.QUESTION,
                        titleText = stringResource(R.string.text_confirm_exit_title_dialog),
                        messageText = stringResource(R.string.text_confirm_exit_detail_dialog),
                        negativeTextRes = R.string.text_cancel,
                        onNegativeClick = {
                            showConfirmDialog = false
                        },
                        positiveTextRes = R.string.text_confirm,
                        onPositiveClick = {
                            viewModel.resetSubmitBarcodeLD()
                            (context as ComponentActivity).finish()
                            showConfirmDialog = false
                        },
                    ),
                )
            }
        }
    }
}

@Composable
private fun TopAppBarContainer(onNavigationClick: () -> Unit) {
    TachyonTopAppBar(
        titleText = stringResource(id = R.string.text_set_qty),
        onNavigationIconClick = onNavigationClick,
    )
}

@Composable
private fun BarcodeDetailsItemContainer(
    submitBarcodeLDState: SdToteSubmitUIModel,
    actualQty: Int,
    qty: Int,
    expiryData: String? = null,
) {
    val submitBarcodeLD = remember { submitBarcodeLDState }
    val currentActualQty = remember { actualQty }

    Column(
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
    ) {
        Row {
            TachyonText(
                text = stringResource(id = R.string.text_product_info),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text =
                    if (submitBarcodeLD.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name) {
                        " (วันที่ ${expiryData ?: "-"})"
                    } else {
                        ""
                    },
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }

        TachyonListItemColumn(
            canClick = false,
        ) {
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
                    if (currentActualQty != 0) {
                        if (qty > 0 && qty == currentActualQty) {
                            TachyonSolidChip(
                                status = TachyonSolidChipStatus.SUCCESS,
                                chipText = stringResource(R.string.text_chip_complete),
                            )
                        } else if (qty < currentActualQty) {
                            TachyonSolidChip(
                                status = TachyonSolidChipStatus.WARNING,
                                chipText =
                                    stringResource(
                                        R.string.text_chip_over,
                                        currentActualQty - qty,
                                    ),
                            )
                        } else if (currentActualQty > 0) {
                            TachyonSolidChip(
                                status = TachyonSolidChipStatus.ERROR,
                                chipText =
                                    stringResource(
                                        R.string.text_chip_under,
                                        qty - currentActualQty,
                                    ),
                            )
                        }
                    }

                    TachyonText(
                        text = submitBarcodeLD.barcode,
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
                        text = submitBarcodeLD.unitName,
                        style = TachyonDictionary.body._2,
                        color = TachyonDictionary.content._default,
                    )
                }
            }

            TachyonText(
                text = submitBarcodeLD.productName,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
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
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TachyonDictionary.surface.static._default2)
                    .padding(TachyonDictionary.space.padding._md),
        ) {
            TachyonNumberCounter(
                modifier = Modifier.fillMaxWidth(),
                enabled = canEdit,
                readOnly = true,
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
private fun ExpiryDateListContainer(
    modifier: Modifier,
    canEdit: Boolean,
    quantityAdjustDate: String?,
    expiryDateList: List<ISdToteExpiryDateModel>,
    submitBarcodeLDState: SdToteSubmitUIModel,
    onAddExpiryDateClick: () -> Unit,
    onIncrement: (Date?, Boolean) -> Unit,
    onDecrement: (Date?, Boolean) -> Unit,
) {
    val coroutineScope = rememberCoroutineScope()
    val expiryDateListGroup = expiryDateList.map { it.expDate }.distinct()
    val scrollState = rememberScrollState()

    var previousListSize by remember { mutableIntStateOf(expiryDateListGroup.size) }
    Timber.d("expList : $expiryDateListGroup")
    Timber.d("previousListSize : $previousListSize")
    LaunchedEffect(expiryDateListGroup.size) {
        Timber
            .tag("STATE")
            .i("ExpiryDateListContainer currentSize=${expiryDateListGroup.size}, previous=$previousListSize")

        if (previousListSize != expiryDateListGroup.size) {
            coroutineScope.launch {
                Timber.tag("STATE").i("ExpiryDateListContainer before animateScrollToItem")
                scrollState.animateScrollTo(0)
            }
        }
        previousListSize = expiryDateListGroup.size
    }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._3xl),
        ) {
            quantityAdjustDate?.let { date ->
                if (expiryDateList.none { it.expDate?.toStringDDMMYYYYFormat() == date }) {
                    TachyonEmptyLazyColumn(
                        modifier =
                            Modifier
                                .weight(1f)
                                .fillMaxSize(),
                        painter = painterResource(R.drawable.icon_empty_document),
                        description = stringResource(R.string.text_docs_list_empty),
                    )
                }
            }
            val filteredList =
                quantityAdjustDate?.let { date ->
                    expiryDateList.filter { it.expDate?.toStringDDMMYYYYFormat() == date }
                } ?: expiryDateList
            filteredList
                .groupBy {
                    it.expDate?.toStringDDMMYYYYFormat()
                }.forEach { (expireDate, expiryLotData) ->
                    Timber
                        .tag("STATE")
                        .d("ExpiryDateListContainer groupedItems = $expiryLotData")
                    key(expireDate) {
                        Column(
                            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
                        ) {
                            val qty = expiryLotData.sumOf { it.qty }
                            val actualQty = expiryLotData.sumOf { it.actualQty }
                            val (expiryListQrCode, expiryListBarcode) =
                                expiryLotData.partition {
                                    it.lotNo !=
                                        OrderReceiveToteLotType.LOTDUMMY.name
                                }

                            BarcodeDetailsItemContainer(
                                submitBarcodeLDState = submitBarcodeLDState,
                                qty = qty,
                                actualQty = actualQty,
                                expiryData = expireDate,
                            )
                            ExpiryDateDetailsContainer(
                                expiryListQrCode = expiryListQrCode,
                                expiryListBarcode = expiryListBarcode,
                                canEdit = canEdit,
                                onIncrement = { _, isLotDummy ->
                                    onIncrement(expiryLotData.first().expDate, isLotDummy)
                                },
                                onDecrement = { _, isLotDummy ->
                                    onDecrement(expiryLotData.first().expDate, isLotDummy)
                                },
                            )
                        }
                    }
                }
            if (submitBarcodeLDState.isAddLot) {
                AddItemContainer(
                    canEdit = canEdit,
                    onAddExpiryDateClick = {
                        onAddExpiryDateClick.invoke()
                    },
                )
            }
        }
    }
}

@Composable
private fun ExpiryDateDetailsContainer(
    expiryListQrCode: List<ISdToteExpiryDateModel>,
    expiryListBarcode: List<ISdToteExpiryDateModel>,
    canEdit: Boolean,
    onIncrement: (Int, Boolean) -> Unit,
    onDecrement: (Int, Boolean) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize(),
        verticalArrangement =
            Arrangement.spacedBy(
                space = TachyonDictionary.space.padding._xs,
            ),
    ) {
        if (expiryListQrCode.isNotEmpty()) {
            TachyonListItemRow(
                canClick = false,
            ) {
                ExpiryDataCounter(
                    title = stringResource(R.string.text_scan_from_qr_code),
                    canEdit = canEdit,
                    qty = expiryListQrCode.sumOf { it.actualQty },
                    onIncrement = { actualQty ->
                        onIncrement.invoke(actualQty, false)
                    },
                    onDecrement = { actualQty ->
                        onDecrement.invoke(actualQty, false)
                    },
                )
            }
        }

        if (expiryListBarcode.isNotEmpty()) {
            TachyonListItemRow(
                canClick = false,
            ) {
                ExpiryDataCounter(
                    title = stringResource(R.string.text_scan_from_barcode),
                    canEdit = canEdit,
                    qty = expiryListBarcode.sumOf { it.actualQty },
                    onIncrement = { actualQty ->
                        onIncrement.invoke(actualQty, true)
                    },
                    onDecrement = { actualQty ->
                        onDecrement.invoke(actualQty, true)
                    },
                )
            }
        }
    }
}

@Composable
private fun ExpiryDataCounter(
    title: String,
    canEdit: Boolean,
    qty: Int,
    onIncrement: (Int) -> Unit = {},
    onDecrement: (Int) -> Unit = {},
) {
    Column(
        verticalArrangement =
            Arrangement.spacedBy(
                space = TachyonDictionary.space.padding._md,
            ),
    ) {
        TachyonText(
            text = title,
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )
        TachyonNumberCounter(
            modifier = Modifier.fillMaxWidth(),
            enabled = canEdit,
            readOnly = true,
            initialNumber = qty,
            onIncrement = onIncrement,
            onDecrement = onDecrement,
        )
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
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonOutlinedButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            style = TachyonOutlinedButtonStyle.GENERAL,
            buttonText = stringResource(id = R.string.text_cancel),
            onClick = {
                onCancelClick()
            },
        )

        TachyonSolidButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            enabled = canEdit,
            style = TachyonSolidButtonStyle.GENERAL,
            buttonText = stringResource(id = R.string.common_save),
            onClick = {
                onSubmitClick()
            },
        )
    }
}

@Composable
private fun AddItemContainer(
    canEdit: Boolean,
    onAddExpiryDateClick: () -> Unit,
) {
    Column(
        modifier =
            Modifier
                .border(
                    width = 1.dp,
                    color = TachyonDictionary.border._default,
                    shape = RoundedCornerShape(TachyonDictionary.space.padding._2xs),
                ).fillMaxWidth()
                .padding(
                    all = TachyonDictionary.space.padding._md,
                ),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        TachyonText(
            text = stringResource(R.string.text_add_lot_item_title),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
            textAlign = TextAlign.Center,
        )

        TachyonOutlinedButton(
            modifier = Modifier.fillMaxWidth(),
            style = TachyonOutlinedButtonStyle.GENERAL,
            buttonText = stringResource(R.string.text_add_lot_item_button),
            enabled = canEdit,
            onClick = {
                onAddExpiryDateClick.invoke()
            },
        )
    }
}
