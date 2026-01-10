package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonEmptyLazyColumn(
    modifier: Modifier,
    painter: Painter,
    title: String? = null,
    description: String,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            modifier =
                Modifier
                    .width((112 / LocalDensity.current.density).dp)
                    .height((88 / LocalDensity.current.density).dp),
            painter = painter,
            contentDescription = description,
        )

        Spacer(
            modifier = Modifier.height(TachyonDictionary.space.padding._xs),
        )

        if (!title.isNullOrEmpty()) {
            TachyonText(
                text = title,
                style = TachyonDictionary.headLine._3,
                color = TachyonDictionary.content._default,
                textAlign = TextAlign.Center,
            )
        }

        TachyonText(
            text = description,
            style = TachyonDictionary.headLine._2,
            color = TachyonDictionary.content._default,
            textAlign = TextAlign.Center,
        )
    }
}
