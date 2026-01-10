package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun TachyonButtonPrimaryOutlined(
    modifier: Modifier,
    buttonText: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    OutlinedButton(
        modifier = modifier,
        enabled = enabled,
        onClick = safeClickWithDebounce { onClick() },
        colors =
            ButtonDefaults.outlinedButtonColors(
                containerColor = Color.Transparent,
                contentColor = TachyonDictionary.content.primary._default,
                disabledContentColor = TachyonDictionary.content._disabled,
            ),
        border =
            BorderStroke(
                width = 1.dp,
                color = if (enabled) TachyonDictionary.border.primary._default else TachyonDictionary.border._default,
            ),
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        TachyonText(
            text = buttonText,
            style = TachyonDictionary.body._1,
        )
    }
}
