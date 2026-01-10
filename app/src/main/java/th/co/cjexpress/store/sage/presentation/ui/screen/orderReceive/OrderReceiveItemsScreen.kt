package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS
import th.co.cjexpress.store.sage.constants.PromotionType
import th.co.cjexpress.store.sage.constants.ScanBarcodeButtonType
import th.co.cjexpress.store.sage.presentation.activity.orderReceive.OrderReceiveDocsActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceive.OrderReceiveItemDetailActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceive.OrderReceiveSummaryActivity
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDataItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeBackState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeCheckState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentByBarcodeStartOverState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.DecodeType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBarcodeInputField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnEmptyState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChip
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChipStatus
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.screen.base.TopAppBarConfigs
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemsViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemsViewModel.UIState
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

class OrderReceiveItemsScreen(
    viewModel: OrderReceiveItemsViewModel,
) : BaseScreen<OrderReceiveItemsViewModel>(viewModel) {
    @Composable
    override fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        super.TopAppBarContainer(
            configs =
                TopAppBarConfigs(
                    titleTextRes = R.string.text_title_scan_order_receive,
                    onNavigationIconClick = {
                        viewModel.promptConfirmBack()
                    },
                ),
        )
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current
        when (data) {
            is UIState.NavToItemDetail -> {
                viewModel.resetBaseScreenState()
                context.startActivity(
                    Intent(
                        context,
                        OrderReceiveItemDetailActivity::class.java,
                    ),
                )
            }

            is ShipmentDocumentByBarcodeStartOverState -> {
                data.data?.let {
                    viewModel.apply {
                        setCountActualQty(data = data.data.sumOfActualQty)
                        removeShipmentBatchModel()
                        increaseStartOverCount()
                        resetBaseScreenState()
                    }
                }
            }

            is ShipmentDocumentByBarcodeCheckState -> {
                viewModel.trackTimeTracking(
                    type = ORDER_RECEIVE_PROCESS,
                    status = ProcessStatusType.IN_PROCESS,
                )
                context
                    .startActivity(
                        Intent(
                            context,
                            OrderReceiveSummaryActivity::class.java,
                        ),
                    ).also {
                        (context as ComponentActivity).finish()
                    }
            }

            is ShipmentDocumentByBarcodeBackState -> {
                context
                    .startActivity(
                        Intent(
                            context,
                            OrderReceiveDocsActivity::class.java,
                        ),
                    ).also {
                        (context as ComponentActivity).finish()
                    }
            }
        }
    }

    @Composable
    override fun ScreenContent() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val shipmentBatchLiveData by viewModel.shipmentBatchLiveData.collectAsState()

        DisposableEffect(lifecycleOwner) {
            val observer =
                LifecycleEventObserver { _, event ->
                    if (event == Lifecycle.Event.ON_PAUSE) {
                        viewModel.trackTimeTracking(
                            type = ORDER_RECEIVE_PROCESS,
                            status = ProcessStatusType.DONE,
                        )
                    }
                    if (event == Lifecycle.Event.ON_STOP) {
                        viewModel.stopTimeTracking(type = ORDER_RECEIVE_PROCESS)
                    }
                }

            lifecycleOwner.lifecycle.addObserver(observer)

            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

        LaunchedEffect(Unit) {
            viewModel.trackTimeTracking(
                type = ORDER_RECEIVE_PROCESS,
                status = ProcessStatusType.IN_PROCESS,
            )
            viewModel.logEventScreenView()
        }

        BarcodeInputContainer()
        BarcodeItemListContainer(
            shipmentItemList = shipmentBatchLiveData,
            onRemoveBarcode = { barcode ->
                viewModel.removeShipmentBatchItem(barcode)
            },
        )
    }

    @Composable
    fun BarcodeInputContainer() {
        TachyonBarcodeInputField(
            label = stringResource(R.string.text_label_scan_order_receive),
            placeholder = stringResource(R.string.text_please_fill_or_scan),
            decodeType = DecodeType.BARCODE_CODEC,
            acceptedPrefix =
                listOf(
                    PromotionType.COMMON_BARCODE,
                ),
            onValidateBarcode = { decoded ->
                viewModel.checkLimitBatch(decoded.barcode)
            },
        )
    }

    @Composable
    private fun BarcodeItemListContainer(
        shipmentItemList: ShipmentDocumentUIModel? = null,
        onRemoveBarcode: (ShipmentDataItemUIModel) -> Unit,
    ) {
        val countActualQty by viewModel.countActualQty.collectAsState()
        val startOverCount by viewModel.startOverCount.collectAsState()

        TachyonLazyColumn(
            modifier = Modifier.fillMaxSize(),
            rowHeaderText = {
                TachyonText(
                    text =
                        stringResource(
                            R.string.text_product_list,
                            shipmentItemList?.items?.size ?: 0,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
                TachyonText(
                    text =
                        stringResource(
                            R.string.text_amount_receive,
                            shipmentItemList?.sumOfActualQty ?: 0,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            },
            items = shipmentItemList?.items ?: emptyList(),
            itemKey = { product -> "${product.barcode}_${product.actualQty}" },
            isScrollToTopWhenUpdatedItem = true,
            emptyState =
                TachyonLazyColumnEmptyState(
                    painter = painterResource(R.drawable.icon_barcode),
                    descriptionText =
                        if (countActualQty != null) {
                            stringResource(
                                R.string.text_please_scan_barcode_receive_next_round,
                                startOverCount,
                                countActualQty ?: 0,
                            )
                        } else {
                            stringResource(R.string.text_please_scan_barcode_receive)
                        },
                ),
        ) { productData ->
            BarcodeDetailItemListContainer(
                productData,
                onRemoveBarcode,
                onItemClick = { item ->
                    if (item.isTote) return@BarcodeDetailItemListContainer

                    viewModel.setShipmentDocumentItemDetail(item)
                    viewModel.setBaseScreenSuccessState(
                        UIState.NavToItemDetail,
                    )
                },
            )
        }
    }

    @Composable
    override fun BottomAppBarContainer() {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonOutlinedButton(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .weight(1f),
                style = TachyonOutlinedButtonStyle.GENERAL,
                buttonText = stringResource(id = R.string.text_start_over),
                onClick = {
                    viewModel.mapTypeButtonToState(
                        typeButton = ScanBarcodeButtonType.START_OVER,
                    )
                },
            )
            TachyonSolidButton(
                modifier = Modifier.weight(1f),
                style = TachyonSolidButtonStyle.GENERAL,
                buttonText = stringResource(R.string.text_check),
                onClick = {
                    viewModel.mapTypeButtonToState(
                        typeButton = ScanBarcodeButtonType.CHECK,
                    )
                },
            )
        }
    }

    @Composable
    private fun BarcodeDetailItemListContainer(
        shipmentData: ShipmentDataItemUIModel,
        onRemoveBarcode: (ShipmentDataItemUIModel) -> Unit,
        onItemClick: (ShipmentDataItemUIModel) -> Unit,
    ) {
        var lineCount by remember { mutableStateOf(0) }

        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .background(TachyonDictionary.surface.static._default2)
                    .clickable(
                        enabled = !shipmentData.isTote,
                        onClick = safeClickWithDebounce { onItemClick(shipmentData) },
                    ),
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._2xs),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .padding(TachyonDictionary.global.spacing._md),
                verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._2xs),
                    ) {
                        if (shipmentData.overQty > 0) {
                            TachyonSolidChip(
                                status = TachyonSolidChipStatus.WARNING,
                                chipText =
                                    stringResource(
                                        R.string.text_chip_over,
                                        shipmentData.overQty,
                                    ),
                            )
                        }
                        TachyonText(
                            text = if (shipmentData.isTote) shipmentData.toteCode else shipmentData.barcode,
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )
                    }
                    Row(
                        modifier =
                            Modifier
                                .align(if (shipmentData.overQty > 0) Alignment.Top else Alignment.CenterVertically)
                                .fillMaxHeight(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                space = TachyonDictionary.space.padding._xs,
                            ),
                    ) {
                        TachyonText(
                            text = "${shipmentData.actualQty}",
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )

                        TachyonText(
                            text =
                                if (shipmentData.isTote) {
                                    stringResource(R.string.text_unit_carton)
                                } else {
                                    shipmentData.unitName
                                },
                            style = TachyonDictionary.body._2,
                            color = TachyonDictionary.content._default,
                        )
                    }
                }
                Row(
                    horizontalArrangement =
                        Arrangement.spacedBy(
                            space = TachyonDictionary.space.padding._xs,
                        ),
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                ) {
                    TachyonText(
                        modifier = Modifier.weight(1f),
                        text = if (shipmentData.isTote) stringResource(R.string.text_tote) else shipmentData.productName,
                        style = TachyonDictionary.body._2,
                        color = TachyonDictionary.content._default,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        onTextLayout = { textLayoutResult ->
                            lineCount = textLayoutResult.lineCount
                        },
                    )
                    IconButton(
                        onClick = safeClickWithDebounce { onRemoveBarcode(shipmentData) },
                        modifier =
                            Modifier
                                .align(
                                    if (lineCount > 1) {
                                        Alignment.Bottom
                                    } else {
                                        Alignment.CenterVertically
                                    },
                                ).background(
                                    shape = RoundedCornerShape((8 / LocalDensity.current.density).dp),
                                    color = TachyonDictionary.surface.action.delete._default,
                                ).size((32 / LocalDensity.current.density).dp),
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.icons_delete),
                            contentDescription = null,
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
    }
}
