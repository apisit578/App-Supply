package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@Composable
fun TachyonExtendedFloatingActionButtonFilled(
    modifier: Modifier,
    buttonText: String,
    leadingIcon: ImageVector? = null,
    onClick: () -> Unit,
) {
    FloatingActionButton(
        modifier =
            Modifier
                .height(40.dp)
                .then(modifier),
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        shape = CircleShape,
        onClick = safeClickWithDebounce { onClick() },
        containerColor = TachyonDictionary.surface.action.primary._default,
        contentColor = TachyonDictionary.content._inversed,
    ) {
        Row(
            modifier
                .padding(
                    start = 10.dp,
                    end = 13.dp,
                ),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
        ) {
            if (leadingIcon != null) {
                Icon(
                    modifier =
                        Modifier
                            .width((18 / LocalDensity.current.density).dp)
                            .height((18 / LocalDensity.current.density).dp),
                    imageVector = Icons.Outlined.Add,
                    contentDescription = null,
                )
            }

            TachyonText(
                text = buttonText,
                style = TachyonDictionary.headLine._5,
            )
        }
    }
}
