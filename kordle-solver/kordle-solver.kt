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
    // Init Dizionario (preso dal codice di Kordle)
    if (lingua == "e") resp =
        getTextFromUrl("https://raw.githubusercontent.com/charlesreid1/five-letter-words/master/sgb-words.txt") // Lista di parole di charlesreid1
    else resp =
        getTextFromUrl("https://gist.githubusercontent.com/Alchiter/6da661de5791d73c055bf09f7a21a5be/raw/cf1a02b83101c721e72450227e580cf2be5e6242/lista_parole_italiane_5_lettere.txt") // Lista caricata su GitHub da Alchiter, presa da https://www.listediparole.it/5lettereparole.htm
    var dict = resp.lines().sorted() // Split per newline, poi sorta.
    if (lingua == "i") dict =
        dict.map { it.lowercase() } // Per il dizionario inglese non è necessario (in quanto è già lowercase), per quello italiano sì.

    // Introduzione
    println("Kordle Solver, di Tedaldi Alessandro")
    println("${grigio}Inserisci le lettere verdi che hai ottenuto alle posizioni corrispondenti, inserisci \"-\" negli altri spazi. \nEsempio: ${verde}P${rosso}--${verde}la$reset -> ${verde}Palla${grigio}, ${verde}Perla${grigio}...$reset")
    print("\n")

    // Input
    var inputWord: String
    do {
        println("Inserisci le lettere, come indicato in precedenza.")
        inputWord = readLine()!!.lowercase()
        if (inputWord.length != 5) println("${rosso}Per favore, inserisci una parola da 5 lettere.$reset.")
    } while (inputWord.length != 5)

    /*
    Spiegazione veloce del programma:
    Il programma guarda ogni parola del dizionario selezionato, e la compara alla stringa data.
    Ogni entry nei dizionari hanno 5 lettere, quindi il programma impone quella lunghezza nell'input.
    Se ogni lettera i della stringa in input è uguale alla lettera i della parola j del dizionario, o è uguale a "-", la parola è valida e viene inserita in una lista di dimensione dinamica
     */

    val parolePossibili = ArrayList<String>()
    val control = arrayOf(1, 1, 1, 1, 1)
    var counterPossibili = 0

    var rightWord: String
    val checkInput = arrayOf(0, 0, 0, 0, 0)

    val dim = dict.size
    for (j in 1..dim - 1) { // Parte da 1 perché, per un problema con lo split e il sort dei dizionari, la prima entry è una stringa vuota
        rightWord = dict[j]
        for (i in 0..4) checkInput[i] = 0

        for (i in 0..4) if (inputWord[i] == rightWord[i] || inputWord[i] == '-') checkInput[i] = 1

        if (checkInput contentEquals control) {
            parolePossibili.add(rightWord)
            counterPossibili += 1
        }
    }

    if (counterPossibili != 0) {
        println("Sono state trovate $verde$counterPossibili$reset possibili parole:")
        for (i in 0..counterPossibili - 1) println(parolePossibili[i])
    } else println("${rosso}Non sono state parole che rispettano i tuoi criteri.$reset")
}
