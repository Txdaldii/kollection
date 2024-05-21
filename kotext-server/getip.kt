// Ispirazione da https://gist.github.com/NanoSpicer/231995bf7af8e57e9705f79d59277227

import java.net.NetworkInterface

fun getLocalIpList(): MutableList<String> {
    val l = NetworkInterface
        .getNetworkInterfaces()
        .toList()
        .flatMap { it.inetAddresses.toList() }
    val l2: MutableList<String> = mutableListOf()
    for (i in l.indices) if (!l[i].toString().startsWith("/fe") && !l[i].toString().startsWith("/0:0")) l2.add(l[i].toString().drop(1))
    return l2
}

fun getIP(): String {
    val ipList = getLocalIpList()
    var scelta: Int

    do {
        println("Scegli un indirizzo IP:")
        for (i in ipList.indices) println("${i + 1}. ${ipList[i]}")
        scelta = readln().toIntOrNull() ?: 0
    } while (scelta - 1 !in ipList.indices)

    return ipList[scelta - 1]
}
