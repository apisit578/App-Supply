package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.painterResource
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonIconDanger() {
    Icon(
        painter = painterResource(R.drawable.baseline_warning_amber_24),
        contentDescription = null,
        tint = TachyonDictionary.content.danger._default,
    )
}
