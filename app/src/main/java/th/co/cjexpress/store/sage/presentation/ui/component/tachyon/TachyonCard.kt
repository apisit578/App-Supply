package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

enum class TachyonCardStyle {
    SUCCESS,
    WARNING,
    INFO,
    ERROR,
}

@Composable
fun TachyonCard(
    icon: @Composable () -> Unit = {},
    text: String,
    style: TachyonCardStyle,
) {
    Card(
        border =
            when (style) {
                TachyonCardStyle.SUCCESS -> BorderStroke(1.dp, TachyonDictionary.border.success._default)
                TachyonCardStyle.WARNING -> BorderStroke(1.dp, TachyonDictionary.border.warning._default)
                TachyonCardStyle.INFO -> BorderStroke(1.dp, TachyonDictionary.border._default)
                TachyonCardStyle.ERROR -> BorderStroke(1.dp, TachyonDictionary.border.danger._default)
            },
        shape = RoundedCornerShape(8.dp),
        colors =
            CardDefaults.cardColors(
                containerColor =
                    when (style) {
                        TachyonCardStyle.SUCCESS -> TachyonDictionary.surface.static.success._default
                        TachyonCardStyle.WARNING -> TachyonDictionary.surface.static.warning._default
                        TachyonCardStyle.INFO -> TachyonDictionary.surface.static._default2
                        TachyonCardStyle.ERROR -> TachyonDictionary.surface.static.danger._default
                    },
            ),
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier =
                Modifier.padding(
                    all = TachyonDictionary.space.padding._sm,
                ),
        ) {
            icon()

            Spacer(modifier = Modifier.width(8.dp))

            TachyonText(
                modifier = Modifier.align(Alignment.CenterVertically),
                text = text,
                style = TachyonDictionary.body._2,
                color = TachyonDictionary.content._default,
            )
        }
    }
}
