package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceiveTote

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.domain.model.QRFreshDecodeDataModel
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteDocsActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteInputExpireActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteItemDetailActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteSummaryActivity
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDiscountsConfigUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteInputExpiryDateSubmitState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBottomAppBar
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonListItemColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChip
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChipStatus
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteItemsViewModel
import timber.log.Timber

@Composable
fun OrderReceiveToteItemsScreen(viewModel: OrderReceiveToteItemsViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val submitBarcodeLDState by viewModel.submitBarcodeFlowLD.collectAsState(null)

    Timber
        .tag("STATE")
        .i(
            "=====\r\nOrderReceiveToteItemsScreen composing \r\nbaseScreenState = $baseScreenState\r\nsubmitBarcodeLDState =" +
                " $submitBarcodeLDState\r\n=====",
        )

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
            Timber.tag("STATE").i("is BaseScreenState.Success<*> = ${state.data}")

            when (state.data) {
                is SdToteDiscountsConfigUIModel -> {
                    viewModel.createNewBarcodeOrch(state.data)
                }

                is SdToteInputExpiryDateSubmitState -> {
                    Timber.tag("STATE").i("is SdToteInputExpiryDateSubmitState")
                    viewModel.prepareInputExpiryDateOrch(state.data.data!!)
                    context.startActivity(
                        Intent(
                            context,
                            OrderReceiveToteInputExpireActivity::class.java,
                        ),
                    )
                }

                is SdToteBarcodeEditingSubmitState -> {
                    Timber.tag("STATE").i("is SdToteBarcodeEditingSubmitState")
                    viewModel.prepareEditingBarcodeOrch(state.data.data!!)
                    context.startActivity(
                        Intent(
                            context,
                            OrderReceiveToteItemDetailActivity::class.java,
                        ),
                    )
                }
            }
        }

        else -> {}
    }

    OrderReceiveToteItemsScreenContainer(viewModel)
}

@Composable
private fun OrderReceiveToteItemsScreenContainer(viewModel: OrderReceiveToteItemsViewModel) {
    val context = LocalContext.current
    val docsDetailLDState by viewModel.docsDetailFlowLD.collectAsState(null)

    Scaffold(
        topBar = {
            TopAppBarContainer(
                onNavigationClick = {
                    viewModel.logEventOnBackPressed()
                    context
                        .startActivity(
                            Intent(context, OrderReceiveToteDocsActivity::class.java),
                        ).also {
                            (context as ComponentActivity).finish()
                        }
                },
            )
        },
        bottomBar = {
            TachyonBottomAppBar {
                BottomAppBarContainer(
                    onClick = {
                        viewModel.logEventCheckTote()
                        context
                            .startActivity(
                                Intent(context, OrderReceiveToteSummaryActivity::class.java),
                            ).also {
                                (context as ComponentActivity).finish()
                            }
                    },
                )
            }
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            TachyonBarcodeInputField(
                label = stringResource(R.string.title_barcode_qr_code),
                placeholder = stringResource(R.string.text_please_fill_or_scan),
                decodeType = DecodeType.BARCODE_CODEC,
                onValidateBarcode = { decoded ->
                    val isQRFresh = decoded.data is QRFreshDecodeDataModel
                    viewModel.logEventScanBarcode(isQRFresh, decoded.barcode)
                    viewModel.searchDecodedBarcodeOrch(decoded)
                },
            )

            BarcodeListContainer(
                modifier = Modifier.weight(1f),
                barcodeList = docsDetailLDState?.items ?: emptyList(),
                onBarcodeSelected = { barcode ->
                    Timber.tag("STATE").i("BarcodeListContainer selectBarcodeToEdit = $barcode")
                    viewModel.selectedBarcodeToEditOrch(barcode)
                },
            )
        }
    }
}

@Composable
private fun TopAppBarContainer(onNavigationClick: () -> Unit) {
    TachyonTopAppBar(
        titleText = stringResource(R.string.text_check_product_in_tote),
        onNavigationIconClick = onNavigationClick,
    )
}

@Composable
private fun BarcodeListContainer(
    modifier: Modifier,
    barcodeList: List<SdToteDocsDetailBarcodeUIModel>,
    onBarcodeSelected: (String) -> Unit,
) {
    val lazyListState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    Timber.tag("STATE").i("BarcodeListContainer composing ${barcodeList.size}")

    SideEffect {
        if (barcodeList.isNotEmpty()) {
            Timber.tag("STATE").i("BarcodeListContainer before animateScrollToItem")
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
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            TachyonText(
                text = stringResource(R.string.text_product_list, barcodeList.size),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )

            TachyonText(
                text = stringResource(R.string.text_receive_or_reference),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
            state = lazyListState,
        ) {
            items(
                items = barcodeList,
                key = { product -> "${product.barcode}_${product.actualQty}" },
            ) { product ->
                BarcodeDetailsItemContainer(
                    barcodeDetail = product,
                    onBarcodeSelected = {
                        Timber.tag("STATE").i("LazyColumn selectBarcodeToEdit = $it")
                        onBarcodeSelected(it)
                    },
                )
            }
        }
    }
}

@Composable
private fun BarcodeDetailsItemContainer(
    barcodeDetail: SdToteDocsDetailBarcodeUIModel,
    onBarcodeSelected: (String) -> Unit,
) {
    TachyonListItemColumn(
        onClick = {
            Timber
                .tag("STATE")
                .i("TachyonListItemRow onClick = ${barcodeDetail.barcode}")
            onBarcodeSelected(barcodeDetail.barcode)
        },
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
                if (barcodeDetail.qty > 0 && barcodeDetail.qty == barcodeDetail.actualQty) {
                    TachyonSolidChip(
                        status = TachyonSolidChipStatus.SUCCESS,
                        chipText = stringResource(R.string.text_chip_complete),
                    )
                } else if (barcodeDetail.qty < barcodeDetail.actualQty) {
                    TachyonSolidChip(
                        status = TachyonSolidChipStatus.WARNING,
                        chipText =
                            stringResource(
                                R.string.text_chip_over,
                                barcodeDetail.actualQty - barcodeDetail.qty,
                            ),
                    )
                } else if (barcodeDetail.actualQty > 0) {
                    TachyonSolidChip(
                        status = TachyonSolidChipStatus.ERROR,
                        chipText =
                            stringResource(
                                R.string.text_chip_under,
                                barcodeDetail.qty - barcodeDetail.actualQty,
                            ),
                    )
                }

                TachyonText(
                    text = barcodeDetail.barcode,
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
                    text = "${barcodeDetail.actualQty}/${barcodeDetail.qty}",
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text = barcodeDetail.unitName,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }

        TachyonText(
            text = barcodeDetail.productName,
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun BottomAppBarContainer(onClick: () -> Unit = {}) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonSolidButton(
            modifier =
                Modifier
                    .fillMaxWidth(),
            style = TachyonSolidButtonStyle.GENERAL,
            buttonText = stringResource(R.string.text_check),
            onClick = {
                onClick.invoke()
            },
        )
    }
}
