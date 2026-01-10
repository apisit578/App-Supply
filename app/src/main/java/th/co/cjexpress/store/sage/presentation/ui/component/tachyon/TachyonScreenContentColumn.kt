package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonScreenContentColumn(
    scaffoldInnerPadding: PaddingValues,
    content: @Composable (ColumnScope.() -> Unit),
) {
    Column(
        modifier =
            Modifier
                .fillMaxSize()
                .padding(scaffoldInnerPadding)
                .background(color = TachyonDictionary.surface.static._default1),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(
                        start = TachyonDictionary.space.padding._xl,
                        top = TachyonDictionary.space.padding._md,
                        end = TachyonDictionary.space.padding._xl,
                        bottom = TachyonDictionary.space.padding._md,
                    ),
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
        ) {
            content()
        }
    }
}
