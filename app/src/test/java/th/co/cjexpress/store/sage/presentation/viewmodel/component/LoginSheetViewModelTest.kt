package th.co.cjexpress.store.sage.presentation.viewmodel.component

import app.cash.turbine.test
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.usecase.login.LoginUseCase
import th.co.cjexpress.store.sage.presentation.state.login.LoginState
import th.co.cjexpress.store.sage.utils.BaseUnitTest
import th.co.cjexpress.store.sage.utils.ResourceUtils

@ExperimentalCoroutinesApi
class LoginSheetViewModelTest : BaseUnitTest() {
    private val loginUseCase: LoginUseCase = mockk()
    private val resourceUtils: ResourceUtils = mockk(relaxed = true)
    private lateinit var viewModel: LoginSheetViewModel

    @Before
    fun setup() {
        viewModel = LoginSheetViewModel(loginUseCase, resourceUtils, logUtils)
    }

    @Test
    fun `login success should update loginState to isSuccess true`() =
        runTest {
            val mockResponse = AuthTokenRespModel(error = "", description = "")
            coEvery { loginUseCase.execute(any(), any(), any()).await() } returns mockResponse

            viewModel.login("testuser", "password")

            viewModel.loginState.test {
                assertEquals(LoginState(isSuccess = true), awaitItem())
                cancelAndIgnoreRemainingEvents()
            }
            verify {
                logUtils.logInfo(
                    action = LogAction.LOGIN_SUCCESS,
                    message = any(),
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `login failure should update loginState with error`() =
        runTest {
            val mockResponse =
                AuthTokenRespModel(
                    error = "invalid_credentials",
                    description = "Invalid username or password",
                )
            coEvery { loginUseCase.execute(any(), any(), any()).await() } returns mockResponse

            viewModel.login("baduser", "badpass")

            viewModel.loginState.test {
                assertEquals(
                    LoginState(isSuccess = false, error = "Invalid username or password"),
                    awaitItem(),
                )
                cancelAndIgnoreRemainingEvents()
            }
            verify {
                logUtils.logError(
                    action = LogAction.LOGIN_FAILED,
                    message = "Invalid username or password",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `resetLoginState should reset to default state`() =
        runTest {
            viewModel.resetLoginState()
            assertEquals(LoginState(), viewModel.loginState.value)
        }

    @Test
    fun `logEventOpenSheet should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_open_login_sheet) } returns "Open login sheet"

            // when
            viewModel.logEventOpenSheet()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.OPEN_LOGIN_SHEET,
                    message = "Open login sheet",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventClearUsername should logInfo`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.log_message_clear_input_username) } returns "Clear input username"

            // when
            viewModel.logEventClearUsername()

            // then
            verify {
                logUtils.logInfo(
                    action = LogAction.CLEAR_INPUT_USERNAME,
                    message = "Clear input username",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventLoginFailed when username and password is empty should logError`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.text_username_required) } returns "Username required"
            every { resourceUtils.getString(R.string.text_password_required) } returns "Password required"

            // when
            viewModel.logEventLoginFailed(isUsernameError = true, isPasswordError = true)

            // then
            verify {
                logUtils.logError(
                    action = LogAction.LOGIN_FAILED,
                    message = "Username required/Password required",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventLoginFailed when username is empty should logError`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.text_username_required) } returns "Username required"

            // when
            viewModel.logEventLoginFailed(isUsernameError = true, isPasswordError = false)

            // then
            verify {
                logUtils.logError(
                    action = LogAction.LOGIN_FAILED,
                    message = "Username required",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }

    @Test
    fun `logEventLoginFailed when password is empty should logError`() =
        runTest {
            // given
            every { resourceUtils.getString(R.string.text_password_required) } returns "Password required"

            // when
            viewModel.logEventLoginFailed(isUsernameError = false, isPasswordError = true)

            // then
            verify {
                logUtils.logError(
                    action = LogAction.LOGIN_FAILED,
                    message = "Password required",
                    username = any(),
                    branchCode = any(),
                    url = any(),
                )
            }
        }
}
