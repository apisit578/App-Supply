package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonCheckBox(
    text: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Checkbox(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors =
                CheckboxDefaults.colors(
                    checkedColor = TachyonDictionary.content.primary._default,
                    checkmarkColor = TachyonDictionary.content._inversed,
                ),
        )
        TachyonText(
            text = text,
            style = TachyonDictionary.body._2,
            color = TachyonDictionary.content._default,
        )
    }
}
