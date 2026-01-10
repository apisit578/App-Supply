package th.co.cjexpress.store.sage.data.manager

import ca.mimic.oauth2library.OAuth2Client
import ca.mimic.oauth2library.OAuthResponse
import com.google.gson.JsonParser
import okhttp3.OkHttpClient
import org.json.JSONException
import org.json.JSONObject
import th.co.cjexpress.store.sage.constants.Logging
import th.co.cjexpress.store.sage.constants.ServiceConstants.SERVICE_KEYCLOAK
import th.co.cjexpress.store.sage.data.config.AppConfig
import th.co.cjexpress.store.sage.data.network.AuthInterceptor
import th.co.cjexpress.store.sage.domain.model.OAuthTokenModel
import timber.log.Timber
import java.net.SocketTimeoutException
import java.util.Date
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OAuth2Manager
    @Inject
    constructor(
        private val appConfig: AppConfig,
    ) {
        private val tokenMap = hashMapOf<String, OAuthTokenModel>()

        fun getToken(key: String): String? =
            if (!tokenMap.containsKey(key) || tokenMap[key]!!.isExpired()) {
                requestToken(key)
            } else {
                tokenMap[key]?.token
            }

        private fun requestToken(key: String): String? {
            val oauthConfig = appConfig.getOAuthConfig(key)
            if (oauthConfig != null) {
                val grantType = oauthConfig.grantType
                val clientID = oauthConfig.clientId
                val clientSecret = oauthConfig.clientSecret
                val site = oauthConfig.url

                var respBody: String? = null
                try {
                    val client =
                        OAuth2Client
                            .Builder(clientID, clientSecret, site)
                            .grantType(grantType)
                            .build()
                    val response = client.refreshAccessToken("refresh-token")
                    respBody = response.body
                    val expires = response.expiresIn

                    val tokenModel =
                        OAuthTokenModel(
                            name = key,
                            token = response.accessToken,
                            expired = Date(Date().time + expires * 1000),
                        )
                    tokenMap[key] = tokenModel
                    // TODO write log
                    return tokenModel.token
                } catch (e: Exception) {
                    val error = if (!isJsonValid(respBody)) respBody else e
                    Timber.tag(Logging.TAG_AUTH).e("auth error, $error")
//                logError(
//                    action = "authMemberError",
//                    tag = TAG_MEMBERSHIP,
//                    data = error
//                )
                    return null
                }
            } else {
                return null
            }
        }

        fun requestAuthToken(
            username: String = "",
            password: String = "",
        ): OAuthResponse? {
            val oauthConfig =
                appConfig.getOAuthConfig(SERVICE_KEYCLOAK)
                    ?: throw Exception("OAuth config not found for key: $SERVICE_KEYCLOAK")
            val grantType = oauthConfig.grantType
            val clientId = oauthConfig.clientId
            val clientSecret = oauthConfig.clientSecret
            val timeout = oauthConfig.timeout ?: 300
            val site = oauthConfig.url

            val okHttpClient =
                OkHttpClient
                    .Builder()
                    .addInterceptor(AuthInterceptor())
                    .connectTimeout(timeout, TimeUnit.SECONDS)
                    .readTimeout(timeout, TimeUnit.SECONDS)
                    .writeTimeout(timeout, TimeUnit.SECONDS)
                    .build()

            return try {
                val client =
                    OAuth2Client
                        .Builder(username, password, clientId, clientSecret, site)
                        .grantType(grantType)
                        .okHttpClient(okHttpClient)
                        .build()

                client.requestAccessToken()
            } catch (e: SocketTimeoutException) {
                throw Exception("Request timed out after $timeout seconds", e)
            } catch (e: Exception) {
                throw Exception("Failed to request AD token: ${e.message}", e)
            }
        }

        fun isJsonValid(jsonString: String?): Boolean {
            if (jsonString.isNullOrEmpty()) {
                return false
            }
            return try {
                JsonParser.parseString(jsonString)
                true
            } catch (e: Exception) {
                false
            }
        }

        fun getErrorDescription(jsonString: String): String? =
            try {
                val jsonObject = JSONObject(jsonString)
                jsonObject.getString("error_description")
            } catch (e: JSONException) {
                null
            } catch (e: Exception) {
                null
            }
    }
