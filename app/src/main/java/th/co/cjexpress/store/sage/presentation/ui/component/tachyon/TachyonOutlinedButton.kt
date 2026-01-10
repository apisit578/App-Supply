package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

enum class TachyonOutlinedButtonStyle {
    GENERAL,
    NEGATIVITY,
}

@Composable
fun TachyonOutlinedButton(
    modifier: Modifier = Modifier,
    style: TachyonOutlinedButtonStyle,
    buttonText: String,
    enabled: Boolean = true,
    showProgressIndicator: Boolean = false,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        onClick = safeClickWithDebounce { onClick() },
        colors =
            when (style) {
                TachyonOutlinedButtonStyle.GENERAL ->
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = TachyonDictionary.content.primary._default,
                        disabledContentColor = TachyonDictionary.content._disabled,
                    )

                TachyonOutlinedButtonStyle.NEGATIVITY ->
                    ButtonDefaults.outlinedButtonColors(
                        containerColor = Color.Transparent,
                        contentColor = TachyonDictionary.content._inversed,
                        disabledContentColor = TachyonDictionary.content._disabled,
                    )
            },
        border =
            BorderStroke(
                width = 1.dp,
                color =
                    when (style) {
                        TachyonOutlinedButtonStyle.GENERAL -> {
                            if (enabled) {
                                TachyonDictionary.border.primary._default
                            } else {
                                TachyonDictionary.border._disabled
                            }
                        }

                        TachyonOutlinedButtonStyle.NEGATIVITY -> {
                            if (enabled) {
                                TachyonDictionary.border.danger._default
                            } else {
                                TachyonDictionary.border._disabled
                            }
                        }
                    },
            ),
        contentPadding =
            PaddingValues(
                start = TachyonDictionary.space.padding._xl,
                top = TachyonDictionary.space.padding._xs,
                end = TachyonDictionary.space.padding._xl,
                bottom = TachyonDictionary.space.padding._xs,
            ),
    ) {
        if (showProgressIndicator) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = TachyonDictionary.content.primary._default,
            )
        } else {
            TachyonText(
                text = buttonText,
                style = TachyonDictionary.body._1,
            )
        }
    }
}
