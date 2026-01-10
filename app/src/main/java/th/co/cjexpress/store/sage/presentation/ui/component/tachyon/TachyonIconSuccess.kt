package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonIconSuccess() {
    Icon(
        imageVector = Icons.Outlined.CheckCircle,
        contentDescription = null,
        tint = TachyonDictionary.global.colors.success._60,
    )
}
