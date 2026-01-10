package th.co.cjexpress.store.sage.data.network

import th.co.cjexpress.store.sage.di.AppDependencyProvider
import java.io.Serializable
import java.util.Date

class RestfulResponse<T>(
    val body: String?,
    val httpStatus: Int,
    val isSuccessful: Boolean,
    val isRedirect: Boolean = false,
    val httpMessage: String = "",
    val headers: Map<String, String> = mapOf(),
    val sentRequestAtMillis: Long = 0,
    val receivedResponseAtMillis: Long = 0,
    var t: Throwable? = null,
) : Serializable {
    var isRetry401 = false
    var bodyObject: T? = null

    val logMsg: HashMap<String, Any?>
        get() {
            val map = logMsgNoBody
            map["body"] = body
            return map
        }

    val logMsgNoBody: HashMap<String, Any?>
        get() {
            val map = HashMap<String, Any?>()
            map["isSuccessful"] = isSuccessful
            map["httpStatus"] = httpStatus
            map["httpMessage"] = httpMessage
            map["t"] = t
            map["sendRequestAt"] = getSentRequestAtMillisDate()
            map["receiveResponseAt"] = getReceivedResponseAtMillisDate()
            map["responseReceive"] = Date()
            return map
        }

    fun bodyToObject(clazz: Class<T>): T? {
        if (body != null) {
            bodyObject =
                AppDependencyProvider.getProviderBuilder().getJsonUtils().fromJson(body, clazz)
            return bodyObject
        }
        return null
    }

    fun getSentRequestAtMillisDate(): Date {
        if (sentRequestAtMillis == 0L) {
            return Date()
        }
        return Date(sentRequestAtMillis)
    }

    fun getReceivedResponseAtMillisDate(): Date {
        if (receivedResponseAtMillis == 0L) {
            return Date()
        }
        return Date(receivedResponseAtMillis)
    }
}
