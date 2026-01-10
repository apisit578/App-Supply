package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@Composable
fun TachyonCircularProgressIndicatorDialog() {
    Dialog(
        properties =
            DialogProperties(
                dismissOnClickOutside = false,
                dismissOnBackPress = false,
            ),
        onDismissRequest = {},
    ) {
        Box(
            modifier =
                Modifier
                    .width(60.dp)
                    .height(60.dp)
                    .background(Color.White, RoundedCornerShape(16.dp))
                    .padding(16.dp),
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                CircularProgressIndicator(
                    color = TachyonDictionary.content.primary._default,
                )
            }
        }
    }
}
