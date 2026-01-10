package th.co.cjexpress.store.sage.presentation.ui.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalBottomSheetProperties
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.DELAY_DEFAULT_TIME
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.presentation.model.component.TachyonAlertDialogUIModel
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonAlertDialog
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedTextField
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.component.LoginSheetViewModel
import th.co.cjexpress.store.sage.utils.safeClickWithDebounce

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginSheet(
    viewModel: LoginSheetViewModel = hiltViewModel(),
    isFirstLogin: Boolean = false,
    username: String? = null,
    scrimColor: Color = BottomSheetDefaults.ScrimColor,
    onDismissRequest: () -> Unit,
    onLoginSuccess: () -> Unit,
) {
    val loginState by viewModel.loginState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState =
        rememberModalBottomSheetState(
            skipPartiallyExpanded = true,
            confirmValueChange = {
                !loginState.isLoggingIn && isFirstLogin
            },
        )

    var inputUsername by remember { mutableStateOf("") }
    var inputPassword by remember { mutableStateOf("") }
    var isUsernameError by remember { mutableStateOf(false) }
    var isPasswordError by remember { mutableStateOf(false) }
    var showPassword by remember { mutableStateOf(false) }
    val userNameFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }

    LaunchedEffect(Unit) {
        delay(DELAY_DEFAULT_TIME)
        viewModel.logEventOpenSheet()

        if (!isFirstLogin && !username.isNullOrEmpty()) {
            inputUsername = username
        }
        sheetState.show()

        if (isFirstLogin) {
            userNameFocusRequester.requestFocus()
        } else {
            passwordFocusRequester.requestFocus()
        }
    }

    LaunchedEffect(loginState) {
        if (loginState.isSuccess) {
            sheetState.hide()

            viewModel.resetLoginState()
            onLoginSuccess()
        }
    }

    if (loginState.error?.isNotEmpty() == true) {
        TachyonAlertDialog(
            TachyonAlertDialogUIModel(
                type = ErrorType.WARNING,
                titleText = stringResource(R.string.text_login_failed),
                messageText = loginState.error!!,
                onPositiveClick = { viewModel.resetLoginState() },
            ),
        )
    }

    ModalBottomSheet(
        containerColor = TachyonDictionary.surface.static._default1,
        scrimColor =
            if (loginState.isLoggingIn) {
                BottomSheetDefaults.ScrimColor
            } else {
                scrimColor
            },
        sheetState = sheetState,
        dragHandle =
            if (loginState.isLoggingIn || !isFirstLogin) {
                null
            } else {
                { BottomSheetDefaults.DragHandle() }
            },
        onDismissRequest = {
            coroutineScope.launch {
                sheetState.hide()
            }

            viewModel.resetLoginState()
            onDismissRequest()
        },
        properties =
            ModalBottomSheetProperties(
                shouldDismissOnBackPress = isFirstLogin,
            ),
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .padding(
                        start = TachyonDictionary.space.padding._xl,
                        top = TachyonDictionary.space.padding._md,
                        end = TachyonDictionary.space.padding._xl,
                        bottom = TachyonDictionary.space.padding._md,
                    ),
            verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._md),
        ) {
            if (!isFirstLogin) {
                TachyonText(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    text = "${stringResource(R.string.text_session_timeout)}\n${stringResource(R.string.text_please_sign_in_again)}",
                    style = TachyonDictionary.body._1,
                    color = TachyonDictionary.content.danger._default,
                    textAlign = TextAlign.Center,
                )
            }

            if (loginState.isLoggingIn && isFirstLogin) {
                Spacer(
                    modifier = Modifier.height(TachyonDictionary.space.padding._xl),
                )
            }

            TachyonOutlinedTextField(
                modifier =
                    Modifier
                        .focusRequester(userNameFocusRequester),
                overrideHeight = Modifier.wrapContentHeight(),
                value = inputUsername,
                onValueChange = {
                    inputUsername = it
                    if (it.isNotEmpty()) isUsernameError = false
                },
                enabled = !loginState.isLoggingIn && isFirstLogin,
                isError = isUsernameError,
                label = {
                    TachyonText(
                        text = stringResource(id = R.string.text_username),
                        style = TachyonDictionary.caption._1,
                        color = TachyonDictionary.content._description,
                    )
                },
                trailingIcon = {
                    if (isUsernameError) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "กรุณาระบุชื่อผู้ใช้",
                        )
                    } else {
                        if (inputUsername.isNotEmpty() && !loginState.isLoggingIn && !loginState.isSuccess && isFirstLogin) {
                            IconButton(
                                onClick =
                                    safeClickWithDebounce {
                                        viewModel.logEventClearUsername()
                                        inputUsername = ""
                                    },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "ล้างค่า",
                                )
                            }
                        }
                    }
                },
                keyboardOptions =
                    KeyboardOptions.Default.copy(
                        imeAction = ImeAction.Next,
                    ),
                keyboardActions =
                    KeyboardActions(
                        onNext = {
                            passwordFocusRequester.requestFocus()
                        },
                    ),
                supportingText = {
                    if (isUsernameError) {
                        TachyonText(
                            text = stringResource(id = R.string.text_username_required),
                            style = TachyonDictionary.caption._1,
                            color = TachyonDictionary.content.danger._default,
                        )
                    }
                },
            )

            TachyonOutlinedTextField(
                modifier =
                    Modifier
                        .focusRequester(passwordFocusRequester),
                overrideHeight = Modifier.wrapContentHeight(),
                visualTransformation =
                    if (showPassword) {
                        VisualTransformation.None
                    } else {
                        PasswordVisualTransformation()
                    },
                value = inputPassword,
                onValueChange = {
                    inputPassword = it
                    if (it.isNotEmpty()) isPasswordError = false
                },
                enabled = !loginState.isLoggingIn,
                isError = isPasswordError,
                label = {
                    TachyonText(
                        text = stringResource(id = R.string.text_password),
                        style = TachyonDictionary.caption._1,
                        color = TachyonDictionary.content._description,
                    )
                },
                trailingIcon = {
                    if (isPasswordError) {
                        Icon(
                            imageVector = Icons.Filled.Info,
                            contentDescription = "กรุณาระบุรหัสผ่าน",
                        )
                    } else {
                        if (inputPassword.isNotEmpty() && !loginState.isLoggingIn && !loginState.isSuccess) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(0.dp),
                            ) {
                                IconButton(
                                    onClick =
                                        safeClickWithDebounce {
                                            showPassword = !showPassword
                                        },
                                ) {
                                    Icon(
                                        painter =
                                            if (showPassword) {
                                                painterResource(R.drawable.visibility_24px)
                                            } else {
                                                painterResource(R.drawable.visibility_off_24px)
                                            },
                                        contentDescription =
                                            if (showPassword) {
                                                "แสดงรหัสผ่าน"
                                            } else {
                                                "ซ่อนรหัสผ่าน"
                                            },
                                    )
                                }

//                                IconButton(
//                                    onClick = { inputPassword = "" },
//                                ) {
//                                    Icon(
//                                        imageVector = Icons.Default.Clear,
//                                        contentDescription = "ล้างค่า",
//                                    )
//                                }
                            }
                        } else {
                            showPassword = false
                        }
                    }
                },
                supportingText = {
                    if (isPasswordError) {
                        TachyonText(
                            text = stringResource(id = R.string.text_password_required),
                            style = TachyonDictionary.caption._1,
                            color = TachyonDictionary.content.danger._default,
                        )
                    }
                },
            )

            TachyonOutlinedButton(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                buttonText = stringResource(id = R.string.text_login),
                enabled = !loginState.isLoggingIn && !loginState.isSuccess,
                style = TachyonOutlinedButtonStyle.GENERAL,
                onClick = {
                    isUsernameError = inputUsername.isEmpty()
                    isPasswordError = inputPassword.isEmpty()

                    if (!isUsernameError && !isPasswordError) {
                        viewModel.login(inputUsername, inputPassword)
                    } else {
                        viewModel.logEventLoginFailed(
                            isUsernameError = isUsernameError,
                            isPasswordError = isPasswordError,
                        )
                    }
                },
            )
        }
    }
}
