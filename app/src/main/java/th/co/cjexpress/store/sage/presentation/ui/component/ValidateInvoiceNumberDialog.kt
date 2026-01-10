package th.co.cjexpress.store.sage.presentation.ui.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonCircularProgressIndicatorDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonSolidButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonTextFieldOutlined
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.component.ValidateInvoiceNumberDialogViewModel
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce
import th.co.cjexpress.store.sage.utils.safeKeyboardActionWithDebounce

@Composable
fun ValidateInvoiceNumberDialog(
    viewModel: ValidateInvoiceNumberDialogViewModel = hiltViewModel(),
    defaultValue: String,
    piNo: String,
    supplierCode: String,
    onDismiss: () -> Unit,
    isValidCallback: (String) -> Unit,
) {
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val showState by viewModel.showDialogState.collectAsState()

    LaunchedEffect(showState) {
        viewModel.setInvoiceNumberInputState(TextFieldValue(text = defaultValue))
        viewModel.setDialogShowState(true)
    }

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            TachyonAlertDialog(dialogData = state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            viewModel.resetBaseScreenState()
            isValidCallback(state.data as String)
        }

        else -> {}
    }

    if (showState) {
        InvoiceNumberInputScreen(
            viewModel = viewModel,
            onValueChange = { value ->
                viewModel.setInvoiceNumberInputState(value)
            },
            onConfirm = { billNo ->
                viewModel.validatePurchaseInvoiceBill(billNo, piNo, supplierCode)
            },
            onDismiss = {
                viewModel.onDismiss()
                onDismiss()
            },
        )
    }
}

@Composable
private fun InvoiceNumberInputScreen(
    viewModel: ValidateInvoiceNumberDialogViewModel,
    onValueChange: (TextFieldValue) -> Unit,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    val placeHolderState by viewModel.placeHolderState.collectAsState()
    val invoiceNumberInputState by viewModel.invoiceNumberInputState.collectAsState()
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(DELAY_DEFAULT_TIME)
        focusRequester.requestFocus()
    }

    AlertDialog(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(TachyonDictionary.global.spacing._md),
        properties =
            DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
        containerColor = TachyonDictionary.surface.static._default1,
        onDismissRequest = {
            viewModel.clearInvoiceNumberInputState()
            onDismiss()
        },
        title = {
            TachyonText(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.text_title_please_input_or_scan_invoice_no),
                textAlign = TextAlign.Center,
                style = TachyonDictionary.headLine._2,
                color = TachyonDictionary.content._default,
            )
        },
        text = {
            TachyonTextFieldOutlined(
                modifier =
                    Modifier
                        .wrapContentHeight()
                        .fillMaxWidth()
                        .focusRequester(focusRequester),
                value = invoiceNumberInputState,
                onValueChange = {
                    onValueChange(it)
                },
                placeholder = {
                    TachyonText(
                        text = placeHolderState,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        style = TachyonDictionary.body._1,
                        color = TachyonDictionary.content._description,
                    )
                },
                trailingIcon =
                    if (invoiceNumberInputState.text.isNotEmpty()) {
                        {
                            IconButton(onClick = safeClickWithDebounce { viewModel.clearInvoiceNumberInputState() }) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "ล้างค่า",
                                )
                            }
                        }
                    } else {
                        null
                    },
                keyboardActions =
                    KeyboardActions(
                        onDone =
                            safeKeyboardActionWithDebounce {
                                onConfirm(invoiceNumberInputState.text)
                            },
                    ),
            )
        },
        confirmButton = {
            TachyonSolidButton(
                buttonText = stringResource(R.string.text_ok),
                style = TachyonSolidButtonStyle.GENERAL,
                onClick =
                    safeClickWithDebounce {
                        onConfirm(invoiceNumberInputState.text)
                    },
            )
        },
        dismissButton = {
            TachyonOutlinedButton(
                buttonText = stringResource(R.string.text_cancel),
                style = TachyonOutlinedButtonStyle.GENERAL,
                onClick =
                    safeClickWithDebounce {
                        viewModel.clearInvoiceNumberInputState()
                        onDismiss()
                    },
            )
        },
    )
}
