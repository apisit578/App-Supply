package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

enum class TachyonSolidChipStatus {
    SUCCESS,
    ERROR,
    WARNING,
    PROCESS,
}

@Composable
fun TachyonSolidChip(
    modifier: Modifier = Modifier,
    status: TachyonSolidChipStatus,
    chipText: String,
) {
    Surface(
        modifier =
            Modifier
                .wrapContentWidth()
                .height((40 / LocalDensity.current.density).dp)
                .then(modifier),
        shape = RoundedCornerShape(8.dp),
        color =
            when (status) {
                TachyonSolidChipStatus.SUCCESS -> TachyonDictionary.content.success._default
                TachyonSolidChipStatus.ERROR -> TachyonDictionary.content.danger._default
                TachyonSolidChipStatus.WARNING -> TachyonDictionary.content.warning._default
                TachyonSolidChipStatus.PROCESS -> TachyonDictionary.content.process._default
            },
        contentColor = TachyonDictionary.content._inversed,
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier =
                Modifier.padding(
                    start = 10.dp,
                    end = 10.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement =
                Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
        ) {
            TachyonText(
                text = chipText,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._inversed,
            )
        }
    }
}
