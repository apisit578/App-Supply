package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.OutlinedTextFieldDefaults.FocusedBorderThickness
import androidx.compose.material3.OutlinedTextFieldDefaults.UnfocusedBorderThickness
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachyonOutlinedTextField(
    modifier: Modifier = Modifier,
    overrideHeight: Modifier? = null,
    overrideWidth: Modifier? = null,
    value: String,
    onValueChange: (String) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textAlign: TextAlign = TextAlign.Start,
    placeholderTextAlignment: Alignment = Alignment.CenterStart,
    unfocusedBorderColor: Color = TachyonDictionary.border._default,
    focusedBorderColor: Color = TachyonDictionary.border._focus,
    errorBorderColor: Color = TachyonDictionary.border.danger._default,
    disabledTextColor: Color = TachyonDictionary.content._disabled,
    disabledBorderColor: Color = TachyonDictionary.border._disabled,
    focusedContainerColor: Color = TachyonDictionary.surface.static._default1,
    unfocusedContainerColor: Color = TachyonDictionary.surface.static._default1,
    disabledContainerColor: Color = TachyonDictionary.surface.static._default1,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val finalModifier =
        Modifier
            .defaultMinSize(minHeight = 1.dp)
            .then(modifier)
            .then(overrideHeight ?: Modifier.height(56.dp))
            .then(overrideWidth ?: Modifier.fillMaxWidth())

    val colors =
        OutlinedTextFieldDefaults.colors(
            cursorColor = TachyonDictionary.content._description,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedBorderColor = focusedBorderColor,
            errorBorderColor = errorBorderColor,
            disabledTextColor = disabledTextColor,
            disabledBorderColor = disabledBorderColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledContainerColor = disabledContainerColor,
        )

    val mutableInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by mutableInteractionSource.collectIsFocusedAsState()
    val shouldFloatLabel = isFocused || value.isNotEmpty()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = finalModifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle =
            TachyonDictionary.body._1.copy(
                textAlign = textAlign,
                color = if (enabled) TachyonDictionary.content._default else disabledTextColor,
            ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(colors.cursorColor),
        interactionSource = mutableInteractionSource,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder =
                    if (value.isEmpty()) {
                        placeholder?.let {
                            {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = placeholderTextAlignment,
                                ) {
                                    it.invoke()
                                }
                            }
                        }
                    } else {
                        null
                    },
                label =
                    if (!isFocused && value.isEmpty()) {
                        label
                    } else if (shouldFloatLabel) {
                        label
                    } else {
                        null
                    },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = mutableInteractionSource,
                colors = colors,
                contentPadding =
                    PaddingValues(
                        top = 0.dp,
                        bottom = 0.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = mutableInteractionSource,
                        colors = colors,
                        shape = OutlinedTextFieldDefaults.shape,
                        focusedBorderThickness = FocusedBorderThickness,
                        unfocusedBorderThickness = UnfocusedBorderThickness,
                    )
                },
            )
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachyonOutlinedTextField(
    modifier: Modifier = Modifier,
    overrideHeight: Modifier? = null,
    overrideWidth: Modifier? = null,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    label: @Composable (() -> Unit)? = null,
    placeholder: @Composable (() -> Unit)? = null,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    supportingText: @Composable (() -> Unit)? = null,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Done),
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    singleLine: Boolean = true,
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    textAlign: TextAlign = TextAlign.Start,
    placeholderTextAlignment: Alignment = Alignment.CenterStart,
    unfocusedBorderColor: Color = TachyonDictionary.border._default,
    focusedBorderColor: Color = TachyonDictionary.border._focus,
    errorBorderColor: Color = TachyonDictionary.border.danger._default,
    disabledTextColor: Color = TachyonDictionary.content._disabled,
    disabledBorderColor: Color = TachyonDictionary.border._disabled,
    focusedContainerColor: Color = TachyonDictionary.surface.static._default1,
    unfocusedContainerColor: Color = TachyonDictionary.surface.static._default1,
    isError: Boolean = false,
    interactionSource: MutableInteractionSource? = null,
) {
    val finalModifier =
        Modifier
            .defaultMinSize(minHeight = 1.dp)
            .then(modifier)
            .then(overrideHeight ?: Modifier.height(56.dp))
            .then(overrideWidth ?: Modifier.fillMaxWidth())

    val colors =
        OutlinedTextFieldDefaults.colors(
            cursorColor = TachyonDictionary.content._description,
            unfocusedBorderColor = unfocusedBorderColor,
            focusedBorderColor = focusedBorderColor,
            errorBorderColor = errorBorderColor,
            disabledTextColor = disabledTextColor,
            disabledBorderColor = disabledBorderColor,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
        )

    val mutableInteractionSource = interactionSource ?: remember { MutableInteractionSource() }
    val isFocused by mutableInteractionSource.collectIsFocusedAsState()
    val shouldFloatLabel = isFocused || value.text.isNotEmpty()

    BasicTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = finalModifier,
        enabled = enabled,
        readOnly = readOnly,
        textStyle =
            TachyonDictionary.body._1.copy(
                textAlign = textAlign,
                color = if (enabled) TachyonDictionary.content._default else disabledTextColor,
            ),
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        singleLine = singleLine,
        maxLines = maxLines,
        visualTransformation = visualTransformation,
        cursorBrush = SolidColor(colors.cursorColor),
        interactionSource = mutableInteractionSource,
        decorationBox = { innerTextField ->
            OutlinedTextFieldDefaults.DecorationBox(
                value = value.text,
                visualTransformation = visualTransformation,
                innerTextField = innerTextField,
                placeholder =
                    if (value.text.isEmpty()) {
                        placeholder?.let {
                            {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = placeholderTextAlignment,
                                ) {
                                    it.invoke()
                                }
                            }
                        }
                    } else {
                        null
                    },
                label =
                    if (!isFocused && value.text.isEmpty()) {
                        label
                    } else if (shouldFloatLabel) {
                        label
                    } else {
                        null
                    },
                leadingIcon = leadingIcon,
                trailingIcon = trailingIcon,
                supportingText = supportingText,
                singleLine = singleLine,
                enabled = enabled,
                isError = isError,
                interactionSource = mutableInteractionSource,
                colors = colors,
                contentPadding =
                    PaddingValues(
                        top = 0.dp,
                        bottom = 0.dp,
                        start = 16.dp,
                        end = 16.dp,
                    ),
                container = {
                    OutlinedTextFieldDefaults.Container(
                        enabled = enabled,
                        isError = isError,
                        interactionSource = mutableInteractionSource,
                        colors = colors,
                        shape = OutlinedTextFieldDefaults.shape,
                        focusedBorderThickness = FocusedBorderThickness,
                        unfocusedBorderThickness = UnfocusedBorderThickness,
                    )
                },
            )
        },
    )
}
