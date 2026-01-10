package th.co.cjexpress.store.sage.presentation.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.constants.MainMenuType
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun TachyonMenuButton(
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    menuType: MainMenuType,
    menuIcon: Int,
    menuName: String,
    onClick: (MainMenuType) -> Unit,
) {
    Column(
        modifier =
            modifier
                .then(
                    Modifier
                        .defaultMinSize(minWidth = (128 / LocalDensity.current.density).dp)
                        .height((112 / LocalDensity.current.density).dp)
                        .wrapContentWidth()
                        .padding(TachyonDictionary.space.padding._2xs)
                        .selectable(
                            selected = enabled,
                            enabled = enabled,
                            onClick =
                                safeClickWithDebounce {
                                    if (enabled) {
                                        onClick(menuType)
                                    }
                                },
                        ),
                ),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._2xs),
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier =
                Modifier
                    .width((40 / LocalDensity.current.density).dp)
                    .height((40 / LocalDensity.current.density).dp)
                    .background(
                        color =
                            if (enabled) {
                                TachyonDictionary.surface.action.primary._default
                            } else {
                                TachyonDictionary.content._disabled
                            },
                        shape = RoundedCornerShape(8.dp),
                    ),
        ) {
            Icon(
                modifier =
                    Modifier
                        .width((18 / LocalDensity.current.density).dp)
                        .height((18 / LocalDensity.current.density).dp),
                painter = painterResource(id = menuIcon),
                contentDescription = menuName,
                tint = TachyonDictionary.content._inversed,
            )
        }

        TachyonText(
            text = menuName,
            color =
                if (enabled) {
                    TachyonDictionary.content._default
                } else {
                    TachyonDictionary.content._disabled
                },
            textAlign = TextAlign.Center,
            style = TachyonDictionary.caption._1,
            maxLines = 2,
        )
    }
}
