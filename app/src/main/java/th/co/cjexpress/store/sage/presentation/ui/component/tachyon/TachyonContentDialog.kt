package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import th.co.cjexpress.store.sage.presentation.model.component.TachyonContentDialogUIModel
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachyonContentDialog(
    dialogData: TachyonContentDialogUIModel,
    titleTextRes: Int,
    content: @Composable () -> Unit,
) {
    var showDialog by remember { mutableStateOf(true) }

    if (showDialog) {
        BasicAlertDialog(
            properties =
                DialogProperties(
                    dismissOnClickOutside = false,
                    dismissOnBackPress = false,
                ),
            onDismissRequest = {
                showDialog = false
            },
        ) {
            Surface(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(TachyonDictionary.space.padding._md),
                shape = RoundedCornerShape(20.dp),
                color = TachyonDictionary.surface.static._default1,
            ) {
                Column {
                    Column(
                        modifier =
                            Modifier
                                .padding(TachyonDictionary.space.padding._xl),
                    ) {
                        TachyonText(
                            modifier =
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth(),
                            text = stringResource(titleTextRes),
                            style = TachyonDictionary.headLine._2,
                            color = TachyonDictionary.content._default,
                            textAlign = TextAlign.Center,
                        )
                    }

                    Column(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = TachyonDictionary.space.padding._xl,
                                ),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                    ) {
                        content()
                    }

                    Row(
                        modifier =
                            Modifier
                                .fillMaxWidth()
                                .padding(TachyonDictionary.space.padding._xl),
                        horizontalArrangement =
                            Arrangement.spacedBy(
                                TachyonDictionary.space.padding._xs,
                                alignment = Alignment.End,
                            ),
                    ) {
                        dialogData.negativeTextRes?.let {
                            TachyonOutlinedButton(
                                buttonText = stringResource(it),
                                style = TachyonOutlinedButtonStyle.GENERAL,
                                onClick = {
                                    dialogData.onNegativeClick?.invoke()
                                    showDialog = false
                                },
                            )
                        }

                        dialogData.positiveTextRes?.let {
                            TachyonSolidButton(
                                buttonText = stringResource(it),
                                style = TachyonSolidButtonStyle.GENERAL,
                                onClick = {
                                    dialogData.onPositiveClick?.invoke()
                                    showDialog = false
                                },
                            )
                        }
                    }
                }
            }
        }
    }
}
