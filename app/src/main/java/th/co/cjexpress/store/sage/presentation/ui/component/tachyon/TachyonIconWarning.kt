package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonIconWarning(tint: Color = TachyonDictionary.content.warning._default) {
    Icon(
        imageVector = Icons.Outlined.Info,
        contentDescription = null,
        tint = tint,
    )
}
