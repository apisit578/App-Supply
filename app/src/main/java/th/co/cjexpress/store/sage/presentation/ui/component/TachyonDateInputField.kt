package th.co.cjexpress.store.sage.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEvent
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.presentation.model.component.TachyonDateInputFieldResultModel
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedTextField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.component.TachyonDateInputFieldViewModel
import th.co.cjexpress.store.sage.utils.safeKeyboardActionWithDebounce

@Composable
fun TachyonDateInputField(
    viewModel: TachyonDateInputFieldViewModel = hiltViewModel(),
    label: String,
    initialDate: String = "",
    enabled: Boolean = true,
    onDateChanged: (TachyonDateInputFieldResultModel) -> Unit,
) {
    val softKeyboardController = LocalSoftwareKeyboardController.current
    val dayInputState by viewModel.dayInputState.collectAsState()
    val monthInputState by viewModel.monthInputState.collectAsState()
    val yearInputState by viewModel.yearInputState.collectAsState()

    LaunchedEffect(Unit) {
        viewModel.setDateCallback(onDateChanged)
        viewModel.setInitialDate(initialDate)
    }

    LaunchedEffect(initialDate) {
        val currentFormattedDate =
            createFormattedDate(dayInputState.text, monthInputState.text, yearInputState.text)
        if (initialDate != currentFormattedDate) {
            viewModel.setInitialDate(initialDate)
        } else {
            viewModel.returnAsInvalidateFormat()
        }
    }

    LaunchedEffect(Unit) {
        delay(DELAY_DEFAULT_TIME)
        viewModel.dayFocusRequester.requestFocus()
        softKeyboardController?.hide()
    }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TachyonDictionary.surface.static._default2),
    ) {
        Column(
            modifier = Modifier.padding(TachyonDictionary.space.padding._md),
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
        ) {
            TachyonText(
                text = label,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )

            Row(
                modifier = Modifier.wrapContentHeight(),
                horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
            ) {
                DateTextField(
                    overrideWidth = Modifier.width((112 / LocalDensity.current.density).dp),
                    focusRequester = viewModel.dayFocusRequester,
                    value = dayInputState,
                    onValueChange = viewModel::handleDayInput,
                    onKeyEvent = createDayKeyHandler(viewModel),
                    onNext = viewModel::handleDayNext,
                    placeholderRes = R.string.common_days,
                    enabled = enabled,
                )

                DateTextField(
                    overrideWidth = Modifier.width((112 / LocalDensity.current.density).dp),
                    focusRequester = viewModel.monthFocusRequester,
                    value = monthInputState,
                    onValueChange = {
                        createMonthValueChangeHandler(
                            viewModel,
                            it,
                            monthInputState,
                        )
                    },
                    onKeyEvent = createMonthKeyHandler(viewModel, monthInputState.text),
                    onNext = viewModel::handleMonthNext,
                    placeholderRes = R.string.common_months,
                    enabled = enabled,
                )

                DateTextField(
                    focusRequester = viewModel.yearFocusRequester,
                    value = yearInputState,
                    onValueChange = {
                        createYearValueChangeHandler(
                            viewModel,
                            it,
                            yearInputState,
                        )
                    },
                    onKeyEvent =
                        createYearKeyHandler(
                            viewModel,
                            yearInputState.text,
                            softKeyboardController,
                        ),
                    onNext = { softKeyboardController?.hide() },
                    placeholderRes = R.string.common_years,
                    imeAction = ImeAction.Done,
                    enabled = enabled,
                )
            }
        }
    }
}

