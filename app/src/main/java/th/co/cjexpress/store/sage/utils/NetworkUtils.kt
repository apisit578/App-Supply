package th.co.cjexpress.store.sage.utils

import java.net.InetAddress
import java.net.NetworkInterface
import java.util.Collections

val deviceMacAddress: String by lazy { getMacAddress() }
val IPV4: String by lazy { getIPAddress(true) }

fun getIPAddress(useIPv4: Boolean): String {
    try {
        val interfaces: List<NetworkInterface> =
            Collections.list(NetworkInterface.getNetworkInterfaces())
        for (intf in interfaces) {
            val addrs: List<InetAddress> =
                Collections.list(intf.inetAddresses)
            for (addr in addrs) {
                if (!addr.isLoopbackAddress) {
                    val sAddr: String = addr.hostAddress ?: ""
                    // boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                    val isIPv4 = sAddr.indexOf(':') < 0
                    if (useIPv4) {
                        if (isIPv4) return sAddr
                    } else {
                        if (!isIPv4) {
                            val delim = sAddr.indexOf('%') // drop ip6 zone suffix
                            return if (delim < 0) {
                                sAddr.uppercase()
                            } else {
                                sAddr.substring(
                                    0,
                                    delim,
                                ).uppercase()
                            }
                        }
                    }
                }
            }
        }
    } catch (ignored: Exception) {
    } // for now eat exceptions
    return ""
}

fun getMacAddress(): String {
    try {
        val all = Collections.list(NetworkInterface.getNetworkInterfaces())
        for (networkInterface in all) {
            if (networkInterface.name.equals("wlan0", ignoreCase = true)) {
                val macBytes = networkInterface.hardwareAddress ?: return "??:??:??:??:??:??"
                return macBytes.joinToString(":") { "%02X".format(it) }
            }
        }
    } catch (e: Exception) {
        e.printStackTrace()
    }

    return "??:??:??:??:??:??"
}
