import kotlin.math.max
import kotlin.math.min

fun creaGriglia(): Array<Array<Char>> = Array(3) { Array(3) { '-' } }

fun stampaGriglia(griglia: Array<Array<Char>>) {
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            print("${griglia[i][j]} ")
        }
        print("\n")
    }
    print("\n")
}

fun mossaValida(griglia: Array<Array<Char>>, pos: Int): Boolean {
    val x = (pos - 1) / 3
    val y = (pos - 1) % 3
    return pos in 1..9 && griglia[x][y] == '-'
}

fun faiMossa(griglia: Array<Array<Char>>, player: Char, pos: Int) {
    val x = (pos - 1) / 3
    val y = (pos - 1) % 3
    griglia[x][y] = player
}

fun giocoFinito(griglia: Array<Array<Char>>): Boolean {
    return ottieniVincitore(griglia) != '-' || griglia.all { row -> row.all { cell -> cell != '-' } }
}

fun ottieniVincitore(griglia: Array<Array<Char>>): Char {
    for (i in 0 until 3) {
        // Check rows
        if (griglia[i][0] != '-' && griglia[i][0] == griglia[i][1] && griglia[i][0] == griglia[i][2]) {
            return griglia[i][0]
        }
        // Check columns
        if (griglia[0][i] != '-' && griglia[0][i] == griglia[1][i] && griglia[0][i] == griglia[2][i]) {
            return griglia[0][i]
        }
    }
    // Check diagonals
    if (griglia[0][0] != '-' && griglia[0][0] == griglia[1][1] && griglia[0][0] == griglia[2][2]) {
        return griglia[0][0]
    }
    if (griglia[0][2] != '-' && griglia[0][2] == griglia[1][1] && griglia[0][2] == griglia[2][0]) {
        return griglia[0][2]
    }
    return '-'
}

fun minimax(griglia: Array<Array<Char>>, depth: Int, isMaximizing: Boolean): Int {
    val winner = ottieniVincitore(griglia)
    if (winner != '-') {
        return if (winner == 'X') 1 else -1
    }
    if (giocoFinito(griglia)) {
        return 0
    }
    return if (isMaximizing) {
        var migliorPunteggio = Int.MIN_VALUE
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val position = i * 3 + j + 1
                if (mossaValida(griglia, position)) {
                    faiMossa(griglia, 'X', position)
                    val score = minimax(griglia, depth + 1, false)
                    faiMossa(griglia, '-', position)
                    migliorPunteggio = max(migliorPunteggio, score)
                }
            }
        }
        migliorPunteggio
    } else {
        var migliorPunteggio = Int.MAX_VALUE
        for (i in 0 until 3) {
            for (j in 0 until 3) {
                val position = i * 3 + j + 1
                if (mossaValida(griglia, position)) {
                    faiMossa(griglia, 'O', position)
                    val punteggio = minimax(griglia, depth + 1, true)
                    faiMossa(griglia, '-', position)
                    migliorPunteggio = min(migliorPunteggio, punteggio)
                }
            }
        }
        migliorPunteggio
    }
}

fun trovaMossaMigliore(griglia: Array<Array<Char>>): Int {
    var migliorPunteggio = Int.MIN_VALUE
    var mossaMigliore = -1
    for (i in 0 until 3) {
        for (j in 0 until 3) {
            val pos = i * 3 + j + 1
            if (mossaValida(griglia, pos)) {
                faiMossa(griglia, 'X', pos)
                val score = minimax(griglia, 0, false)
                faiMossa(griglia, '-', pos)
                if (score > migliorPunteggio) {
                    migliorPunteggio = score
                    mossaMigliore = pos
                }
            }
        }
    }
    return mossaMigliore
}

fun main() {
    println("Dare una mossa in input:\n1 2 3\n4 5 6\n7 8 9\n")
    val griglia = creaGriglia()

    stampaGriglia(griglia)

    while (!giocoFinito(griglia)) {
        println("Inserisci la tua mossa (da 1 a 9): ")
        var pos = readLine()!!.toIntOrNull() ?: -1
        while (!mossaValida(griglia, pos)) {
            println("Mossa non valida, riprova.")
            println("Inserisci la tua mossa (da 1 a 9): ")
            pos = readLine()!!.toInt()
        }
        faiMossa(griglia, 'O', pos)
        stampaGriglia(griglia)

        if (giocoFinito(griglia)) break

        val mossaMigliore = trovaMossaMigliore(griglia)
        faiMossa(griglia, 'X', mossaMigliore)
        stampaGriglia(griglia)
    }

    when (ottieniVincitore(griglia)) {
        'X' -> println("Hai perso!")
        'O' -> println("Hai vinto!")
        else -> println("Pareggio!")
    }
}
