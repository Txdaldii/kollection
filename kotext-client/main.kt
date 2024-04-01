import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.Socket
import kotlin.system.exitProcess

fun main() {
    println("Kotext (client), di Tedaldi Alessandro")
    println("Inserisci l'indirizzo IP del server.")
    val serverAddress = readLine()!!
    val serverPort = 12345

    println("Inserisci il tuo username:")
    val username = readLine()

    try {
        val socket = Socket(serverAddress, serverPort)
        println("Connessione al server riuscita.")
        println("Scrivi \"file::/path/to/file.*\" per ottenere un link ad un tuo file per condividerlo qui!")

        val reader = BufferedReader(InputStreamReader(socket.getInputStream()))
        val writer = PrintWriter(socket.getOutputStream(), true)

        val userInput = BufferedReader(InputStreamReader(System.`in`))

        // Invia l'username al server
        writer.println(username)

        val serverListener = Thread {
            // Attendiamo che il server confermi l'accettazione dell'username
            reader.readLine()

            while (true) {
                try {
                    val serverMessage = reader.readLine()
                    println(serverMessage)
                } catch (e: java.net.SocketException) {
                    println("Connessione al server persa.")
                    exitProcess(-1)
                }
            }
        }

        serverListener.start()

        var userInputLine: String?
        while (userInput.readLine().also { userInputLine = it } != null) {
            if ("file::" in userInputLine!!) getFileLink(userInputLine!!.removeRange(0, 6))
            else writer.println(userInputLine)
        }
    } catch (e: Exception) {
        println("Errore durante la connessione al server: ${e.message}")
    }
}
