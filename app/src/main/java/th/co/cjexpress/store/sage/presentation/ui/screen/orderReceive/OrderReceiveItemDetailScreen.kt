package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceive

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonNumberCounter
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonLazyColumnItem
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.screen.base.TopAppBarConfigs
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemDetailViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceive.OrderReceiveItemDetailViewModel.UIState

class OrderReceiveItemDetailScreen(
    viewModel: OrderReceiveItemDetailViewModel,
) : BaseScreen<OrderReceiveItemDetailViewModel>(viewModel) {
    @Composable
    override fun TopAppBarContainer(configs: TopAppBarConfigs?) {
        super.TopAppBarContainer(
            configs =
                TopAppBarConfigs(
                    titleTextRes = R.string.text_title_set_receive_qty,
                    onNavigationIconClick = {
                        viewModel.promptBeforeBack()
                    },
                ),
        )
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
                buttonText = stringResource(id = R.string.text_cancel),
                onClick = { viewModel.promptBeforeBack() },
            )
            TachyonSolidButton(
                modifier = Modifier.weight(1f),
                style = TachyonSolidButtonStyle.GENERAL,
                buttonText = stringResource(R.string.text_save),
                onClick = { viewModel.submitQrDocument() },
            )
        }
    }

    @Composable
    override fun SuccessStateCallback(data: Any?) {
        val context = LocalContext.current

        when (data) {
            is UIState.NavToItemsScreen -> {
                (context as ComponentActivity).finish()
            }
        }
    }

    @Composable
    override fun ScreenContent() {
        val actualQtyState = viewModel.inputActualQtyState.collectAsState()

        ShipmentDocumentItemDetailContainer()

        ActualQtyCounterContainer(
            actualQty = actualQtyState.value,
            onActualQtyChange = {
                viewModel.setInputActualQtyState(it)
            },
        )
    }

    @Composable
    private fun ShipmentDocumentItemDetailContainer() {
        val shipmentDocumentItemDetail = viewModel.shipmentDocumentItemDetail.collectAsState()

        TachyonText(
            text = stringResource(id = R.string.text_product_info),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )

        TachyonLazyColumnItem(
            canClick = false,
        ) {
            Column(
                modifier =
                    Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    TachyonText(
                        text = shipmentDocumentItemDetail.value.barcode,
                        style = TachyonDictionary.body._1,
                        color = TachyonDictionary.content._default,
                    )

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._2xs),
                    ) {
                        TachyonText(
                            text = "${shipmentDocumentItemDetail.value.actualQty}",
                            style = TachyonDictionary.body._1,
                            color = TachyonDictionary.content._default,
                        )

                        TachyonText(
                            text = shipmentDocumentItemDetail.value.unitName,
                            style = TachyonDictionary.body._2,
                            color = TachyonDictionary.content._default,
                        )
                    }
                }

                TachyonText(
                    text = shipmentDocumentItemDetail.value.productName,
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
        actualQty: Int,
        onActualQtyChange: (Int) -> Unit,
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
                initialNumber = actualQty,
                onIncrement = {
                    onActualQtyChange(it)
                },
                onDecrement = {
                    onActualQtyChange(it)
                },
            )
        }
    }
}
