package th.co.cjexpress.store.sage.data.source.remote

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import th.co.cjexpress.store.sage.BuildConfig
import th.co.cjexpress.store.sage.data.config.ConfigLoader
import th.co.cjexpress.store.sage.data.network.RestfulClient
import th.co.cjexpress.store.sage.data.network.RestfulResponse
import th.co.cjexpress.store.sage.domain.model.payload.PayloadConfig

class PayloadApi(
    private val configLoader: ConfigLoader,
) {
    suspend fun loadConfigByBranch(branchCode: String): RestfulResponse<PayloadConfig> {
        val headers = hashMapOf<String, String>()
        headers["Authorization"] = "User API-Key ${BuildConfig.PAYLOAD_API_KEY}"

        val url = "${configLoader.jsonConfig.url}$branchCode.json"
        return withContext(Dispatchers.IO) {
            val req =
                RestfulClient<PayloadConfig>(
                    url = url,
                    headers = headers,
                    timeout = configLoader.jsonConfig.configTimeout,
                )
            req.requestGet()
        }
    }
}
