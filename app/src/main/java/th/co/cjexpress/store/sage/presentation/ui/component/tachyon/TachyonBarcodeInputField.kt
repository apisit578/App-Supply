package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

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
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.constants.PromotionType
import th.co.cjexpress.store.sage.presentation.model.component.TachyonBarcodeInputFieldUIModel
import th.co.cjexpress.store.sage.presentation.state.base.BaseScreenState
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.component.TachyonBarcodeInputFieldViewModel
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce
import th.co.cjexpress.store.sage.utils.safeKeyboardActionWithDebounce

enum class DecodeType {
    NONE,
    BARCODE_CODEC,
}

@Composable
fun TachyonBarcodeInputField(
    viewModel: TachyonBarcodeInputFieldViewModel = hiltViewModel(),
    label: String,
    placeholder: String,
    decodeType: DecodeType,
    acceptedPrefix: List<PromotionType>? = null,
    onValidateBarcode: (TachyonBarcodeInputFieldUIModel) -> Unit,
) {
    val baseScreenState by viewModel.baseScreenState.collectAsState()
    val inputBarcode = viewModel.inputBarcodeState.collectAsState()
    val softKeyboardController = LocalSoftwareKeyboardController.current
    val focusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(DELAY_DEFAULT_TIME)
        focusRequester.requestFocus()
        softKeyboardController?.hide()
    }

    when (val state = baseScreenState) {
        is BaseScreenState.Loading -> TachyonCircularProgressIndicatorDialog()
        is BaseScreenState.Error -> {
            TachyonAlertDialog(dialogData = state.errorData)
        }

        is BaseScreenState.Success<*> -> {
            viewModel.resetBaseScreenState()
            onValidateBarcode(state.data as TachyonBarcodeInputFieldUIModel)
        }

        else -> {}
    }

    TachyonOutlinedTextField(
        modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(focusRequester),
        keyboardActions =
            KeyboardActions(
                onDone =
                    safeKeyboardActionWithDebounce {
                        val barcode = inputBarcode.value.text
                        viewModel.clearInputBarcodeState()

                        when (decodeType) {
                            DecodeType.NONE -> {
                                viewModel.setNoneResult(barcode)
                            }

                            DecodeType.BARCODE_CODEC -> {
                                viewModel.decodeBarcode(barcode, acceptedPrefix)
                            }
                        }
                    },
            ),
        value = inputBarcode.value,
        onValueChange = { viewModel.setInputBarcodeState(it) },
        label = {
            TachyonText(
                text = label,
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._description,
            )
        },
        placeholder = {
            TachyonText(
                text = placeholder,
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._placeholder,
            )
        },
        trailingIcon = {
            if (inputBarcode.value.text.isNotEmpty()) {
                IconButton(
                    onClick = safeClickWithDebounce { viewModel.clearInputBarcodeState() },
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(R.string.text_clear),
                    )
                }
            }
        },
    )
}
