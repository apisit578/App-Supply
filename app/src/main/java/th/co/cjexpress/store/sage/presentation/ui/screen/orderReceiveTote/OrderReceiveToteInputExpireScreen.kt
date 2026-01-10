package th.co.cjexpress.store.sage.presentation.ui.screen.orderReceiveTote

import androidx.activity.ComponentActivity
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.state.orderReceiveTote.SdToteInputExpiryDateSubmitState
import th.co.cjexpress.store.sage.presentation.ui.component.TachyonDateCounter
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonBottomAppBar
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCheckBox
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonScreenContentColumn
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTopAppBar
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.orderReceiveTote.OrderReceiveToteInputExpireViewModel
import timber.log.Timber
import java.util.Date

@Composable
fun OrderReceiveToteInputExpireScreen(viewModel: OrderReceiveToteInputExpireViewModel = hiltViewModel()) {
    val context = LocalContext.current
    val baseScreenState by viewModel.baseScreenState.collectAsState()

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            viewModel.logEventAlertDialog(state.errorData.messageText)
            TachyonAlertDialog(state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            when (state.data) {
                is SdToteInputExpiryDateSubmitState -> {
                    if (state.data.data?.isAddLot == false) {
                        viewModel.resetSubmitBarcodeLD()
                    }

                    viewModel.resetBaseScreenState()
                    (context as ComponentActivity).finish()
                }
            }
        }

        else -> {}
    }

    OrderReceiveToteInputExpireScreenContainer(viewModel)
}

@Composable
private fun OrderReceiveToteInputExpireScreenContainer(viewModel: OrderReceiveToteInputExpireViewModel) {
    val context = LocalContext.current
    val inputExpiryDateState by viewModel.inputExpiryDateState.collectAsState()
    val submitBarcodeLDState by viewModel.submitBarcodeFlowLD.collectAsState(null)
    val isNoQrCheckBoxState = remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.logEventScreenView()
        viewModel.logEventDefaultExpiryDate(
            barcode = submitBarcodeLDState?.barcode.orEmpty(),
            expiryDate = inputExpiryDateState.toString(),
        )
    }
    Timber
        .tag("STATE")
        .i("OrderReceiveToteInputExpireScreenContainer composing $inputExpiryDateState, $submitBarcodeLDState")

    Scaffold(
        topBar = {
            TopAppBarContainer(
                onNavigationClick = {
                    viewModel.logEventOnBackPressed()
                    if (submitBarcodeLDState?.isAddLot == false) {
                        viewModel.resetSubmitBarcodeLD()
                    }
                    (context as ComponentActivity).finish()
                },
            )
        },
        bottomBar = {
            TachyonBottomAppBar {
                BottomButtonContainer(
                    onCancelClick = {
                        viewModel.logEventOnBackPressed()
                        if (submitBarcodeLDState?.isAddLot == false) {
                            viewModel.resetSubmitBarcodeLD()
                        }
                        (context as ComponentActivity).finish()
                    },
                    onSubmit = {
                        submitBarcodeLDState?.let {
                            if (it.isAddLot) {
                                viewModel.addNewLotOrch(
                                    isNoQrCheckBoxState.value,
                                    inputExpiryDateState,
                                )
                                (context as ComponentActivity).finish()
                            } else {
                                viewModel.submitDummyLotExpiryDateOrch()
                            }
                        }
                    },
                )
            }
        },
    ) { innerPadding ->
        TachyonScreenContentColumn(scaffoldInnerPadding = innerPadding) {
            InputExpireContainer(
                modifier =
                    Modifier
                        .weight(1f),
                expiryDate = inputExpiryDateState,
                isAddLot = submitBarcodeLDState?.isAddLot == true,
                onDateSelected = {
                    viewModel.setInputExpiryDateState(it)
                },
                onCheckChange = { checked ->
                    isNoQrCheckBoxState.value = checked
                },
            )
        }
    }
}

@Composable
private fun TopAppBarContainer(onNavigationClick: () -> Unit) {
    TachyonTopAppBar(
        titleText = stringResource(id = R.string.text_enter_expiry_date),
        onNavigationIconClick = onNavigationClick,
    )
}

@Composable
private fun InputExpireContainer(
    modifier: Modifier,
    expiryDate: Date,
    isAddLot: Boolean,
    onDateSelected: (Date) -> Unit,
    onCheckChange: (Boolean) -> Unit,
) {
    Timber.tag("STATE").i("InputExpireContainer expiryDate = $expiryDate")
    val isChecked = remember { mutableStateOf(true) }

    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .then(modifier),
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
    ) {
        TachyonText(
            text = stringResource(id = R.string.text_select_expiry_date),
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )

        Column(
            modifier =
                Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .fillMaxWidth()
                    .background(TachyonDictionary.surface.static._default2)
                    .padding(TachyonDictionary.space.padding._md),
        ) {
            TachyonDateCounter(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                defaultDate = expiryDate,
                onValueChange = {
                    Timber.tag("STATE").d("DateCounter on ValueChange = $it")
                    onDateSelected(it)
                },
            )
        }
        if (isAddLot) {
            TachyonCheckBox(
                text = stringResource(R.string.text_checkbox_qr_expire),
                checked = isChecked.value,
            ) {
                isChecked.value = it
                onCheckChange.invoke(isChecked.value)
            }
        }
    }
}

@Composable
private fun BottomButtonContainer(
    onCancelClick: () -> Unit,
    onSubmit: () -> Unit,
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
            style = TachyonSolidButtonStyle.GENERAL,
            buttonText = stringResource(id = R.string.text_ok),
            onClick = {
                onSubmit()
            },
        )
    }
}
