// Codice di Rajendhiran Easu per prendere l'indirizzo IP
// Questo codice viene usato in main.kt

import java.net.NetworkInterface
import java.util.*

object IPHelper {
    val IPV4Address: String
        get() = getIPAddress(true)

    val IPV6Address: String
        get() = getIPAddress(false)

    /*
     * Get IP address from first non-localhost interface
     * @param userIPV4 true=return ipv4, false=return ipv6
     * @return address or empty string
     */
    private fun getIPAddress(userIPV4: Boolean): String {
        try {
            Collections.list(NetworkInterface.getNetworkInterfaces()).forEach {
                Collections.list(it.inetAddresses).forEach {
                    if (!it.isLoopbackAddress) {
                        val sAddr = it.hostAddress
                        val isIPv4 = sAddr.indexOf(':') < 0
                        if (userIPV4) {
                            if (isIPv4) {
                                return sAddr
                            }
                        } else {
                            if (!isIPv4) {
                                val delim = sAddr.indexOf('%')
                                return if (delim < 0) sAddr.toUpperCase() else sAddr.substring(0, delim).toUpperCase()
                            }
                        }
                    }
                }
            }
        } catch (ignored: Exception) {
        }
        return ""
    }
}
