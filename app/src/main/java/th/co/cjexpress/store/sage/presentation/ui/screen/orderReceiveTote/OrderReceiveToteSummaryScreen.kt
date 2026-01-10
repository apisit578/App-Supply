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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.OrderReceiveToteDiscountType
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteDocsActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteItemDetailActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceiveTote.OrderReceiveToteItemsActivity
import th.co.cjexpress.store.sage.presentation.model.orderReceiveTote.SdToteDocsDetailBarcodeUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteBarcodeEditingSubmitState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonEmptyLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonListItemColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChip
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChipStatus
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.screen.base.TopAppBarConfigs
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteSummaryViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteSummaryViewModel.UIState
import th.co.cjexpress.store.sage.utils.DateUtils.toStringDDMMYYYYFormat
import timber.log.Timber

class OrderReceiveToteSummaryScreen(
    viewModel: OrderReceiveToteSummaryViewModel,
) : BaseScreen<OrderReceiveToteSummaryViewModel>(viewModel) {
    @Composable
    override fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        TopAppBarContainer(
            onNavigationClick = {
                viewModel.navBack()
            },
        )
    }

    @Composable
    override fun BottomAppBarContainer() {
        BottomButtonContainer(
            onBackToMainMenuClick = {
                viewModel.navToMainMenu()
            },
            onConfirmClick = {
                viewModel.validateBeforeApproveLD()
            },
        )
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current

        when (data) {
            is UIState.NavBack -> {
                viewModel.logEventOnBackPressed()
                context
                    .startActivity(Intent(context, OrderReceiveToteItemsActivity::class.java))
                    .also {
                        (context as ComponentActivity).finish()
                    }
            }

            is UIState.NavToMain -> {
                viewModel.logEventOnBackMainMenu()
                viewModel.resetSdToteDocsDetailLD()
                context.startActivity(Intent(context, MainMenuActivity::class.java)).also {
                    (context as ComponentActivity).finish()
                }
            }

            is UIState.NavToToteDocs -> {
                viewModel.resetBaseScreenState()
                viewModel.resetSdToteDocsDetailLD()
                context
                    .startActivity(Intent(context, OrderReceiveToteDocsActivity::class.java))
                    .also {
                        (context as ComponentActivity).finish()
                    }
            }

            is UIState.NavToToteItemDetail -> {
                context.startActivity(
                    Intent(context, OrderReceiveToteItemDetailActivity::class.java),
                )
            }

            is SdToteBarcodeEditingSubmitState -> {
                Timber.tag("STATE").i("is SdToteBarcodeEditingSubmitState")
                data.data?.let {
                    viewModel.setSubmitBarcodeDataLD(it)
                    viewModel.updateIsAddLotStateLD(false)
                    viewModel.navToToteItemDetail()
                }
            }
        }
    }

    @Composable
    override fun ScreenContent() {
        rememberCoroutineScope()
        val docsDetailLDState by viewModel.docsDetailFlowLD.collectAsState()
        val barcodeGroupedItems = viewModel.groupBarcodeByExpiryDate(docsDetailLDState.items)
        val incompleteItems = viewModel.getIncompleteItems(barcodeGroupedItems)

        LaunchedEffect(Unit) {
            viewModel.logEventScreenView(
                message = docsDetailLDState.toString(),
            )
        }

        BarcodeListContainer(
            modifier = Modifier.fillMaxSize(),
            barcodeList = incompleteItems,
            onBarcodeSelected = { barcode, expDate ->
                Timber.tag("STATE").i("BarcodeListContainer selectBarcodeToEdit = $barcode")

                viewModel.searchBarcodeLD(barcode)?.let { barcodeItem ->
                    viewModel
                        .createSubmitBarcodeLD(
                            isNewBarcode = false,
                            barcodeData = barcodeItem,
                            actualQty = barcodeItem.actualQty,
                            expList = barcodeItem.expList,
                        ).apply {
                            this.productName = barcodeItem.productName
                            this.unitName = barcodeItem.unitName
                            this.qty = barcodeItem.qty
                            this.quantityAdjustDate = expDate
                        }.let { submitData ->
                            viewModel.barcodeSelected(submitData)
                        }
                }
            },
        )
    }
}

