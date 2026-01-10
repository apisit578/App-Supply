package th.co.cjexpress.store.sage.data.model

import android.os.Build
import com.google.gson.annotations.SerializedName
import th.co.cjexpress.store.sage.BuildConfig
import th.co.cjexpress.store.sage.constants.LogAction
import th.co.cjexpress.store.sage.constants.LogLevel
import th.co.cjexpress.store.sage.utils.IPV4
import th.co.cjexpress.store.sage.utils.UUIDUtils.getCurrentUUID
import th.co.cjexpress.store.sage.utils.deviceMacAddress
import java.util.Calendar
import java.util.Date

data class HandheldLogModel(
    @SerializedName("level")
    val level: LogLevel,
    @SerializedName("action")
    val action: LogAction,
    @SerializedName("username")
    val username: String,
    @SerializedName("branchCode")
    val branchCode: String,
    @SerializedName("appScreen")
    var appScreen: String? = null,
) {
    @SerializedName("appName")
    val appName: String = "SAGE - Store Admin Guided Efficiently"

    @SerializedName("appVersion")
    val appVersion: String = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    @SerializedName("createAt")
    val createAt: Date = Calendar.getInstance().time

    @SerializedName("deviceIp")
    val deviceIp: String = IPV4

    @SerializedName("macAddress")
    val macAddress: String = deviceMacAddress

    @SerializedName("packageName")
    val packageName: String = BuildConfig.APPLICATION_ID

    @SerializedName("url")
    var url: String = ""

    @SerializedName("message")
    var message: String = ""

    @SerializedName("model")
    val model: String = "${Build.MODEL} - ${Build.BRAND}"

    @SerializedName("osVersion")
    val osVersion: String? = Build.VERSION.RELEASE

    @SerializedName("uuid")
    val uuid: String? = getCurrentUUID()

    private fun addExpiryAt(): Date {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DATE, 30)
        return calendar.time
    }
}
