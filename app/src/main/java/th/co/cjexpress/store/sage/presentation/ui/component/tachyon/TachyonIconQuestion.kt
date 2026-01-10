package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonIconQuestion() {
    Icon(
        imageVector = Icons.Outlined.Info,
        contentDescription = null,
        tint = TachyonDictionary.content._subdued,
    )
}
