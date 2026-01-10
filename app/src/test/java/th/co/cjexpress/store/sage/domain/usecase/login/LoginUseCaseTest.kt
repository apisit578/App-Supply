package th.co.cjexpress.store.sage.domain.usecase.login

import com.auth0.android.jwt.Claim
import com.auth0.android.jwt.JWT
import io.mockk.MockKAnnotations
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.every
import io.mockk.impl.annotations.RelaxedMockK
import io.mockk.mockk
import io.mockk.slot
import io.mockk.spyk
import io.mockk.unmockkAll
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.callback.AuthTokenCallback
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.repository.LoginRepository
import th.co.cjexpress.store.sage.utils.CrashReporter
import th.co.cjexpress.store.sage.utils.ResourceUtils

class LoginUseCaseTest {
    @RelaxedMockK lateinit var loginRepository: LoginRepository

    @RelaxedMockK lateinit var configRepository: ConfigRepository

    @RelaxedMockK lateinit var resourceUtils: ResourceUtils

    @RelaxedMockK lateinit var crashReporter: CrashReporter

    private val callbackSlot = slot<AuthTokenCallback>()

    private lateinit var mockJwt: JWT
    private lateinit var mockAuthTokenRespModel: AuthTokenRespModel
    private lateinit var appConfig: AppConfig
    private lateinit var loginUseCase: LoginUseCase

    @Before
    fun setUp() {
        MockKAnnotations.init(this)

        mockJwt = mockk(relaxed = true)
        mockAuthTokenRespModel = mockk(relaxed = true)
        appConfig = mockk(relaxed = true)

        every { configRepository.getAppConfig() } returns appConfig
        every { mockAuthTokenRespModel.token } returns "test_token"
        every { mockJwt.claims["branch"]?.asString() } returns "M04C"
        // default: app มี branch เดิม
        every { appConfig.getBranchInfo()?.branchCode } returns "M04C"

        loginUseCase =
            spyk(
                LoginUseCase(
                    loginRepository = loginRepository,
                    configRepository = configRepository,
                    resourceUtils = resourceUtils,
                    jwtFactory = { _ -> mockJwt },
                    crashReporter = crashReporter,
                ),
                recordPrivateCalls = true,
            )
    }

    @After
    fun tearDown() {
        unmockkAll()
        clearAllMocks()
    }

    // ---------- helper ----------
    private fun invokeCheckMatchBranch(
        target: LoginUseCase,
        userBranch: String,
    ): Boolean {
        val m = LoginUseCase::class.java.getDeclaredMethod("checkMatchBranch", String::class.java)
        m.isAccessible = true
        return m.invoke(target, userBranch) as Boolean
    }

