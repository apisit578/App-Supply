package th.co.cjexpress.store.sage.presentation.ui.screen.login

import android.content.Intent
import androidx.activity.ComponentActivity
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import th.co.cjexpress.store.sage.BuildConfig
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.presentation.activity.mainMenu.MainMenuActivity
import th.co.cjexpress.store.sage.presentation.ui.component.LoginSheet
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButton
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonOutlinedButtonStyle
import th.co.cjexpress.store.sage.presentation.ui.component.tachyon.TachyonText
import th.co.cjexpress.store.sage.presentation.ui.screen.base.BaseScreen
import th.co.cjexpress.store.sage.presentation.ui.theme.TachyonDictionary
import th.co.cjexpress.store.sage.presentation.viewmodel.login.LoginViewModel
import th.co.cjexpress.store.sage.utils.IPV4

class LoginScreen(
    viewModel: LoginViewModel,
) : BaseScreen<LoginViewModel>(viewModel) {
    @Composable
    override fun ScreenContent() {
        val loadBranchConfigState by viewModel.loadBranchConfigState.collectAsState()
        val showLoginSheet = viewModel.showLoginSheet

        LaunchedEffect(Unit) {
            viewModel.loadGlobalConfig()
        }

        if (showLoginSheet) {
            LoginSheet(
                isFirstLogin = true,
                scrimColor = Color.Transparent,
                onDismissRequest = { viewModel.showLoginSheet(false) },
            ) {
                viewModel.loadBranchConfig()
            }
        }

        if (loadBranchConfigState?.isSuccess == true) {
            val context = LocalContext.current
            context.startActivity(Intent(context, MainMenuActivity::class.java)).also {
                (context as ComponentActivity).finish()
            }
        }

        val initializingIndicator by viewModel.initializingIndicator.collectAsState()
        val initializingMessage by viewModel.initializingMessage.collectAsState()
        val initializingErrorState by viewModel.initializingErrorState.collectAsState()
        val readyToLogin by viewModel.readyToLogin.collectAsState()

        Column(
            modifier =
                Modifier
                    .fillMaxSize(),
            verticalArrangement =
                Arrangement.spacedBy(
                    space = TachyonDictionary.space.padding._xl,
                    alignment = Alignment.CenterVertically,
                ),
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(TachyonDictionary.space.padding._xs),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size((100 / LocalDensity.current.density).dp),
//                                    .clip(CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.cj_logo),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                    )
                }

                TachyonText(
                    text = stringResource(R.string.text_sage),
                    style = TachyonDictionary.display._2,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text = stringResource(R.string.text_sage_full),
                    style = TachyonDictionary.headLine._5,
                    color = TachyonDictionary.content._default,
                )
            }

            if (readyToLogin) {
                TachyonOutlinedButton(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    buttonText = "เข้าสู่ระบบ",
                    style = TachyonOutlinedButtonStyle.GENERAL,
                    onClick = { viewModel.showLoginSheet(true) },
                )
            } else {
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement =
                        Arrangement.spacedBy(
                            space = TachyonDictionary.space.padding._md,
                            alignment = Alignment.CenterVertically,
                        ),
                ) {
                    if (initializingIndicator) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(16.dp),
                            color = TachyonDictionary.content.primary._default,
                        )
                    }

                    if (initializingMessage.isNotEmpty()) {
                        TachyonText(
                            text = initializingMessage,
                            style = TachyonDictionary.caption._1,
                            color =
                                if (!initializingErrorState) {
                                    TachyonDictionary.content._default
                                } else {
                                    TachyonDictionary.content.danger._default
                                },
                        )
                    }
                }
            }
        }
    }

    @Composable
    override fun BottomAppBarContainer() {
        val branchInfo by viewModel.branchIpState.collectAsState()

        Row(
            modifier =
                Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                TachyonText(
                    text =
                        if (branchInfo?.data != null) {
                            "สาขา: ${branchInfo!!.data!!.branchCode}"
                        } else {
                            stringResource(R.string.text_branch_na)
                        },
                    style = TachyonDictionary.caption._1,
                    color = TachyonDictionary.content._default,
                )

                branchInfo?.data?.branchNameTh?.let {
                    TachyonText(
                        modifier = Modifier.fillMaxWidth(0.5f),
                        text = it,
                        style = TachyonDictionary.body._1,
                        color = TachyonDictionary.content._default,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                }

                TachyonText(
                    text = IPV4,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }

            Column(
                horizontalAlignment = Alignment.End,
            ) {
                TachyonText(
                    text = stringResource(R.string.text_version),
                    style = TachyonDictionary.caption._1,
                    color = TachyonDictionary.content._default,
                )

                TachyonText(
                    text = BuildConfig.VERSION_NAME,
                    style = TachyonDictionary.body._2,
                    color = TachyonDictionary.content._default,
                )
            }
        }
    }
}
