package th.co.cjexpress.store.sage.domain.usecase.login

import com.auth0.android.jwt.JWT
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import th.co.cjexpress.store.sage.R
import th.co.cjexpress.store.sage.constants.ErrorType
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.callback.AuthTokenCallback
import th.co.cjexpress.store.sage.domain.model.BranchInfoModel
import th.co.cjexpress.store.sage.domain.model.Result
import th.co.cjexpress.store.sage.domain.repository.ConfigRepository
import th.co.cjexpress.store.sage.domain.repository.LoginRepository
import th.co.cjexpress.store.sage.presentation.model.BusinessException
import th.co.cjexpress.store.sage.utils.CrashReporter
import th.co.cjexpress.store.sage.utils.CrashlyticsCrashReporter
import th.co.cjexpress.store.sage.utils.ResourceUtils

class LoginUseCase(
    private val loginRepository: LoginRepository,
    private val configRepository: ConfigRepository,
    private val resourceUtils: ResourceUtils,
    private val jwtFactory: (String) -> JWT = ::JWT,
    private val crashReporter: CrashReporter = CrashlyticsCrashReporter(),
) {
    private val appConfig = configRepository.getAppConfig()

    fun execute(
        username: String,
        password: String,
        isRegenToken: Boolean,
    ): Deferred<AuthTokenRespModel?> =
        CoroutineScope(Dispatchers.IO).async {
            val loginDeferred = CompletableDeferred<AuthTokenRespModel?>()

            loginRepository.getAuthToken(
                username,
                password,
                object : AuthTokenCallback {
                    override fun onAuthSuccess(response: AuthTokenRespModel?) {
                        if (response != null) {
                            when (val authToken = decodeJwt(username, response, isRegenToken)) {
                                is Result.Success -> {
                                    setCrashlytics()

                                    loginDeferred.complete(
                                        authToken.data,
                                    )

                                    return
                                }

                                is Result.Error -> {
                                    loginDeferred.complete(
                                        authToken.data,
                                    )
                                }
                            }
                        }
                    }

                    override fun onAuthError(response: AuthTokenRespModel?) {
                        if (response != null) {
                            loginDeferred.complete(
                                response,
                            )
                        }
                    }
                },
            )

            return@async loginDeferred.await()
        }

    private fun decodeJwt(
        username: String,
        authResp: AuthTokenRespModel,
        isRegenToken: Boolean,
    ): Result<AuthTokenRespModel> {
        return try {
            val jwt = jwtFactory(authResp.token)

            val branch =
                jwt.claims["branch"]?.asString()
                    ?: return branchRequired(username)

            @Suppress("UNCHECKED_CAST")
            val groups = jwt.claims["groups"]?.asObject(List::class.java)
            if (groups.isNullOrEmpty()) return groupRequired(username)

            val role =
                extractServicePosbackRole(groups)
                    ?: return invalidGroup(username)

            when (role) {
                "/service.posback/branch-manager",
                "/service.posback/branch-staff",
                -> handleBranchStaff(branch, username, authResp)

                "/service.posback/oc",
                "/service.posback/audit",
                "/service.posback/amoc",
                "/service.posback/area-manager",
                -> handleAreaRoles(isRegenToken, username, authResp)

                else -> accessDenied(username)
            }
        } catch (_: Exception) {
            exceptionError(username)
        }
    }

    private fun extractServicePosbackRole(groups: List<*>): String? =
        groups.map { it.toString() }.firstOrNull { it.startsWith("/service.posback/") }

    private fun handleBranchStaff(
        branch: String,
        username: String,
        authResp: AuthTokenRespModel,
    ): Result<AuthTokenRespModel> {
        if (!checkMatchBranch(branch)) {
            resetLoginUser()
            return errorResult(
                code = "branch_mismatch",
                description = "พนักงานไม่มีสิทธิ์ ใช้งานสาขานี้\r\nปัจจุบันพนักงานมีข้อมูลเป็นสาขา $branch",
            )
        }
        configRepository.updateAuthToken(authResp)
        setLoginUser(username, branch)
        return Result.Success(authResp)
    }

    private fun handleAreaRoles(
        isRegenToken: Boolean,
        username: String,
        authResp: AuthTokenRespModel,
    ): Result<AuthTokenRespModel> {
        configRepository.updateAuthToken(authResp)
        return if (isRegenToken) {
            Result.Success(authResp)
        } else {
            resetLoginUser()
            errorResult(
                code = "auth_branch_required",
                description = "ข้อมูลสาขาไม่ถูกต้อง\r\nกรุณาติดต่อทีม IT Support\r\n(user: $username)",
            )
        }
    }

    private fun accessDenied(username: String): Result<AuthTokenRespModel> =
        errorResult(
            code = "access_denied",
            description = "คุณไม่มีสิทธิ์ใช้งาน\r\nกรุณาติดต่อทีม IT Support\r\n(user: $username)",
        )

    private fun invalidGroup(username: String): Result<AuthTokenRespModel> =
        errorResult(
            code = "invalid_group",
            description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (invalid group)\r\nกรุณาติดต่อทีม IT Support\r\n(user: $username)",
        )

    private fun groupRequired(username: String): Result<AuthTokenRespModel> =
        errorResult(
            code = "group_required",
            description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (group required)\r\nกรุณาติดต่อทีม IT Support\r\n(user: $username)",
        )

    private fun branchRequired(username: String): Result<AuthTokenRespModel> =
        errorResult(
            code = "branch_required",
            description = "ข้อมูลผู้ใช้งานไม่ครบถ้วน (branch required)\r\nกรุณาติดต่อทีม IT Support\r\n(user: $username)",
        )

    private fun exceptionError(username: String): Result<AuthTokenRespModel> =
        errorResult(
            code = "exception",
            description = "เกิดข้อผิดพลาดบางอย่าง\r\nกรุณาติดต่อทีม IT Support\r\n(user: $username)",
        )

    private fun errorResult(
        code: String,
        description: String,
    ): Result<AuthTokenRespModel> {
        val respModel = AuthTokenRespModel(error = code, description = description)
        return Result.Error(
            cause =
                BusinessException(
                    errorCode = respModel.error,
                    type = ErrorType.WARNING,
                    title = resourceUtils.getString(R.string.text_unable_to_continue),
                    message = respModel.description,
                ),
            data = respModel,
        )
    }

    private fun setLoginUser(
        username: String,
        branch: String,
    ) {
        appConfig.username = username

        /*spoVariable.USER_DATA = mdoTCNMUser_L2()
        spoVariable.USER_DATA.FTUsrCode(appConfig.username)
        spoVariable.USER_DATA.FTUsrName(appConfig.username)*/

        if (branch.isNotEmpty()) {
            val branchCode = appConfig.getBranchInfo()?.branchCode ?: ""
            if (branchCode.isEmpty()) {
                val branchInfo =
                    BranchInfoModel(
                        branchCode = branch,
                        branchName = branch,
                    )
                configRepository.updateBranchInfo(branchInfo)
            }
        }
        // spoVariable.USER_DATA.FTBchCode(appConfig.deviceConfig?.branchInfo?.branchCode)
    }

    private fun resetLoginUser() {
        appConfig.authToken = null
        appConfig.username = ""
        // spoVariable.USER_DATA = mdoTCNMUser_L2()
    }

    private fun checkMatchBranch(userBranch: String): Boolean {
        val mapBranch = configRepository.getAppConfig().getBranchInfo()?.branchCode ?: ""
        return mapBranch.isEmpty() || mapBranch == userBranch
    }

    fun setCrashlytics() {
        val username = appConfig.username ?: ""
        crashReporter.setUserId(username)

        val branchCodeKey = appConfig.getBranchInfo()?.branchCode ?: "N/A"
        crashReporter.setCustomKey("BranchCode", branchCodeKey)
    }
}
