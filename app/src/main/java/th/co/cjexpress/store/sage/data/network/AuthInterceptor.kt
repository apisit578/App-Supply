package th.co.cjexpress.store.sage.data.network

import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import timber.log.Timber
import java.net.SocketTimeoutException

class AuthInterceptor : Interceptor {
    private val appConfig = AppDependencyProvider.getProviderBuilder().getAppConfig()
    private val logUtils = AppDependencyProvider.getProviderBuilder().getLogManager()

    override fun intercept(chain: Interceptor.Chain): Response {
        var url = ""
        var logCode = 0
        var logMessage = ""

        try {
            val request = chain.request()
            url = request.url.toString()
            Timber.tag("DEV").d("[AUTH] request: ${request.method} - $url")

            val buffer = Buffer()
            request.body?.writeTo(buffer)

            val response = chain.proceed(request)
            logCode = response.code
            logMessage = response.message

            val msg = "[AUTH] response code: $logCode, message: $logMessage"
            Timber.tag("DEV").d(msg)

            if (logCode != 200) {
                logUtils.logWarning(
                    action = LogAction.AUTH_REQUEST,
                    username = appConfig.username.orEmpty(),
                    url = request.url.toString(),
                    message = msg,
                )
            }

            return response
        } catch (ex: SocketTimeoutException) {
            val msg = "[AUTH] response error SocketTimeoutException: ${ex.message}"
            Timber.tag("DEV").d(msg)

            logUtils.logError(
                action = LogAction.AUTH_REQUEST,
                username = appConfig.username.orEmpty(),
                url = url,
                message = msg,
            )

            throw ex
        } catch (ex: Exception) {
            val msg = "[AUTH] response error Exception: ${ex.message}"
            Timber.tag("DEV").d(msg)

            logUtils.logError(
                action = LogAction.AUTH_REQUEST,
                username = appConfig.username.orEmpty(),
                url = url,
                message = msg,
            )

            throw ex
        }
    }
}
