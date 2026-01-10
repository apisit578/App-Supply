package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonBottomAppBar(content: @Composable () -> Unit = {}) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .background(TachyonDictionary.surface.static._default1)
                .padding(
                    start = TachyonDictionary.space.padding._xl,
//                    top = TachyonDictionary.space.padding._md,
                    end = TachyonDictionary.space.padding._xl,
                    bottom = TachyonDictionary.space.padding._xl,
                ),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xl),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        content()
    }
}
