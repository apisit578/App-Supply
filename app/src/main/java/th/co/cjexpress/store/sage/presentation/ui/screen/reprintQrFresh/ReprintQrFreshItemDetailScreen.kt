package th.co.cjexpress.store.sage.presentation.ui.screen.reprintQrFresh

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.hilt.navigation.compose.hiltViewModel
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonDateInputField
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonNumberCounter
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonListItemColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemDetailViewModel
import th.co.cjexpress.store.sage.presentation.viewmodel.reprintQrFresh.ReprintQrFreshItemDetailViewModel.UIState

@Composable
fun ReprintQrFreshItemDetailScreen(viewModel: ReprintQrFreshItemDetailViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()

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
                is UIState.NavToItemsScreen -> {
                    (context as ComponentActivity).finish()
                }
                else -> {}
            }
        }

        else -> {}
    }

    ReprintQrFreshItemDetailContainer(viewModel)
}

@Composable
private fun ReprintQrFreshItemDetailContainer(viewModel: ReprintQrFreshItemDetailViewModel) {
    val context = LocalContext.current
    val productQrFreshItem by viewModel.productQrFreshItem.collectAsState()
    val canEditState by viewModel.canEditState.collectAsState()
    val actualQtyState by viewModel.inputActualQtyState.collectAsState()
    val dateInputLD by viewModel.dateInputLD.collectAsState()

    Scaffold(
        topBar = {
            TachyonTopAppBar(
                titleText = stringResource(id = R.string.text_detail),
                onNavigationIconClick = {
                    viewModel.promptBeforeBack()
                },
            )
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
            ) {
                BarcodeDetailsItemContainer(
                    barcode = productQrFreshItem?.barcode.orEmpty(),
                    productName = productQrFreshItem?.skuName.orEmpty(),
                )

                TachyonDateInputField(
                    label = stringResource(id = R.string.text_title_input_date),
                    enabled = canEditState,
                    initialDate = viewModel.getDefaultInitialDate(),
                ) { result ->
                    viewModel.setDateInputResult(result)
                }

                ActualQtyCounterContainer(
                    actualQty = actualQtyState,
                    onActualQtyChange = { qty ->
                        viewModel.inputActualQtyState.value = qty
                    },
                )
            }
            BottomButtonContainer(
                onSubmitClick = {
                    viewModel.submitItemDetail(
                        actualQty = actualQtyState,
                    )
                },
            )
        }
    }
}

@Composable
private fun BarcodeDetailsItemContainer(
    barcode: String,
    productName: String,
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
    ) {
        TachyonText(
            text = stringResource(id = R.string.text_product_info),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )

        TachyonListItemColumn(
            canClick = false,
        ) {
            TachyonText(
                text = barcode,
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._default,
            )
            TachyonText(
                text = productName,
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
                .padding(TachyonDictionary.space.padding._md),
    ) {
        TachyonText(
            text = stringResource(id = R.string.text_title_input_qr_qty),
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

@Composable
private fun BottomButtonContainer(onSubmitClick: () -> Unit) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TachyonSolidButton(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f),
            style = TachyonSolidButtonStyle.GENERAL,
            buttonText = stringResource(id = R.string.common_save),
            onClick = {
                onSubmitClick()
            },
        )
    }
}
