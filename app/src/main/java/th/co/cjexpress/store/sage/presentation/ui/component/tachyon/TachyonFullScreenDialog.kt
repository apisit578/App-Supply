package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import th.co.cjexpress.store.sage.R

@Preview
@Composable
fun TachyonFullScreenDialog() {
    Dialog(
        onDismissRequest = {},
        properties =
            DialogProperties(
                usePlatformDefaultWidth = false,
                decorFitsSystemWindows = false,
            ),
    ) {
        Surface(
            modifier =
                Modifier
                    .fillMaxSize(),
            shape = RoundedCornerShape(0.dp),
        ) {
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        // Add padding inside the dialog
                        .padding(16.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "Fullscreen Dialog",
                        style = MaterialTheme.typography.titleLarge,
                    )
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This is an example of a fullscreen dialog in Material 3. You can customize it as per your needs.",
                    style = MaterialTheme.typography.bodyLarge,
                )

                Spacer(modifier = Modifier.weight(1f)) // Push actions to the bottom
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                ) {
                    TextButton(onClick = {}) {
                        Text(stringResource(id = R.string.text_cancel_en))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = {}) {
                        Text(stringResource(id = R.string.text_confirm_en))
                    }
                }
            }
        }
    }
}
