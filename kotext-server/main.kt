import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class ChatServer() {
    private val clients = ConcurrentHashMap<String, PrintWriter>()

    fun start() {
        var serverSocket: ServerSocket
        while (true) {
            val ip = getIP() // Da getip.kt
            var port: Int
            do {
                print("Scegli una porta: ")
                port = readln().toIntOrNull() ?: 12345
            } while (port < 0 || port > 65535)
            try {
                serverSocket = ServerSocket(port)
                println("Server avviato sull'IP $ip, sulla porta $port")
                break
            } catch (e: java.net.BindException) {
                println("Indirizzo già in uso.")
            }
        }

        while (true) {
            val clientSocket = serverSocket.accept()
            println("Nuova connessione da ${clientSocket.inetAddress.hostAddress}")
            val clientHandler = ClientHandler(clientSocket)
            clientHandler.start()
        }
    }

    inner class ClientHandler(private val socket: Socket) : Thread() {
        private lateinit var reader: BufferedReader
        private lateinit var writer: PrintWriter
        private lateinit var originalUsername: String
        private var username: String = ""

        override fun run() {
            try {
                reader = BufferedReader(InputStreamReader(socket.getInputStream()))
                writer = PrintWriter(socket.getOutputStream(), true)

                // Richiedi all'utente di inserire il nome utente
                writer.println("Inserisci il tuo username:")
                originalUsername = reader.readLine()

                synchronized(clients) {
                    username = generateUniqueUsername(originalUsername)
                    clients[username] = writer
                }
                broadcast("$username si unisce alla chat")

                var inputLine: String?
                while (reader.readLine().also { inputLine = it } != null) {
                    broadcast("[$username]: $inputLine")
                }
            } catch (e: Exception) {
                println("Errore nella gestione del client: ${e.message}")
            } finally {
                synchronized(clients) {
                    clients.remove(username)
                    broadcast("$username ha abbandonato la chat")
                }
                socket.close()
            }
        }

        private fun generateUniqueUsername(originalUsername: String): String {
            var modifiedUsername = originalUsername
            var counter = 1
            while (clients.containsKey(modifiedUsername)) {
                modifiedUsername = "$originalUsername$counter"
                counter++
            }
            return modifiedUsername
        }
    }

    private fun broadcast(message: String) {
        synchronized(clients) {
            clients.forEach { (_, writer) -> writer.println(message) }
        }
    }
}

fun main() {
    println("Kotext (server), di Tedaldi Alessandro")
    val server = ChatServer()
    server.start()
}
