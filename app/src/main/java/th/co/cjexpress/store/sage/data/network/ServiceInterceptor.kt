package th.co.cjexpress.store.sage.data.network

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import th.co.cjexpress.store.sage.presentation.state.auth.AuthState
import timber.log.Timber
import java.net.SocketTimeoutException

class ServiceInterceptor(
//    private val addAuthToken: Boolean = true,
//    private var branchCode: String? = null,
//    private val token: String? = null
) : Interceptor {
    private val appConfig = AppDependencyProvider.getProviderBuilder().getAppConfig()
    private val logUtils = AppDependencyProvider.getProviderBuilder().getLogManager()

    override fun intercept(chain: Interceptor.Chain): Response {
        var url = ""
        var logCode = 0
        var logMessage = ""

        try {
            var request = chain.request()
            url = request.url.toString()

//            if (branchCode == null) {
//                branchCode = appConfig.deviceConfig?.branchInfo?.branchCode ?: ""
//            }

            val buffer = Buffer()
            request.body?.writeTo(buffer)

            val body: String = buffer.readUtf8()
//            if (body?.isEmpty() == true) {
//                body = null
//            }
            Timber.tag("DEV").d("[API] request: ${request.method} - ${request.url}, body: $body")

//            if (request.header("No-Authentication") == null) {
//                if (addAuthToken) {
//                    val finalToken = "Bearer ${token ?: accessToken}"
//                    request = request.newBuilder()
//                        .addHeader("Authorization", finalToken)
//                        .addHeader("branch", branchCode!!)
//                        .addHeader("userName", appConfig.username)
//                        .addHeader("deviceID", appConfig.androidId)
//                        .build()
//                }
//            }
            request.headers
                .names()
                .filter { name -> name != "Authorization" }
                .joinToString { key ->
                    val value = request.header(key).orEmpty()
                    "$key = $value"
                }.takeIf { it.isNotEmpty() }
                ?.let { headerLog ->
                    Timber.tag("DEV").d("[API] header: $headerLog")
                }

            val response = chain.proceed(request)
            logCode = response.code

            val logBody = response.peekBody(Long.MAX_VALUE)
            logMessage = logBody.string()

            val msg = "[API] response code: $logCode, message: $logMessage"
            Timber.tag("DEV").d(msg)

            if (logCode != 200) {
                logUtils.logError(
                    action = LogAction.REQUEST_API,
                    username = appConfig.username.orEmpty(),
                    url = request.url.toString(),
                    message = msg,
                )
            } else {
                logUtils.logInfo(
                    action = LogAction.REQUEST_API,
                    username = appConfig.username.orEmpty(),
                    message = logMessage,
                    url = request.url.toString(),
                )
            }

            if (logCode == 401 && logMessage.contains("expire", ignoreCase = true)) {
                AuthState.emitUnauthorizedEvent(true)
            }

            return response
        } catch (ex: SocketTimeoutException) {
            val msg = "[API] response error SocketTimeoutException: ${ex.message}"
            Timber.tag("DEV").d(msg)

            logUtils.logError(
                action = LogAction.REQUEST_API,
                username = appConfig.username.orEmpty(),
                url = url,
                message = msg,
            )

            throw ex
        } catch (ex: Exception) {
            val msg = "[API] response error Exception: ${ex.message}"
            Timber.tag("DEV").d(msg)

            logUtils.logError(
                action = LogAction.REQUEST_API,
                username = appConfig.username.orEmpty(),
                url = url,
                message = msg,
            )

            throw ex
        }
    }
}
