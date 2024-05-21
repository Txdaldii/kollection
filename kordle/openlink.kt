import java.awt.Desktop
import java.net.URI

fun getLink(parola: String): String {
    return "https://www.google.com/search?q=$parola+definizione"
}

fun openLink(parola: String) {
    val url = getLink(parola)
    if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.BROWSE)) {
        Desktop.getDesktop().browse(URI(url))
    } else println("Apri questo link in un browser: $url")
}
