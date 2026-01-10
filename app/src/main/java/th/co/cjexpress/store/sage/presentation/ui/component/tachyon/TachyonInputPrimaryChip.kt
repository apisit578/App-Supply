package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Icon
import androidx.compose.material3.InputChip
import androidx.compose.material3.InputChipDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonInputPrimaryChip(
    leadingIcon: ImageVector? = null,
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
        },
        label = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                TachyonText(
                    text = chipText,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._inversed,
                )
            }
        },
        colors =
            InputChipDefaults.inputChipColors(
                disabledContainerColor = TachyonDictionary.content.success._default,
            ),
        border =
            InputChipDefaults.inputChipBorder(
                selected = false,
                enabled = false,
                disabledBorderColor = TachyonDictionary.content.success._default,
            ),
    )
}