    @Test
    fun login_by_branch_manager_expect_return_auth_success() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/branch-manager")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { crashReporter.setUserId(any()) }
            verify { crashReporter.setCustomKey("BranchCode", any()) }
            assertEquals(mockAuthTokenRespModel, result)
        }

    @Test
    fun login_by_branch_staff_expect_return_auth_success() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/branch-staff")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { crashReporter.setUserId(any()) }
            verify { crashReporter.setCustomKey("BranchCode", any()) }
            assertEquals(mockAuthTokenRespModel, result)
        }

    @Test
    fun login_with_multiple_groups_picks_first_service_posback_role() =
        runTest {
            // กลุ่มแรกไม่ใช่ service.posback แต่มีอันที่สองเป็น branch-staff
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns
                listOf("something-else", "/service.posback/branch-staff", "/service.posback/oc")
            every { appConfig.getBranchInfo()?.branchCode } returns "M04C"

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            assertEquals(mockAuthTokenRespModel, result)
        }

    @Test
    fun login_by_oc_expect_return_auth_success() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/oc")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { crashReporter.setUserId(any()) }
            verify { crashReporter.setCustomKey("BranchCode", any()) }
            assertEquals(mockAuthTokenRespModel, result)
        }

    @Test
    fun login_by_audit_and_isRegenToken_is_false_expect_return_error_auth_branch_required() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/audit")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", false).await()

            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            assertEquals(
                AuthTokenRespModel(
                    error = "auth_branch_required",
                    description = "ข้อมูลสาขาไม่ถูกต้อง\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_with_other_role_expect_return_error_access_denied() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/test")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(
                AuthTokenRespModel(
                    error = "access_denied",
                    description = "คุณไม่มีสิทธิ์ใช้งาน\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_with_other_group_expect_return_error_invalid_group() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.xxxxx/audit")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(
                AuthTokenRespModel(
                    error = "invalid_group",
                    description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (invalid group)\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_with_no_groups_expect_return_error_group_required() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf<String>()

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(
                AuthTokenRespModel(
                    error = "group_required",
                    description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (group required)\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_with_groups_null_expect_group_required() =
        runTest {
            every { mockJwt.claims["groups"] } returns null

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(
                AuthTokenRespModel(
                    error = "group_required",
                    description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (group required)\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_with_no_branch_expect_return_error_branch_required() =
        runTest {
            every { mockJwt.claims["branch"]?.asString() } returns null
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf<String>()

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(
                AuthTokenRespModel(
                    error = "branch_required",
                    description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (branch required)\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_error_expect_return_error_exception() =
        runTest {
            val throwingUseCase =
                spyk(
                    LoginUseCase(loginRepository, configRepository, resourceUtils, { _ -> mockJwt }, crashReporter),
                    recordPrivateCalls = true,
                )
            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = throwingUseCase.execute("admin", "1234", true).await()

            assertEquals(
                AuthTokenRespModel(
                    error = "exception",
                    description = "เกิดข้อผิดพลาดบางอย่าง\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_failure_expect_return_error_exception() =
        runTest {
            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthError(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()
            assertEquals(mockAuthTokenRespModel, result)
        }

    // ---------- setLoginUser / resetLoginUser / checkMatchBranch ครบทุกกิ่ง ----------
    @Test
    fun setLoginUser_with_empty_branch_does_not_update_branchInfo() =
        runTest {
            every { appConfig.getBranchInfo()?.branchCode } returns "" // mapBranch empty
            every { mockJwt.claims["branch"]?.asString() } returns "" // user branch empty
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/branch-staff")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(mockAuthTokenRespModel, result)
            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { appConfig.username = "admin" }
            verify(exactly = 0) { configRepository.updateBranchInfo(any()) }
        }

    @Test
    fun setLoginUser_with_non_empty_branch_and_existing_non_empty_branchCode_does_not_update_branchInfo() =
        runTest {
            every { appConfig.getBranchInfo()?.branchCode } returns "M04C"
            every { mockJwt.claims["branch"]?.asString() } returns "M04C"
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/branch-staff")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(mockAuthTokenRespModel, result)
            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { appConfig.username = "admin" }
            verify(exactly = 0) { configRepository.updateBranchInfo(any()) }
        }

    @Test
    fun setLoginUser_with_non_empty_branch_and_empty_mapBranch_updates_branchInfo() =
        runTest {
            every { appConfig.getBranchInfo()?.branchCode } returns "" // mapBranch empty
            every { mockJwt.claims["branch"]?.asString() } returns "M04C"
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/branch-staff")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(mockAuthTokenRespModel, result)
            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { appConfig.username = "admin" }
            verify(exactly = 1) {
                configRepository.updateBranchInfo(
                    match<BranchInfoModel> { it.branchCode == "M04C" && it.branchName == "M04C" },
                )
            }
        }

    @Test
    fun setLoginUser_with_non_empty_branch_and_getBranchInfo_object_is_null_updates_branchInfo() =
        runTest {
            every { appConfig.getBranchInfo() } returns null
            every { mockJwt.claims["branch"]?.asString() } returns "M04C"
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/branch-staff")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(mockAuthTokenRespModel, result)
            verify(exactly = 1) {
                configRepository.updateBranchInfo(
                    match<BranchInfoModel> { it.branchCode == "M04C" && it.branchName == "M04C" },
                )
            }
        }

    @Test
    fun resetLoginUser_clears_authToken_and_username() =
        runTest {
            every { appConfig.getBranchInfo()?.branchCode } returns "M04C"
            every { mockJwt.claims["branch"]?.asString() } returns "M04C"
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/audit")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }
            val result = loginUseCase.execute("admin", "1234", false).await()

            assertEquals(
                AuthTokenRespModel(
                    error = "auth_branch_required",
                    description = "ข้อมูลสาขาไม่ถูกต้อง\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { appConfig.authToken = null }
            verify { appConfig.username = "" }
        }

    @Test
    fun checkMatchBranch_mapBranch_empty_returns_true() {
        every { appConfig.getBranchInfo()?.branchCode } returns "" // mapBranch empty
        val useCase = spyk(LoginUseCase(loginRepository, configRepository, resourceUtils, { _ -> mockJwt }, crashReporter))
        assertTrue(invokeCheckMatchBranch(useCase, "ANY"))
    }

    @Test
    fun checkMatchBranch_equal_returns_true() {
        every { appConfig.getBranchInfo()?.branchCode } returns "M04C"
        val useCase = spyk(LoginUseCase(loginRepository, configRepository, resourceUtils, { _ -> mockJwt }, crashReporter))
        assertTrue(invokeCheckMatchBranch(useCase, "M04C"))
    }

    @Test
    fun checkMatchBranch_not_equal_returns_false() {
        every { appConfig.getBranchInfo()?.branchCode } returns "M04C"
        val useCase = spyk(LoginUseCase(loginRepository, configRepository, resourceUtils, { _ -> mockJwt }, crashReporter))
        assertFalse(invokeCheckMatchBranch(useCase, "M04B"))
    }

    @Test
    fun setCrashlytics_sets_user_and_branch_custom_key_when_branch_exists() {
        every { appConfig.username } returns "testUser"
        every { appConfig.getBranchInfo()?.branchCode } returns "BR123"

        loginUseCase.setCrashlytics()

        verify { crashReporter.setUserId("testUser") }
        verify { crashReporter.setCustomKey("BranchCode", "BR123") }
    }

    @Test
    fun setCrashlytics_sets_user_and_branch_custom_key_when_branch_is_null() {
        every { appConfig.username } returns "testUser"
        every { appConfig.getBranchInfo()?.branchCode } returns null

        loginUseCase.setCrashlytics()

        verify { crashReporter.setUserId("testUser") }
        verify { crashReporter.setCustomKey("BranchCode", "N/A") }
    }

    @Test
    fun setCrashlytics_username_null_uses_empty_string() {
        every { appConfig.username } returns null
        every { appConfig.getBranchInfo()?.branchCode } returns null

        loginUseCase.setCrashlytics()

        verify { crashReporter.setUserId("") }
        verify { crashReporter.setCustomKey("BranchCode", "N/A") }
    }

    @Test
    fun handleBranchStaff_branch_mismatch_calls_reset_and_returns_error() {
        every { appConfig.getBranchInfo()?.branchCode } returns "M04C"

        val m =
            LoginUseCase::class.java.getDeclaredMethod(
                "handleBranchStaff",
                String::class.java, // branch
                String::class.java, // username
                AuthTokenRespModel::class.java, // authResp
            )
        m.isAccessible = true

        @Suppress("UNCHECKED_CAST")
        val result =
            m.invoke(
                loginUseCase,
                "M04B", // branch จาก token (ไม่ตรงกับ mapBranch)
                "admin", // username
                mockAuthTokenRespModel, // authResp
            ) as Result<AuthTokenRespModel>

        assertTrue(result is Result.Error)
        val err = result as Result.Error
        assertEquals("branch_mismatch", err.data?.error)

        verify { appConfig.authToken = null }
        verify { appConfig.username = "" }

        verify(exactly = 0) { configRepository.updateAuthToken(any()) }
    }

    @Test
    fun decodeJwt_branch_claim_present_but_value_null_returns_branch_required() =
        runTest {
            val specialJwt = mockk<JWT>(relaxed = true)
            val branchClaim = mockk<Claim>(relaxed = true)
            every { branchClaim.asString() } returns null
            every { specialJwt.claims } returns mapOf("branch" to branchClaim)

            val useCase =
                spyk(
                    LoginUseCase(loginRepository, configRepository, resourceUtils, { _ -> specialJwt }, crashReporter),
                    recordPrivateCalls = true,
                )

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }

            val result = useCase.execute("admin", "1234", true).await()
            assertEquals(
                AuthTokenRespModel(
                    error = "branch_required",
                    description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (branch required)\r\nกรุณาติดต่อทีม IT Support\r\n(user: admin)",
                ),
                result,
            )
        }

    @Test
    fun login_by_amoc_with_isRegenToken_true_expect_success() =
        runTest {
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/amoc")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }

            val result = loginUseCase.execute("admin", "1234", true).await()

            verify { configRepository.updateAuthToken(mockAuthTokenRespModel) }
            verify { crashReporter.setUserId(any()) }
            verify { crashReporter.setCustomKey("BranchCode", any()) }
            assertEquals(mockAuthTokenRespModel, result)
        }

    @Test
    fun setLoginUser_branch_non_empty_and_branchInfo_object_null_updates_branchInfo() =
        runTest {
            every { appConfig.getBranchInfo() } returns null // object เป็น null
            every { mockJwt.claims["branch"]?.asString() } returns "M04C"
            every { mockJwt.claims["groups"]?.asObject(List::class.java) } returns listOf("/service.posback/branch-staff")

            coEvery { loginRepository.getAuthToken("admin", "1234", capture(callbackSlot)) } answers {
                callbackSlot.captured.onAuthSuccess(mockAuthTokenRespModel)
            }

            val result = loginUseCase.execute("admin", "1234", true).await()

            assertEquals(mockAuthTokenRespModel, result)
            verify(exactly = 1) {
                configRepository.updateBranchInfo(
                    match { it.branchCode == "M04C" && it.branchName == "M04C" },
                )
            }
        }

    @Test
    fun checkMatchBranch_branchInfo_object_null_returns_true() {
        every { appConfig.getBranchInfo() } returns null
        val useCase =
            spyk(
                LoginUseCase(loginRepository, configRepository, resourceUtils, { _ -> mockJwt }, crashReporter),
                recordPrivateCalls = true,
            )
        val m = LoginUseCase::class.java.getDeclaredMethod("checkMatchBranch", String::class.java)
        m.isAccessible = true

        val result = m.invoke(useCase, "ANY") as Boolean
        assertTrue(result) // mapBranch == "" -> true
    }
}
