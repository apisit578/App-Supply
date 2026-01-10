package th.co.cjexpress.store.sage.data.network

import com.chuckerteam.chucker.api.ChuckerInterceptor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import okhttp3.CacheControl
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Headers.Companion.toHeaders
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.Response
import th.co.cjexpress.store.sage.BuildConfig
import th.co.cjexpress.store.sage.constants.RestfulAuthType
import th.co.cjexpress.store.sage.di.AppDependencyProvider
import java.io.IOException
import java.util.concurrent.TimeUnit

class RestfulClient<T>(
    val url: String,
    private val body: Any? = null,
    private val isJsonBody: Boolean = false,
    private val mediaType: String = "application/json; charset=utf-8",
    private var headers: Map<String, String> = mapOf(),
    timeout: Long = 15000,
    private val name: String? = null,
) {
    private var client: OkHttpClient
    private val jsonUtils = AppDependencyProvider.getProviderBuilder().getJsonUtils()
    private val appConfig = AppDependencyProvider.getProviderBuilder().getAppConfig()
    private val oAuth2Manager = AppDependencyProvider.getProviderBuilder().getOAuth2Manager()
    private var retry401 = true

    init {
        var clientBuilder =
            OkHttpClient
                .Builder()
                .addInterceptor(ServiceInterceptor())
                .connectTimeout(timeout, TimeUnit.MILLISECONDS)
                .readTimeout(timeout, TimeUnit.MILLISECONDS)
                .writeTimeout(timeout, TimeUnit.MILLISECONDS)

        if (BuildConfig.FLAVOR.contains("DEV")) {
            clientBuilder.addInterceptor(
                ChuckerInterceptor(
                    AppDependencyProvider.getProviderBuilder().getContext(),
                ),
            )
        }

//        if (!DaggerManager.configLoaded || appConfig.httpsConfig.isApplySelfCertUrl(url)) {
//            clientBuilder = applySelfCert(clientBuilder)
//        }

        client = clientBuilder.build()
    }

    fun requestGet(): RestfulResponse<T> = requestSync("GET")

    fun requestPost(): RestfulResponse<T> = requestSync("POST")

    fun requestPut(): RestfulResponse<T> = requestSync("PUT")

    fun requestPatch(): RestfulResponse<T> = requestSync("PATCH")

    fun requestQueueGet(callback: RestfulCallback<T>) {
        requestQueue("GET", callback)
    }

    fun requestQueuePost(callback: RestfulCallback<T>) {
        requestQueue("POST", callback)
    }

    fun requestQueuePatch(callback: RestfulCallback<T>) {
        requestQueue("PATCH", callback)
    }

    fun requestQueuePut(callback: RestfulCallback<T>) {
        requestQueue("PUT", callback)
    }

    fun requestAsyncGet(): Deferred<RestfulResponse<T>> =
        CoroutineScope(Dispatchers.IO).async {
            return@async requestGet()
        }

    fun requestAsyncPost(): Deferred<RestfulResponse<T>> =
        CoroutineScope(Dispatchers.IO).async {
            return@async requestPost()
        }

    fun requestAsyncPut(): Deferred<RestfulResponse<T>> =
        CoroutineScope(Dispatchers.IO).async {
            return@async requestPut()
        }

    fun requestAsyncPatch(): Deferred<RestfulResponse<T>> =
        CoroutineScope(Dispatchers.IO).async {
            return@async requestPatch()
        }

    private fun requestSync(method: String): RestfulResponse<T> {
        try {
            val req = getRequest(method)
            val response = client.newCall(req).execute()
            val resp = extractResponse(response)
            resp.isRetry401 = !retry401
            logResponse(resp)
            return if (resp.httpStatus == 401) {
                process401Sync(method, resp)
            } else {
                resp
            }
        } catch (e: Exception) {
            val resp =
                RestfulResponse<T>(
                    body = null,
                    httpStatus = 0,
                    httpMessage = e.localizedMessage ?: "",
                    isSuccessful = false,
                    t = e,
                )
            logResponse(resp)
            return resp
        }
    }

    private fun requestQueue(
        method: String,
        callback: RestfulCallback<T>,
    ) {
        val req = getRequest(method)
        client.newCall(req).enqueue(
            object : Callback {
                override fun onFailure(
                    call: Call,
                    e: IOException,
                ) {
                    val resp =
                        RestfulResponse<T>(
                            body = null,
                            httpStatus = 0,
                            httpMessage = e.localizedMessage ?: "",
                            isSuccessful = false,
                            t = e,
                        )
                    // logResponse(resp)
                    callback.onResponse(resp)
                }

                override fun onResponse(
                    call: Call,
                    response: Response,
                ) {
                    val resp = extractResponse(response)
                    // logResponse(resp)

                    if (resp.httpStatus == 401) {
                        process401Queue(method, callback, resp)
                    } else {
                        callback.onResponse(resp)
                    }
                }
            },
        )
    }

    private fun getRequest(method: String): Request {
        val body = if (method == "GET") null else getBody()
        var request =
            Request
                .Builder()
                .url(url)
                .method(method, body)
                .cacheControl(CacheControl.Builder().noCache().build())
        if (headers.isNotEmpty()) {
            request = request.headers(headers.toHeaders())
        }

        return request.build()
    }

    private fun getBody(): RequestBody? {
        if (body != null) {
            return if (isJsonBody) {
                (body as String).toRequestBody(mediaType.toMediaType())
            } else {
                jsonUtils.toJson(body).toRequestBody(mediaType.toMediaType())
            }
        }
        return null
    }

    private fun extractResponse(response: Response): RestfulResponse<T> =
        RestfulResponse(
            body = response.body?.string(),
            httpStatus = response.code,
            httpMessage = response.message,
            isSuccessful = response.isSuccessful,
            isRedirect = response.isRedirect,
            headers = response.headers.toMap(),
            sentRequestAtMillis = response.sentRequestAtMillis,
            receivedResponseAtMillis = response.receivedResponseAtMillis,
        )

    private fun logResponse(response: RestfulResponse<T>) {
//        val appConfig = DaggerManager.getDaggerBuilder().getAppConfig()
//        if(DaggerManager.configLoaded && appConfig.payloadConfig.posLogger.getLogRestful(url)){
//            if (response.isSuccessful) {
//                logInfo("Restful_Request", tag = TAG_RESTFUL, response)
//            } else {
//                logWarning("Restful_Request", tag = TAG_RESTFUL, response)
//            }
//        }else{
//            if (response.isSuccessful) {
//                Timber.d("Restful_Request(isSuccessful=true) : ${jsonUtils.toJson(response)}")
//            } else {
//                Timber.w("Restful_Request(isSuccessful=false) : ${jsonUtils.toJson(response)}")
//            }
//        }
    }

    private fun process401Sync(
        method: String,
        resp: RestfulResponse<T>,
    ): RestfulResponse<T> {
        if (retry401 && name != null) {
            retry401 = false
            val token = refreshToken(name)
            token?.let {
                headers =
                    headers
                        .toMutableMap()
                        .also { h -> h["Authorization"] = "Bearer $token" }
                        .toMap()
                return requestSync(method)
            }
        }
        return resp
    }

    private fun process401Queue(
        method: String,
        callback: RestfulCallback<T>,
        resp: RestfulResponse<T>,
    ) {
        if (retry401 && name != null) {
            retry401 = false
            val token = refreshToken(name)
            token?.let {
                headers =
                    headers
                        .toMutableMap()
                        .also { h -> h["Authorization"] = "Bearer $token" }
                        .toMap()
                requestQueue(method, callback)
                return
            }
        }
        callback.onResponse(resp)
    }

    private fun refreshToken(serviceName: String): String? {
        val sConfig = appConfig.getServiceConfig(serviceName)
        sConfig?.let {
            if (it.authType == RestfulAuthType.OAUTH) {
                it.oauth?.let { oauth ->
                    return oAuth2Manager.getToken(oauth)
                }
            }
        }
        return null
    }
}
