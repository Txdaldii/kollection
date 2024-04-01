import java.net.URL
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.HttpURLConnection

fun getTextFromUrl(urlString: String): String {
    val url = URL(urlString)
    val connection = url.openConnection() as HttpURLConnection
    connection.connect()

    val inputStream = connection.inputStream
    val reader = BufferedReader(InputStreamReader(inputStream))

    val stringBuilder = StringBuilder()
    var line: String?
    while (reader.readLine().also { line = it } != null) {
        stringBuilder.append(line).append("\n")
    }

    reader.close()
    return stringBuilder.toString()
}

fun main() {
    // Dichiara Colori
    val reset = "\u001b[0m"
    val grigio = "\u001b[37m"
    val grigioScuro = "\u001b[90m"
    val giallo = "\u001b[93m"
    val verde = "\u001b[92m"
    val rosso = "\u001b[91m"

    var lingua: String

    // Selezione lingua
    do {
        println("Seleziona una lingua (i: italiano, e = english)\nN.B: Va a modificare solo il dizionario, non i print.")
        lingua = readLine()!!.lowercase()
        if (lingua != "i" && lingua != "e") println("Per favore inserisci 'i' o 'e'.")
    } while (lingua != "i" && lingua != "e")

    var resp: String
    // Init Dizionario
    if (lingua == "e") resp = getTextFromUrl("https://raw.githubusercontent.com/charlesreid1/five-letter-words/master/sgb-words.txt") // Lista di parole di charlesreid1
    else resp = getTextFromUrl("https://gist.githubusercontent.com/Alchiter/6da661de5791d73c055bf09f7a21a5be/raw/cf1a02b83101c721e72450227e580cf2be5e6242/lista_parole_italiane_5_lettere.txt") // Lista caricata su GitHub da Alchiter, presa da https://www.listediparole.it/5lettereparole.htm
    var dict = resp.lines().sorted() // Split per newline, poi sorta.
    if (lingua == "i") dict = dict.map {it.lowercase()} // Per il dizionario inglese non è necessario (in quanto è già lowercase), per quello italiano sì.

    // Introduzione
    println("Kordle, di Tedaldi Alessandro")
    println("${grigio}Come funziona?\nUna parola da 5 lettere viene generata, indovinala in meno tentativi possibili! (Max. 6)\n${verde}Verde$grigio = presente nel posto corretto\n${giallo}Giallo$grigio = presente nel posto sbagliato\n${grigioScuro}Grigio$grigio = assente$reset")

    var rigioca = ""

    do {
        // Generazione Parola
        val dictLen = dict.size
        val wordNumber = (0..dictLen - 1).random()
        val rightWord = dict[wordNumber]

        // Counter di tentativi
        var l = 0

        // Dichiarazione Array
        val checkInput = IntArray(5)
        val checkRight = IntArray(5)
        val output = arrayOf("", "", "", "", "")

        var win = false

        // Logica di gioco
        while (true) {
            for (i in 0..4) checkInput[i] = 0
            for (i in 0..4) checkRight[i] = 0

            l += 1
            println("Tentativo n. $l")
            println("Scegli una parola da 5 lettere.")

            var inputWord = readLine()!!.lowercase()
            var valid = false

            if (inputWord in dict) valid = true
            if (inputWord == "") valid = false

            while (valid != true) {
                println("Parola invalida. Scegli un'altra parola.")
                inputWord = readLine()!!.lowercase()

                if (inputWord in dict) valid = true
            }

            for (i in 0..4) {
                if (inputWord[i] == rightWord[i]) {
                    checkInput[i] = 2
                    checkRight[i] = 2
                }
            }

            for (i in 0..4) {
                for (j in 0..4) {
                    if (checkInput[i] != 0 || checkRight[j] != 0) continue
                    if (inputWord[i] == rightWord[j]) {
                        checkInput[i] = 1
                        checkRight[j] = 1
                    }
                }
            }

            for (i in 0..4) {
                if (checkInput[i] == 2) output[i] = "$verde${inputWord[i]}$reset"
                else if (checkInput[i] == 1) output[i] = "$giallo${inputWord[i]}$reset"
                else output[i] = "$grigioScuro${inputWord[i]}$reset"
            }

            println("-~-~-~-")
            for (i in 0..4) print("${output[i]}")
            print("\n")
            println("-~-~-~-")

            if (inputWord == rightWord) {
                win = true; break
            }
            if (l == 6) break
        }

        if (!win) println("Hai perso! La risposta era $rosso$rightWord$reset!")
        else {
            if (l == 1) println("Ce l'hai fatta in un tentativo!")
            else println("Ce l'hai fatta in $l tentativi!")
        }

        println("Scrivi 1 per rigiocare.")
        rigioca = readLine()!!
    } while (rigioca == "1")
}
