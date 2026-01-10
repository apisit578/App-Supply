package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.analytics.ProcessStatusType
import th.co.cjexpress.store.sage.analytics.ProcessTimeTrackingType.ORDER_RECEIVE_PROCESS
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.presentation.activity.orderReceive.OrderReceiveDocsActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceive.OrderReceiveItemsActivity
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryCompleteUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.OrderReceiveSummaryItemUIModel
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnEmptyState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnItem
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChip
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidChipStatus
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.screen.base.TopAppBarConfigs
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveSummaryViewModel

class OrderReceiveSummaryScreen(
    viewModel: OrderReceiveSummaryViewModel,
) : BaseScreen<OrderReceiveSummaryViewModel>(viewModel) {
    @Composable
    override fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        val context = LocalContext.current

        super.TopAppBarContainer(
            configs =
                TopAppBarConfigs(
                    titleTextRes = R.string.text_summary_title_summary,
                    onNavigationIconClick = {
                        viewModel.logEventOnBackPressed()
                        context
                            .startActivity(Intent(context, OrderReceiveItemsActivity::class.java))
                            .also {
                                (context as ComponentActivity).finish()
                            }
                    },
                ),
        )
    }

    @Composable
    override fun BottomAppBarContainer() {
        val summaryState by viewModel.summaryState.collectAsState()

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonSolidButton(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                style = TachyonSolidButtonStyle.GENERAL,
                buttonText = stringResource(R.string.text_confirm),
                onClick = { viewModel.onButtonConfirmClick(sdNo = summaryState.data.sdNo) },
            )
        }
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current

        when (data) {
            is OrderReceiveSummaryViewModel.UIState.NavBack -> {
                context
                    .startActivity(
                        Intent(
                            context,
                            OrderReceiveItemsActivity::class.java,
                        ),
                    ).also {
                        (context as ComponentActivity).finish()
                    }
            }

            is OrderReceiveSummaryCompleteUIModel -> {
                if (data.isComplete) {
                    TachyonAlertDialog(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.SUCCESS,
                            titleText = stringResource(R.string.text_save_success),
                            messageText =
                                stringResource(
                                    R.string.text_format_shipment_document_no,
                                    data.sdNo,
                                ),
                            onPositiveClick = {
                                context
                                    .startActivity(
                                        Intent(
                                            context,
                                            OrderReceiveDocsActivity::class.java,
                                        ),
                                    ).also {
                                        (context as ComponentActivity).finish()
                                    }
                            },
                        ),
                    )
                } else {
                    TachyonAlertDialog(
                        TachyonAlertDialogUIModel(
                            type = ErrorType.QUESTION,
                            titleText = stringResource(R.string.text_confirm_to_save_document),
                            messageText = stringResource(R.string.text_confirm_to_order_receive),
                            negativeTextRes = R.string.text_no,
                            onNegativeClick = {
                                viewModel.onButtonDialogConfirmCancelClick()
                            },
                            positiveTextRes = R.string.text_yes,
                            onPositiveClick = {
                                viewModel.onButtonDialogConfirmCompleteClick(
                                    sdNo = data.sdNo,
                                    isComplete = true,
                                )
                            },
                        ),
                    )
                }
            }
        }
    }

    @Composable
    override fun ScreenContent() {
        val lifecycleOwner = LocalLifecycleOwner.current
        val summaryState by viewModel.summaryState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.loadSummary()
            viewModel.logEventScreenView()
            viewModel.trackTimeTracking(
                type = ORDER_RECEIVE_PROCESS,
                status = ProcessStatusType.IN_PROCESS,
            )
        }

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

        OrderReceiveSummaryContainer(items = summaryState.data.items)
    }

    @Composable
    fun OrderReceiveSummaryContainer(items: List<OrderReceiveSummaryItemUIModel>) {
        TachyonLazyColumn(
            modifier = Modifier.fillMaxSize(),
            rowHeaderText = {
                TachyonText(
                    text =
                        stringResource(R.string.text_product_list, items.size),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
                TachyonText(
                    text = stringResource(R.string.text_receive_or_reference),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            },
            items = items,
            itemKey = { it.barcode },
            isScrollToTopWhenUpdatedItem = true,
            emptyState =
                TachyonLazyColumnEmptyState(
                    painter = painterResource(id = R.drawable.icon_checked),
                    descriptionText = stringResource(id = R.string.text_no_difference_found),
                ),
        ) { summaryData ->
            SummaryItemContainer(
                summaryData = summaryData,
            )
        }
    }

    @Composable
    private fun SummaryItemContainer(summaryData: OrderReceiveSummaryItemUIModel) {
        TachyonLazyColumnItem(
            canClick = false,
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
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TachyonSolidChip(
                        status =
                            when (summaryData.isMissing) {
                                true -> TachyonSolidChipStatus.ERROR
                                else -> TachyonSolidChipStatus.WARNING
                            },
                        chipText = summaryData.chipText,
                    )
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
                            text = summaryData.unitCount,
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )
                        TachyonText(
                            text = summaryData.unitName,
                            style = TachyonDictionary.body._2,
                            color = TachyonDictionary.content._default,
                        )
                    }
                }

                TachyonText(
                    text = summaryData.barcode,
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )

                Column {
                    TachyonText(
                        text = summaryData.productName,
                        style = TachyonDictionary.body._2,
                        color = TachyonDictionary.content._default,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                    )
                }
            }
        }
    }
}
