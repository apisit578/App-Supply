package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun DSC_CheckedLazyColumn(
    modifier: Modifier,
    description1: String,
    description2: String? = null,
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Image(
            painter = painterResource(R.drawable.icon_checked),
            contentDescription = description1,
        )

        Spacer(
            modifier = Modifier.height(TachyonDictionary.space.padding._xs),
        )

        TachyonText(
            text = description1,
            style = TachyonDictionary.caption._1,
            color = TachyonDictionary.content._default,
        )

        if (description2 != null) {
            TachyonText(
                text = description2,
                style = TachyonDictionary.caption._1,
                color = TachyonDictionary.content._default,
            )
        }
    }
}
