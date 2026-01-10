package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

enum class TachyonCustomChipType {
    SUCCESS,
    DANGER,
    WARNING,
    PROCESS,
}

@Composable
fun TachyonCustomChip(
    modifier: Modifier = Modifier,
    chipType: TachyonCustomChipType,
    chipText: String,
    painter: Painter? = null,
) {
    Surface(
        modifier =
            Modifier
                .width((110 / LocalDensity.current.density).dp)
                .height((32 / LocalDensity.current.density).dp)
                .then(modifier),
        shape = RoundedCornerShape(8.dp),
        color =
            when (chipType) {
                TachyonCustomChipType.SUCCESS -> TachyonDictionary.content.success._default
                TachyonCustomChipType.DANGER -> TachyonDictionary.content.danger._default
                TachyonCustomChipType.WARNING -> TachyonDictionary.content.warning._default
                TachyonCustomChipType.PROCESS -> TachyonDictionary.content.process._default
            },
        contentColor = TachyonDictionary.content._inversed,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        ) {
            if (painter != null) {
                Icon(
                    modifier =
                        Modifier
                            .width((20 / LocalDensity.current.density).dp)
                            .height((20 / LocalDensity.current.density).dp),
                    tint = TachyonDictionary.content._inversed,
                    painter = painter,
                    contentDescription = null,
                )
            }

            TachyonText(
                text = chipText,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._inversed,
            )
        }
    }
}
