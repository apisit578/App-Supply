package th.co.cjexpress.store.sage.data.repository

import ca.mimic.oauth2library.OAuthResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import th.co.cjexpress.store.sage.data.manager.OAuth2Manager
import th.co.cjexpress.store.sage.data.model.auth.AuthTokenRespModel
import th.co.cjexpress.store.sage.domain.callback.AuthTokenCallback
import th.co.cjexpress.store.sage.domain.repository.LoginRepository
import th.co.cjexpress.store.sage.utils.JsonUtils

class LoginRepositoryImpl(
    private val oAuth2Manager: OAuth2Manager,
    private val jsonUtils: JsonUtils,
) : LoginRepository {
    override suspend fun getAuthToken(
        username: String,
        password: String,
        callback: AuthTokenCallback,
    ) {
        withContext(Dispatchers.IO) {
            try {
                val response = oAuth2Manager.requestAuthToken(username, password)
                    ?: return@withContext callback.onAuthError(
                        AuthTokenRespModel(
                            error = "bad_request",
                            description = "ไม่สามารถเชื่อมต่อกับเซิฟเวอร์ได้",
                        )
                    )

                if (response.isSuccessful && response.code == 200) {
                    val authResp = jsonUtils.fromJson(
                        response.body,
                        AuthTokenRespModel::class.java
                    )
                    callback.onAuthSuccess(authResp)
                    return@withContext
                }

                val errorModel = buildErrorModel(response)
                callback.onAuthError(errorModel)
            } catch (e: Exception) {
                callback.onAuthError(
                    AuthTokenRespModel(
                        error = "exception",
                        description = "เกิดข้อผิดพลาดในขั้นตอนการยืนยันสิทธิ์\r\n(${e.message})",
                    )
                )
            }
        }
    }

    private fun buildErrorModel(response: OAuthResponse): AuthTokenRespModel {
        val description = when (response.isSuccessful) {
            true -> {
                "เกิดข้อผิดพลาดในขั้นตอนการยืนยันสิทธิ์\\r\\n(code != 200)"
            }

            else -> {
                when (response.code) {
                    400 -> "ระบบไม่พร้อมใช้งาน"
                    401 -> "ชื่อผู้ใช้หรือรหัสผ่านไม่ถูกต้อง"
                    else -> {
                        val msg = buildString {
                            append("code:${response.code}")
                            response.oAuthError?.errorException?.message?.takeIf { it.isNotEmpty() }
                                ?.let { append(" -> $it") }
                        }
                        "เกิดข้อผิดพลาดในขั้นตอนการยืนยันสิทธิ์\\r\\n($msg)"
                    }
                }
            }
        }

        return AuthTokenRespModel(
            error = response.code.toString(),
            description = description
        )
    }
}
