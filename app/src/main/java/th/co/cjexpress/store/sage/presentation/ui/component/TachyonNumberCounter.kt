package th.co.cjexpress.store.sage.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedTextField
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun TachyonNumberCounter(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    initialNumber: Int,
    minNumber: Int = 0,
    maxNumber: Int? = null,
    onIncrement: (Int) -> Unit = {},
    onDecrement: (Int) -> Unit = {},
) {
    var inputText by remember(initialNumber) {
        mutableStateOf(
            TextFieldValue(
                text = initialNumber.toString(),
                selection = TextRange(initialNumber.toString().length),
            ),
        )
    }

    val focusManager = LocalFocusManager.current

    key(enabled) {
        Row(
            modifier =
                Modifier
                    .height(48.dp)
                    .focusable()
                    .padding(top = 4.dp, bottom = 4.dp)
                    .background(
                        color = TachyonDictionary.content._inversed,
                        shape = RoundedCornerShape(4.dp),
                    ).border(
                        width = 1.dp,
                        color = TachyonDictionary.border._default,
                        shape = RoundedCornerShape(4.dp),
                    ).then(modifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(
                modifier = Modifier.focusable(false),
                enabled = enabled,
                onClick =
                    safeClickWithDebounce {
                        if (initialNumber > minNumber) {
                            onDecrement(initialNumber - 1)
                        }
                    },
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp, 24.dp)
                            .background(
                                color =
                                    if (enabled) {
                                        TachyonDictionary.surface.action.delete._default
                                    } else {
                                        TachyonDictionary.surface.action.delete._disabled
                                    },
                                shape = CircleShape,
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icons_minus),
                        contentDescription = "ลบ",
                        tint = if (enabled) TachyonDictionary.content._inversed else TachyonDictionary.content._disabled,
                    )
                }
            }

            TachyonOutlinedTextField(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = TachyonDictionary.global.spacing._xs),
                overrideHeight = Modifier.height(48.dp),
                value = inputText,
                onValueChange = { newTextFieldValue ->
                    convertInputValue(
                        initialNumber = initialNumber,
                        newText = newTextFieldValue.text,
                        minNumber = minNumber,
                        maxNumber = maxNumber,
                        onIncrement = {
                            inputText =
                                TextFieldValue(
                                    text = it.toString(),
                                    selection = TextRange(it.toString().length),
                                )
                            onIncrement(it)
                        },
                        onDecrement = {
                            inputText =
                                TextFieldValue(
                                    text = it.toString(),
                                    selection = TextRange(it.toString().length),
                                )
                            onDecrement(it)
                        },
                    )
                },
                enabled = enabled,
                readOnly = readOnly,
                singleLine = true,
                textAlign = TextAlign.Center,
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number),
                keyboardActions =
                    KeyboardActions(
                        onDone = { focusManager.clearFocus() },
                    ),
                focusedBorderColor = TachyonDictionary.border._inversed,
                unfocusedBorderColor = TachyonDictionary.border._inversed,
                disabledBorderColor = TachyonDictionary.border._inversed,
            )

            IconButton(
                modifier = Modifier.focusable(false),
                enabled = enabled,
                onClick =
                    safeClickWithDebounce {
                        if (initialNumber < (maxNumber ?: Int.MAX_VALUE)) {
                            onIncrement(initialNumber + 1)
                        }
                    },
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(24.dp, 24.dp)
                            .background(
                                color =
                                    if (enabled) {
                                        TachyonDictionary.surface.action.primary._default
                                    } else {
                                        TachyonDictionary.surface.action.primary._disabled
                                    },
                                shape = CircleShape,
                            ),
                    contentAlignment = Alignment.Center,
                ) {
                    Icon(
                        painter = painterResource(R.drawable.icons_plus),
                        contentDescription = "เพิ่ม",
                        tint = if (enabled) TachyonDictionary.content._inversed else TachyonDictionary.content._disabled,
                    )
                }
            }
        }
    }
}

fun convertInputValue(
    initialNumber: Int,
    newText: String,
    minNumber: Int,
    maxNumber: Int?,
    onIncrement: (Int) -> Unit,
    onDecrement: (Int) -> Unit,
) {
    val result = newText.filter { it.isDigit() }.trimStart('0').ifEmpty { "0" }
    val newValue = result.toIntOrNull() ?: minNumber

    val value =
        when {
            newValue < minNumber -> minNumber
            maxNumber != null && newValue > maxNumber -> maxNumber
            else -> newValue
        }

    if (value > initialNumber) {
        onIncrement(value)
    } else if (value < initialNumber) {
        onDecrement(value)
    }
}
