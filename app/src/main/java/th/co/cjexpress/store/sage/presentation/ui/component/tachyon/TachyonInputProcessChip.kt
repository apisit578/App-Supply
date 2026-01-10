package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonInputProcessChip(
    leadingIcon: ImageVector? = null,
    painter: Painter? = null,
    chipText: String,
) {
    InputChip(
        modifier =
            Modifier
                .width((110 / LocalDensity.current.density).dp)
                .height((32 / LocalDensity.current.density).dp),
        enabled = false,
        selected = false,
        onClick = {},
        leadingIcon = {
            if (leadingIcon != null) {
                Icon(
                    modifier =
                        Modifier
                            .width((20 / LocalDensity.current.density).dp)
                            .height((20 / LocalDensity.current.density).dp),
                    tint = TachyonDictionary.content._inversed,
                    imageVector = leadingIcon,
                    contentDescription = chipText,
                )
            }
            if (painter != null) {
                Icon(
                    modifier =
                        Modifier
                            .width((20 / LocalDensity.current.density).dp)
                            .height((20 / LocalDensity.current.density).dp),
                    tint = TachyonDictionary.content._inversed,
                    painter = painter,
                    contentDescription = chipText,
                )
            }
        },
        label = {
            TachyonText(
                text = chipText,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._inversed,
            )
        },
        colors =
            InputChipDefaults.inputChipColors(
                disabledContainerColor = TachyonDictionary.content.process._default,
            ),
        border =
            InputChipDefaults.inputChipBorder(
                selected = false,
                enabled = false,
                disabledBorderColor = TachyonDictionary.content.process._default,
            ),
    )
}
