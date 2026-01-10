package th.co.cjexpress.store.sage.presentation.ui.component.tachyon

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.utils.SoundPoolUtils

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TachyonAlertDialog(dialogData: TachyonAlertDialogUIModel) {
    var showDialog by remember { mutableStateOf(false) }

    LaunchedEffect(dialogData.type) {
        // play sound
        // Note: play 1 time by type when recompose
        SoundPoolUtils.play(type = dialogData.type)
    }

    LaunchedEffect(showDialog) {
        showDialog = true
    }

    if (showDialog) {
        BasicAlertDialog(
            properties =
                DialogProperties(
                    dismissOnClickOutside = dialogData.cancelable,
                    dismissOnBackPress = dialogData.cancelable,
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
                                .fillMaxWidth()
                                .padding(TachyonDictionary.space.padding._xl),
//                        verticalArrangement =
//                            Arrangement.spacedBy(
//                                TachyonDictionary.space.padding._md,
//                            ),
                    ) {
                        Column(
                            modifier =
                                Modifier
                                    .fillMaxWidth(),
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center,
                        ) {
                            when (dialogData.type) {
                                ErrorType.ERROR, ErrorType.SESSION_TIMEOUT -> TachyonIconDanger()
                                ErrorType.WARNING -> TachyonIconWarning()
                                ErrorType.SUCCESS -> TachyonIconSuccess()
                                ErrorType.QUESTION -> TachyonIconQuestion()
                            }
                        }

                        Spacer(modifier = Modifier.padding(top = TachyonDictionary.space.padding._xs))

                        TachyonText(
                            modifier =
                                Modifier
                                    .wrapContentHeight()
                                    .fillMaxWidth(),
                            text = dialogData.titleText,
                            style = TachyonDictionary.headLine._2,
                            color = TachyonDictionary.content._default,
                            textAlign = TextAlign.Center,
                        )
                        if (dialogData.messageText.isNotEmpty()) {
                            TachyonText(
                                modifier =
                                    Modifier
                                        .fillMaxWidth(),
                                segmentsText =
                                    if (dialogData.showF1Message) {
                                        listOf(
                                            TextSegment.Highlight(
                                                stringResource(R.string.text_please_open_ticket_via_f1),
                                                TachyonDictionary.content.danger._default,
                                            ),
                                            TextSegment.Normal(dialogData.messageText),
                                        )
                                    } else {
                                        listOf(
                                            TextSegment.Normal(dialogData.messageText),
                                        )
                                    },
                                style = TachyonDictionary.body._1,
                                color = TachyonDictionary.content._default,
                                textAlign = TextAlign.Center,
                            )
                        }

                        dialogData.ref?.let {
                            Spacer(modifier = Modifier.padding(top = TachyonDictionary.space.padding._sm))

                            TachyonText(
                                modifier = Modifier.fillMaxWidth(),
                                text = "($it)",
                                style = TachyonDictionary.body._1,
                                color = TachyonDictionary.content._default,
                                textAlign = TextAlign.Center,
                            )
                        }
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
