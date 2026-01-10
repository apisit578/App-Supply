package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun TachyonListItemRow(
    modifier: Modifier = Modifier,
    canClick: Boolean = true,
    onClick: () -> Unit = {},
    content: @Composable (RowScope.() -> Unit),
) {
    Column(
        modifier =
            Modifier
                .background(TachyonDictionary.surface.static._default2)
                .heightIn(
                    min = (56 / LocalDensity.current.density).dp,
                ).wrapContentHeight()
                .fillMaxWidth()
                .focusable()
                .clickable(
                    enabled = canClick,
                    onClick = safeClickWithDebounce { onClick.invoke() },
                ).then(modifier),
    ) {
        Row(
            modifier =
                Modifier
                    .wrapContentHeight()
                    .padding(TachyonDictionary.space.padding._md),
            horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
        ) {
            content()
        }
    }
}
