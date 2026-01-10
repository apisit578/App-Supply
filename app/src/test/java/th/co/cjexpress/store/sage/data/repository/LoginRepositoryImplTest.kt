package th.co.cjexpress.store.sage.data.repository

import ca.mimic.oauth2library.OAuthResponse
import io.mockk.MockKAnnotations
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.unmockkAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.manager.OAuth2Manager
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.callback.AuthTokenCallback
import th.co.cjexpress.store.sage.utils.JsonUtils

class LoginRepositoryImplTest {
    @RelaxedMockK
    lateinit var oAuth2Manager: OAuth2Manager

    @RelaxedMockK
    lateinit var jsonUtils: JsonUtils

    @RelaxedMockK
    lateinit var authTokenRespModelMock: AuthTokenRespModel

    private lateinit var loginRepository: LoginRepositoryImpl

    @Before
    fun setUp() {
        MockKAnnotations.init(this)
        loginRepository = LoginRepositoryImpl(oAuth2Manager, jsonUtils)
    }

    @After
    fun tearDown() {
        unmockkAll()
    }

    @Test
    fun test_getAuthToken_success_with_code_200_expect_callback_authSuccess() =
        runTest {
            // given
            val responseMock = mockk<OAuthResponse>(relaxed = true)
            every { responseMock.isSuccessful } returns true
            every { responseMock.code } returns 200
            every { responseMock.body } returns "test_body"
            every { jsonUtils.fromJson("test_body", AuthTokenRespModel::class.java) } returns authTokenRespModelMock
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns responseMock
            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )
            // then
            coVerify { callback.onAuthSuccess(any()) }
        }

    @Test
    fun test_getAuthToken_success_with_code_202_expect_callback_authError() =
        runTest {
            // given
            val responseMock = mockk<OAuthResponse>(relaxed = true)
            every { responseMock.isSuccessful } returns true
            every { responseMock.code } returns 202
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns responseMock
            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )
            // then
            coVerify { callback.onAuthError(match { it.description.contains("code != 200") }) }
        }

    @Test
    fun test_getAuthToken_failure_with_code_400_expect_callback_authError() =
        runTest {
            // given
            val responseMock = mockk<OAuthResponse>(relaxed = true)
            every { responseMock.isSuccessful } returns false
            every { responseMock.code } returns 400
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns responseMock
            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )
            // then
            coVerify { callback.onAuthError(match { it.description == "ระบบไม่พร้อมใช้งาน" }) }
        }

    @Test
    fun test_getAuthToken_failure_with_code_401_expect_callback_authError() =
        runTest {
            // given
            val responseMock = mockk<OAuthResponse>(relaxed = true)
            every { responseMock.isSuccessful } returns false
            every { responseMock.code } returns 401
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns responseMock
            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )
            // then
            coVerify { callback.onAuthError(any()) }
        }

    @Test
    fun test_getAuthToken_failure_with_code_500_expect_callback_authError() =
        runTest {
            // given
            val responseMock = mockk<OAuthResponse>(relaxed = true)
            every { responseMock.isSuccessful } returns false
            every { responseMock.code } returns 500
            every { responseMock.oAuthError?.errorException?.message } returns "test"
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns responseMock
            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )
            // then
            coVerify { callback.onAuthError(any()) }
        }

    @Test
    fun test_getAuthToken_failure_with_code_500_without_message_expect_callback_authError() =
        runTest {
            // given
            val responseMock = mockk<OAuthResponse>(relaxed = true)
            every { responseMock.isSuccessful } returns false
            every { responseMock.code } returns 500
            every { responseMock.oAuthError?.errorException?.message } returns null
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns responseMock

            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )

            // then
            coVerify { callback.onAuthError(any()) }
        }

    @Test
    fun test_getAuthToken_when_resp_is_null_expect_callback_authError() =
        runTest {
            // given
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns null
            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )
            // then
            coVerify { callback.onAuthError(any()) }
        }

    @Test
    fun test_getAuthToken_when_exception_expect_callback_authError() =
        runTest {
            // given
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } throws Exception()
            // when
            val callback = mockk<AuthTokenCallback>(relaxed = true)
            loginRepository.getAuthToken(
                username = "admin",
                password = "1234",
                callback = callback,
            )
            // then
            coVerify { callback.onAuthError(any()) }
        }

    @Test
    fun test_getAuthToken_failure_with_code_500_and_empty_message_expect_callback_authError() =
        runTest {
            // given
            val responseMock = mockk<OAuthResponse>(relaxed = true)
            every { responseMock.isSuccessful } returns false
            every { responseMock.code } returns 500
            every { responseMock.oAuthError?.errorException?.message } returns ""
            coEvery { oAuth2Manager.requestAuthToken("admin", "1234") } returns responseMock

            val callback = mockk<AuthTokenCallback>(relaxed = true)

            // when
            loginRepository.getAuthToken("admin", "1234", callback)

            // then
            coVerify { callback.onAuthError(match { it.description.contains("code:500") }) }
        }
}
