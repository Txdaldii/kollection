/*
    Documentazione: https://github.com/Lioydiano/Classeviva-Official-Endpoints/tree/master
    Ringrazio FLAK-ZOSO (@flak_flak) per avermi aiutato con l'header delle richieste e lo studentId
 */

fun main() {
    println("Klasse, di Tedaldi Alessandro")
    val token = login()!!

    print("\n")
    do {
        println("Cosa vuoi fare?\n1. Voti\n2. Compiti\n3. Media voti\n4. Assenze\n5. Lezioni\n\nAltro: Esci")
        var sel = readln().toIntOrNull() ?: 0
        when (sel) {
            1 -> getVoti(token)
            2 -> getCompiti(token)
            3 -> calcolaMedie(token)
            4 -> getAssenze(token)
            5 -> getLezioni(token)
            else -> sel = -1
        }
    } while (sel != -1)
    println("\nArrivederci!")
}
