package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonTextFieldOutlined(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
        ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    interactionSource: MutableInteractionSource? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    unfocusedBorderColor: Color = TachyonDictionary.border._default,
    focusedBorderColor: Color = TachyonDictionary.border._focus,
    errorBorderColor: Color = TachyonDictionary.content.danger._default,
    disabledTextColor: Color = TachyonDictionary.content._disabled,
    disabledLabelColor: Color = TachyonDictionary.content._disabled,
    disabledBorderColor: Color = TachyonDictionary.content._disabled,
    textAlign: TextAlign = TextAlign.Start,
) {
    OutlinedTextField(
        modifier =
            Modifier
                .defaultMinSize(minHeight = 1.dp)
                .clearAndSetSemantics { }
                .then(modifier),
        value = value,
        onValueChange = { onValueChange(it) },
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        visualTransformation = visualTransformation,
        colors =
            OutlinedTextFieldDefaults.colors(
                cursorColor = TachyonDictionary.content._description,
                unfocusedBorderColor = unfocusedBorderColor,
                focusedBorderColor = focusedBorderColor,
                errorBorderColor = errorBorderColor,
//            errorSupportingTextColor = TachyonDictionary.content.danger._default,
                disabledTextColor = disabledTextColor,
                disabledLabelColor = disabledLabelColor,
                disabledBorderColor = disabledBorderColor,
            ),
        textStyle = TachyonDictionary.body._1.copy(textAlign = textAlign),
    )
}

@Composable
fun TachyonTextFieldOutlined(
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    keyboardOptions: KeyboardOptions =
        KeyboardOptions.Default.copy(
            imeAction = ImeAction.Done,
        ),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    interactionSource: MutableInteractionSource? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    unfocusedBorderColor: Color = TachyonDictionary.border._default,
    focusedBorderColor: Color = TachyonDictionary.border._focus,
    errorBorderColor: Color = TachyonDictionary.content.danger._default,
    disabledTextColor: Color = TachyonDictionary.content._disabled,
    disabledLabelColor: Color = TachyonDictionary.content._disabled,
    disabledBorderColor: Color = TachyonDictionary.content._disabled,
    textAlign: TextAlign = TextAlign.Start,
) {
    OutlinedTextField(
        modifier =
            Modifier
                .defaultMinSize(minHeight = 1.dp)
                .then(modifier),
        value = value,
        onValueChange = { onValueChange(it) },
        enabled = enabled,
        readOnly = readOnly,
        label = label,
        placeholder = placeholder,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        supportingText = supportingText,
        isError = isError,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        interactionSource = interactionSource,
        visualTransformation = visualTransformation,
        colors =
            OutlinedTextFieldDefaults.colors(
                cursorColor = TachyonDictionary.content._description,
                unfocusedBorderColor = unfocusedBorderColor,
                focusedBorderColor = focusedBorderColor,
                errorBorderColor = errorBorderColor,
//            errorSupportingTextColor = TachyonDictionary.content.danger._default,
                disabledTextColor = disabledTextColor,
                disabledLabelColor = disabledLabelColor,
                disabledBorderColor = disabledBorderColor,
            ),
        textStyle = TachyonDictionary.body._1.copy(textAlign = textAlign),
    )
}