@Composable
private fun DateTextField(
    focusRequester: FocusRequester,
    overrideWidth: Modifier? = null,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    onKeyEvent: (KeyEvent) -> Boolean,
    onNext: () -> Unit,
    placeholderRes: Int,
    enabled: Boolean,
    imeAction: ImeAction = ImeAction.Next,
) {
    TachyonOutlinedTextField(
        modifier =
            Modifier
                .focusRequester(focusRequester)
                .onKeyEvent(onKeyEvent),
        overrideWidth = overrideWidth,
        overrideHeight = Modifier.height(40.dp),
        enabled = enabled,
        textAlign = TextAlign.Center,
        placeholderTextAlignment = Alignment.Center,
        value = value,
        onValueChange = onValueChange,
        placeholder = {
            TachyonText(
                text = stringResource(id = placeholderRes),
                style = TachyonDictionary.body._1,
                color = TachyonDictionary.content._placeholder,
            )
        },
        keyboardOptions =
            KeyboardOptions.Default.copy(
                imeAction = imeAction,
                keyboardType = KeyboardType.Number,
            ),
        keyboardActions =
            KeyboardActions(
                onNext = safeKeyboardActionWithDebounce { onNext.invoke() },
                onDone = safeKeyboardActionWithDebounce { onNext.invoke() },
            ),
    )
}

private fun createFormattedDate(
    day: String,
    month: String,
    year: String,
): String =
    if (day.isNotEmpty() && month.isNotEmpty() && year.isNotEmpty()) {
        String.format(
            "%02d/%02d/%04d",
            day.toIntOrNull() ?: 0,
            month.toIntOrNull() ?: 0,
            year.toIntOrNull() ?: 0,
        )
    } else {
        ""
    }

private fun createDayKeyHandler(viewModel: TachyonDateInputFieldViewModel): (KeyEvent) -> Boolean =
    { keyEvent ->
        handleKeyEvent(keyEvent) {
            when (keyEvent.key) {
                Key.Enter -> {
                    viewModel.handleDayNext()
                    true
                }

                else -> false
            }
        }
    }

private fun createMonthKeyHandler(
    viewModel: TachyonDateInputFieldViewModel,
    monthInputState: String,
): (KeyEvent) -> Boolean =
    { keyEvent ->
        handleKeyEvent(keyEvent) {
            when (keyEvent.key) {
                Key.Enter -> {
                    viewModel.handleMonthNext()
                    true
                }

                Key.Backspace -> {
                    if (monthInputState.isEmpty()) {
                        viewModel.handleBackspaceInMonth()
                        true
                    } else {
                        false
                    }
                }

                else -> false
            }
        }
    }

private fun createYearKeyHandler(
    viewModel: TachyonDateInputFieldViewModel,
    yearInputState: String,
    softKeyboardController: SoftwareKeyboardController?,
): (KeyEvent) -> Boolean =
    { keyEvent ->
        handleKeyEvent(keyEvent) {
            when (keyEvent.key) {
                Key.Backspace -> {
                    if (yearInputState.isEmpty()) {
                        viewModel.handleBackspaceInYear()
                        true
                    } else {
                        false
                    }
                }

                Key.Enter -> {
                    if (yearInputState.length == 4) {
                        softKeyboardController?.hide()
                        true
                    } else {
                        false
                    }
                }

                else -> false
            }
        }
    }

private fun createMonthValueChangeHandler(
    viewModel: TachyonDateInputFieldViewModel,
    newValue: TextFieldValue,
    currentValue: TextFieldValue,
) {
    when {
        newValue.text.isEmpty() && currentValue.text.isNotEmpty() ->
            viewModel.handleMonthInput(
                newValue,
            )

        newValue.text.isEmpty() && currentValue.text.isEmpty() -> viewModel.handleBackspaceInMonth()
        else -> viewModel.handleMonthInput(newValue)
    }
}

private fun createYearValueChangeHandler(
    viewModel: TachyonDateInputFieldViewModel,
    newValue: TextFieldValue,
    currentValue: TextFieldValue,
) {
    when {
        newValue.text.isEmpty() && currentValue.text.isNotEmpty() -> {
            viewModel.handleYearInput(newValue)
        }

        newValue.text.isEmpty() && currentValue.text.isEmpty() -> viewModel.handleBackspaceInYear()
        else -> viewModel.handleYearInput(newValue)
    }
}

private fun handleKeyEvent(
    keyEvent: KeyEvent,
    handler: () -> Boolean,
): Boolean = if (keyEvent.type == KeyEventType.KeyUp) handler() else false
