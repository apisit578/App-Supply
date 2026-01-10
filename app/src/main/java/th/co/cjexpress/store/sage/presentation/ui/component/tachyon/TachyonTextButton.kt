package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun TachyonTextButton(
    modifier: Modifier,
    leadingIcon: ImageVector? = null,
    buttonText: String,
    enabled: Boolean = true,
    onClick: () -> Unit,
) {
    TextButton(
        modifier =
            Modifier
                .defaultMinSize(1.dp, 1.dp)
                .then(modifier),
        enabled = enabled,
        contentPadding = PaddingValues(0.dp),
        onClick = safeClickWithDebounce { onClick() },
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (leadingIcon != null) {
                Icon(
                    modifier =
                        Modifier
                            .width((18 / LocalDensity.current.density).dp)
                            .height((18 / LocalDensity.current.density).dp),
                    imageVector = Icons.Outlined.Add,
                    tint =
                        if (enabled) {
                            TachyonDictionary.content.primary._default
                        } else {
                            TachyonDictionary.content._disabled
                        },
                    contentDescription = buttonText,
                )
            }

            TachyonText(
                text = buttonText,
                style = TachyonDictionary.headLine._5,
                color =
                    if (enabled) {
                        TachyonDictionary.content.primary._default
                    } else {
                        TachyonDictionary.content._disabled
                    },
            )
        }
    }
}
