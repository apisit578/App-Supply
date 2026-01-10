package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import kotlinx.coroutines.delay
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.activity.orderReceive.OrderReceiveItemsActivity
import th.co.cjexpress.store.sage.presentation.model.component.TachyonContentDialogUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentListItemUIModel
import th.co.cjexpress.store.sage.presentation.model.orderReceive.ShipmentDocumentUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceive.ShipmentDocumentListState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonContentDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnEmptyState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnItem
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnPullToRefreshState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedTextField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.screen.base.TopAppBarConfigs
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveDocsViewModel
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce
import th.co.cjexpress.store.sage.utils.safeKeyboardActionWithDebounce

class OrderReceiveDocsScreen(
    viewModel: OrderReceiveDocsViewModel,
) : BaseScreen<OrderReceiveDocsViewModel>(viewModel) {
    @Composable
    override fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        val context = LocalContext.current

        super.TopAppBarContainer(
            configs =
                TopAppBarConfigs(
                    titleTextRes = R.string.text_order_receive,
                    onNavigationIconClick = {
                        viewModel.logEventOnBackPressed()
                        context.startActivity(Intent(context, MainMenuActivity::class.java)).also {
                            (context as ComponentActivity).finish()
                        }
                    },
                ),
        )
    }

    @Composable
    override fun BottomAppBarContainer() {
        val context = LocalContext.current
        val showScanNewDocDialogState by viewModel.showScanNewDocDialogState.collectAsState()
        if (showScanNewDocDialogState) {
            ScanNewDocDialog(
                dismissNewDocDialog = {
                    viewModel.clearInputShipmentDocumentState()
                    viewModel.hideKeyboard(context)
                    viewModel.setShowScanNewDocDialogState(false)
                },
                onConfirm = { ldNo ->
                    viewModel.shipmentDocumentOpen(ldNo)
                    viewModel.setShowScanNewDocDialogState(false)
                },
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.global.spacing._xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TachyonSolidButton(
                modifier = Modifier.fillMaxWidth(),
                buttonText = stringResource(R.string.text_start_order_receive),
                style = TachyonSolidButtonStyle.GENERAL,
            ) {
                viewModel.clearInputShipmentDocumentState()
                viewModel.setShowScanNewDocDialogState(true)
                viewModel.trackPressStartReceiveEvent()
            }
        }
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current

        when (data) {
            is ShipmentDocumentUIModel -> {
                viewModel.setupShipmentSummaryLiveData(data)
                context
                    .startActivity(
                        Intent(context, OrderReceiveItemsActivity::class.java),
                    ).also {
                        (context as ComponentActivity).finish()
                    }
            }
        }
    }

    @Composable
    override fun ScreenContent() {
        val shipmentDocumentListState by viewModel.shipmentDocumentListState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.resetShipmentSummaryLiveData()
            viewModel.getSdDocsList()
        }

        ShipmentDocumentListContainer(shipmentDocumentListState = shipmentDocumentListState)
    }

    @Composable
    fun ShipmentDocumentListContainer(shipmentDocumentListState: ShipmentDocumentListState) {
        val pullingState by viewModel.baseScreenState.collectAsState()

        TachyonLazyColumn(
            modifier = Modifier.fillMaxSize(),
            rowHeaderText = {
                TachyonText(
                    text =
                        stringResource(
                            R.string.text_format_list_document,
                            shipmentDocumentListState.data?.documentListSize ?: "0",
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            },
            pullToRefreshState =
                TachyonLazyColumnPullToRefreshState(
                    pullToRefreshLoadingState = pullingState is BaseScreenState.Loading,
                    pullToRefreshCallback = { viewModel.getSdDocsList() },
                ),
            items = shipmentDocumentListState.data?.shipmentDocumentList ?: emptyList(),
            itemKey = { docs -> docs.sdNo },
            isScrollToTopWhenUpdatedItem = true,
            emptyState =
                TachyonLazyColumnEmptyState(
                    painter = painterResource(id = R.drawable.icon_empty_document),
                    descriptionText = stringResource(id = R.string.text_docs_list_empty),
                ),
        ) { document ->
            ShipmentDocumentDetailItemContainer(
                document = document,
                onDocSelected = { docNo ->
                    viewModel.shipmentDocumentBySDNo(docNo)
                },
            )
        }
    }

    @Composable
    private fun ShipmentDocumentDetailItemContainer(
        document: ShipmentDocumentListItemUIModel,
        onDocSelected: (String) -> Unit,
    ) {
        TachyonLazyColumnItem(
            onClick = {
                onDocSelected(document.sdNo)
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
                TachyonText(
                    text =
                        stringResource(
                            R.string.text_format_date,
                            document.date,
                        ),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text = stringResource(R.string.text_format_shipment_document_no, document.sdNo),
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text = stringResource(R.string.text_format_shipment_document_ld_no, document.ldNo),
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }
    }

    @Composable
    private fun ScanNewDocDialog(
        dismissNewDocDialog: () -> Unit,
        onConfirm: (String) -> Unit,
    ) {
        val inputShipmentDocumentState by viewModel.inputShipmentDocumentState.collectAsState()

        TachyonContentDialog(
            dialogData =
                TachyonContentDialogUIModel(
                    onPositiveClick = {
                        onConfirm(inputShipmentDocumentState.text)
                    },
                    positiveTextRes = R.string.text_ok,
                    onNegativeClick = {
                        dismissNewDocDialog()
                    },
                    negativeTextRes = R.string.text_cancel,
                ),
            titleTextRes = R.string.text_please_scan_ld,
            content = {
                ContentDialog(
                    onConfirm = onConfirm,
                )
            },
        )
    }

    @Composable
    private fun ContentDialog(onConfirm: (String) -> Unit) {
        val inputShipmentDocumentState by viewModel.inputShipmentDocumentState.collectAsState()
        val focusRequester = remember { FocusRequester() }

        LaunchedEffect(Unit) {
            delay(DELAY_DEFAULT_TIME)
            focusRequester.requestFocus()
        }

        TachyonOutlinedTextField(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(focusRequester),
            value = inputShipmentDocumentState,
            supportingText = {},
            onValueChange = { viewModel.setInputShipmentDocumentState(it) },
            placeholder = {
                TachyonText(
                    text = stringResource(R.string.text_shipment_document_ld_no),
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content._description,
                )
            },
            trailingIcon = {
                if (inputShipmentDocumentState.text.isNotEmpty()) {
                    IconButton(
                        onClick =
                            safeClickWithDebounce {
                                viewModel.clearInputShipmentDocumentState()
                            },
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = stringResource(R.string.text_clear),
                        )
                    }
                }
            },
            keyboardActions =
                KeyboardActions(
                    onDone =
                        safeKeyboardActionWithDebounce {
                            onConfirm(inputShipmentDocumentState.text)
                        },
                ),
        )
    }
}