@Composable
private fun TopAppBarContainer(onNavigationClick: () -> Unit) {
    TachyonTopAppBar(
        titleText = stringResource(id = R.string.text_summary_title),
        onNavigationIconClick = onNavigationClick,
    )
}

@Composable
private fun BarcodeListContainer(
    modifier: Modifier,
    barcodeList: List<SdToteDocsDetailBarcodeUIModel>,
    onBarcodeSelected: (barcode: String, expDate: String?) -> Unit,
) {
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
                text = stringResource(id = R.string.text_result),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }

        if (barcodeList.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
            ) {
                items(
                    items = barcodeList,
                    key = { product ->
                        val expDate =
                            product.expList
                                .firstOrNull()
                                ?.expDate
                                ?.toStringDDMMYYYYFormat()
                        "${product.barcode}_${expDate}_${product.actualQty}"
                    },
                ) { product ->
                    BarcodeDetailsItemContainer(
                        barcodeDetail = product,
                        onBarcodeSelected = { barcode, expDate ->
                            //                            viewModel.selectBarcodeToEdit(barcode = it)
                            onBarcodeSelected(barcode, expDate)
                        },
                    )
                }
            }
        } else {
            TachyonEmptyLazyColumn(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                painter = painterResource(R.drawable.icon_checked),
                description = stringResource(id = R.string.text_no_difference_found),
            )
        }
    }
}

@Composable
private fun BarcodeDetailsItemContainer(
    barcodeDetail: SdToteDocsDetailBarcodeUIModel,
    onBarcodeSelected: (barcode: String, expDate: String?) -> Unit,
) {
    TachyonListItemColumn(
        onClick = {
            onBarcodeSelected(
                barcodeDetail.barcode,
                if (barcodeDetail.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name) {
                    barcodeDetail.expList
                        .firstOrNull()
                        ?.expDate
                        ?.toStringDDMMYYYYFormat()
                } else {
                    null
                },
            )
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
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            space = TachyonDictionary.space.padding._xs,
                        ),
                ) {
                    if (barcodeDetail.actualQty > barcodeDetail.qty) {
                        TachyonSolidChip(
                            status = TachyonSolidChipStatus.WARNING,
                            chipText =
                                stringResource(
                                    R.string.text_chip_over,
                                    barcodeDetail.actualQty - barcodeDetail.qty,
                                ),
                        )
                    } else {
                        TachyonSolidChip(
                            status = TachyonSolidChipStatus.ERROR,
                            chipText =
                                stringResource(
                                    R.string.text_chip_under,
                                    barcodeDetail.qty - barcodeDetail.actualQty,
                                ),
                        )
                    }

                    if (barcodeDetail.qrCodePrint > 0) {
                        TachyonSolidChip(
                            status = TachyonSolidChipStatus.PROCESS,
                            chipText =
                                stringResource(
                                    id = R.string.text_print_qr_code,
                                    barcodeDetail.qrCodePrint,
                                ),
                        )
                    }
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

        if (barcodeDetail.discountType == OrderReceiveToteDiscountType.FRESHLIFE.name) {
            val formattedDate =
                barcodeDetail.expList
                    .firstOrNull()
                    ?.expDate
                    ?.toStringDDMMYYYYFormat()

            TachyonText(
                text = stringResource(R.string.text_product_expire_date, formattedDate ?: "-"),
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun BottomButtonContainer(
    onBackToMainMenuClick: () -> Unit,
    onConfirmClick: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement =
            Arrangement.spacedBy(
                space = TachyonDictionary.space.padding._xl,
            ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonOutlinedButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            style = TachyonOutlinedButtonStyle.GENERAL,
            buttonText = stringResource(id = R.string.text_back_to_main_menu),
            onClick = {
                onBackToMainMenuClick()
            },
        )

        TachyonSolidButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            style = TachyonSolidButtonStyle.GENERAL,
            buttonText = stringResource(id = R.string.text_confirm),
            onClick = {
                onConfirmClick()
            },
        )
    }
}
