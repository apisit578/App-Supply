package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun TachyonButtonPrimaryFilled(
    modifier: Modifier,
    buttonText: String,
    showProgressIndicator: Boolean = false,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    Button(
        modifier = modifier,
        enabled = enabled,
        onClick = safeClickWithDebounce { onClick() },
        colors =
            ButtonDefaults.buttonColors(
                containerColor = TachyonDictionary.surface.action.primary._default,
                contentColor = TachyonDictionary.content._inversed,
                disabledContainerColor = TachyonDictionary.surface.action.primary._disabled,
                disabledContentColor = TachyonDictionary.content._disabled,
            ),
        contentPadding = PaddingValues(horizontal = 0.dp),
    ) {
        if (showProgressIndicator) {
            CircularProgressIndicator(
                modifier = Modifier.size(16.dp),
                color = TachyonDictionary.content.primary._default,
            )
        } else {
            TachyonText(
                text = buttonText,
                style = TachyonDictionary.headLine._5,
            )
        }
    }
}
