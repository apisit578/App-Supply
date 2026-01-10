package th.co.cjexpress.store.sage.data.network

import android.util.Base64
import th.co.cjexpress.store.sage.constants.DEFAULT_REQUEST_TIMEOUT_MS
import th.co.cjexpress.store.sage.constants.MIN_REQUEST_TIMEOUT_MS
import th.co.cjexpress.store.sage.constants.RestfulAuthType
import th.co.cjexpress.store.sage.di.AppDependencyProvider

fun <T> getRestfulClient(
    serviceName: String,
    body: Any? = null,
    isJsonBody: Boolean = false,
    header: HashMap<String, String> = hashMapOf(),
    queryString: String = "",
    pathParams: HashMap<String, String> = hashMapOf(),
    timeout: Long? = null,
): RestfulClient<T>? {
    val appConfig = AppDependencyProvider.getProviderBuilder().getAppConfig()
    val oAuth2Manager = AppDependencyProvider.getProviderBuilder().getOAuth2Manager()

    val sConfig = appConfig.getServiceConfig(serviceName)
    return sConfig?.let {
        when (it.authType) {
            RestfulAuthType.OAUTH -> {
                it.oauth?.let { oauth ->
                    val token = oAuth2Manager.getToken(oauth)
                    header["Authorization"] = "Bearer $token"
                }
            }
            RestfulAuthType.BASIC -> {
                it.basic?.let { basic ->
                    val creds = "${basic.username}:${basic.password}"
                    val credsByteArray = creds.toByteArray(Charsets.UTF_8)
                    header["Authorization"] =
                        ("Basic " + Base64.encodeToString(credsByteArray, Base64.NO_WRAP))
                }
            }
            else -> {}
        }

        val finalTimeout = timeout ?: it.timeout?.toLong() ?: 15000
        return RestfulClient(
            url = it.url.replacePathParams(pathParams) + queryString,
            body = body,
            isJsonBody = isJsonBody,
            headers = header,
            timeout = finalTimeout,
            name = serviceName,
        )
    }
}

fun String.replacePathParams(pathParams: Map<String, String>): String {
    var updatedUrl = this
    pathParams.forEach { (key, value) ->
        updatedUrl = updatedUrl.replace("{$key}", value)
    }
    return updatedUrl
}

fun getSafeRequestTimeout(timeout: Int?): Int {
    val safeTimeout = ((timeout ?: DEFAULT_REQUEST_TIMEOUT_MS) - MIN_REQUEST_TIMEOUT_MS)
        .coerceAtLeast(MIN_REQUEST_TIMEOUT_MS)
    return safeTimeout
}
