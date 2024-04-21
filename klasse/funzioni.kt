import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.text.DecimalFormat
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter

fun sortJSONArrayByKeyStr(json: JSONArray, key: String): JSONArray {
    val jsonList = mutableListOf<JSONObject>()
    for (i in 0..< json.length()) jsonList.add(json.getJSONObject(i))
    jsonList.sortByDescending { it.getString(key) }
    return JSONArray(jsonList)
}

fun sortJSONArrayByKeyDouble(json: JSONArray, key: String): JSONArray {
    val jsonList = mutableListOf<JSONObject>()
    for (i in 0..< json.length() - 1) jsonList.add(json.getJSONObject(i))
    jsonList.sortByDescending { it.get(key).toString().toDoubleOrNull() }
    return JSONArray(jsonList)
}

fun req(url: String, token: String): String {
    val client = OkHttpClient()

    val request = Request.Builder()
        .url("https://web.spaggiari.eu/rest/$url")
        .addHeader("ContentsDiary-Type", "application/json")
        .addHeader("User-Agent", "CVVS/std/4.2.3 Android/12")
        .addHeader("Z-Dev-Apikey", "Tg1NWEwNGIgIC0K")
        .addHeader("Z-Auth-Token", token)
        .get()
        .build()

    client.newCall(request).execute().use { response ->
        return response.body?.string() ?: "{}"
    }
}

fun getListaMaterie(array: JSONArray): MutableList<String> {
    val listaMaterie: MutableList<String> = mutableListOf()
    for (i in 0..< array.length()) {
        val oggetto = array.getJSONObject(i)
        val materia = oggetto.get("subjectDesc").toString()
        if (materia !in listaMaterie) listaMaterie.add(materia)
    }

    return listaMaterie
}

fun filtraJsonArray(jsonArray: JSONArray, key: String, value: String): JSONArray {
    val listaFiltrata: MutableList<JSONObject> = mutableListOf()
    for (i in 0..< jsonArray.length()) {
        val oggetto = jsonArray.getJSONObject(i)
        if (oggetto.get(key).toString() == value) listaFiltrata.add(oggetto)
    }
    val jsonFiltrato = JSONArray(listaFiltrata)
    return jsonFiltrato
}

fun getVoti(token: String) {
    val resp = JSONObject(req("v1/students/$studentId/grades", token))
    var voti = resp.getJSONArray("grades")

    val listaMaterie = getListaMaterie(voti)

    println("Ordina per?\n1. Data\n2. Materia\n3. Voto")
    val selSort = readln().toIntOrNull() ?: 0
    when (selSort) {
        2 -> voti = sortJSONArrayByKeyDouble(voti, "subjectId")
        3 -> voti = sortJSONArrayByKeyDouble(voti , "decimalValue")
        else -> voti = sortJSONArrayByKeyStr(voti, "evtDate")
    }
    print("\n")

    val countMaterie = listaMaterie.size
    var stringaFiltri = "Filtra?\n1. Primo Quadrimestre\n2. Secondo Quadrimestre\n"

    for (i in 3 .. countMaterie + 2) stringaFiltri += "$i. ${listaMaterie[i - 3]}\n"
    stringaFiltri += "Altro: Tutti i voti"
    println(stringaFiltri)
    val selFiltro = readln().toIntOrNull() ?: 0
    print("\n")
    when (selFiltro) {
        1 -> voti = filtraJsonArray(voti, "periodPos", "1")
        2 -> voti = filtraJsonArray(voti, "periodPos", "3")
    }
    if (selFiltro - 3 in listaMaterie.indices) voti = filtraJsonArray(voti, "subjectDesc", listaMaterie[selFiltro - 3])

    for (i in 0..< voti.length()) {
        val voto = voti.getJSONObject(i)

        val data = voto.get("evtDate")
        val materia = voto.get("subjectDesc")
        val displayVal = voto.get("displayValue")
        val component = voto.get("componentDesc")

        println("$data $materia: $displayVal ($component)")
    }
    print("\n")
}

fun getCompiti(token: String) {
    do {
        var nextSchoolday: String
        var nextSchooldayDateObj: LocalDate
        val yyyyMMdd =
            DateTimeFormatter.ofPattern("yyyyMMdd") // formatter in yyyyMMdd (serve solo per l'URL della richiesta)

        var sel: String

        do {
            println("Inserisci una data (formato yyyyMMdd).\nInserisci \"d\" per ottenere automaticamente la data di domani.\nInserisci \"i\" per tornare indietro.")
            sel = readln()
            val onlyDigits = sel.matches(Regex("\\d+"))
        } while (sel != "d" && sel != "i" && !onlyDigits)
        when (sel) {
            "d" -> {
                nextSchooldayDateObj = LocalDate.now().plusDays(1) // Prende la data di domani
                if (nextSchooldayDateObj.dayOfWeek == DayOfWeek.SUNDAY) nextSchooldayDateObj =
                    nextSchooldayDateObj.plusDays(1) // Se domani è domenica, prendi la data di lunedì
                nextSchoolday = nextSchooldayDateObj.format(yyyyMMdd)
            }

            "i" -> break
            else -> {
                nextSchooldayDateObj = LocalDate.parse(sel, yyyyMMdd)
                nextSchoolday = sel
            }
        }

        val resp = JSONObject(req("v1/students/$studentId/agenda/all/$nextSchoolday/$nextSchoolday", token))
        val compiti = resp.getJSONArray("agenda")

        println("Compiti per il $nextSchooldayDateObj")
        for (i in 0..< compiti.length()) {
            val compito = compiti.getJSONObject(i)

            val materia = compito.get("subjectDesc")
            val prof = compito.get("authorName")
            val note = compito.get("notes")

            if (materia != JSONObject.NULL) println("$prof ($materia): $note\n") else println("$prof: $note\n")
        }
    } while (true)
    print("\n")
}

fun calcolaMedie(token: String) {
    val resp = JSONObject(req("v1/students/$studentId/grades", token))
    val voti = resp.getJSONArray("grades")

    val listaMaterie = getListaMaterie(voti)

    var c1Q = 0
    var c2Q = 0
    val cMaterie: MutableMap<String, Int> = mutableMapOf()

    for (i in 0..< voti.length()) {
        val voto = voti.getJSONObject(i)
        val q = voto.get("periodPos").toString()
        val materia = voto.get("subjectDesc").toString()

        if (voto["decimalValue"] != JSONObject.NULL) { // Se il voto non ha valore decimale (ex. i voti che non fanno media o i +) non contarlo
            if (q == "1") c1Q += 1 else c2Q += 1
            if (materia !in cMaterie.keys) cMaterie[materia] = 1 else cMaterie[materia] = cMaterie[materia]!! + 1
        }
    }

    var v1Q = 0.0
    var v2Q = 0.0
    val vMaterie: MutableMap<String, Double> = mutableMapOf()

    for (i in 0..< voti.length()) {
        val voto = voti.getJSONObject(i)
        val q = voto.get("periodPos").toString()
        val decValue = voto.get("decimalValue").toString().toDoubleOrNull() ?: 0.0
        val materia = voto.get("subjectDesc").toString()

        if (q == "1") v1Q += decValue else v2Q += decValue
        if (materia !in vMaterie.keys) vMaterie[materia] = decValue else vMaterie[materia] = vMaterie[materia]!! + decValue
    }

    val m1Q = DecimalFormat("#.##").format(v1Q / c1Q).toDouble() // Approssimo a 2 cifre decimali
    val m2Q = DecimalFormat("#.##").format(v2Q / c2Q).toDouble()
    val mMaterie: MutableMap<String, Double> = mutableMapOf()
    for (i in 0 ..< listaMaterie.size) mMaterie[listaMaterie[i]] = DecimalFormat("#.##").format(vMaterie[listaMaterie[i]]!! / cMaterie[listaMaterie[i]]!!).toDouble()
    val lmMaterie = mMaterie.entries.sortedByDescending { it.value }.associate { it.key to it.value } // Dato che la mutableMap non è sortabile, devo usare una LinkedHashMap

    println("La media del primo quadrimestre è $m1Q")
    println("La media del secondo quadrimestre è $m2Q")
    for (materia in lmMaterie.keys) println("La media generale della materia $materia è ${lmMaterie[materia]}")
    print("\n")
}

fun getAssenze(token: String) {
    var assenze = JSONObject(req("v1/students/$studentId/absences/details", token)).getJSONArray("events")

    val giustificate: MutableList<JSONObject> = mutableListOf()
    val nonGiustificate: MutableList<JSONObject> = mutableListOf()

    for (i in 0 ..< assenze.length()) {
        val assenza = assenze.getJSONObject(i)
        if (assenza.getBoolean("isJustified")) giustificate.add(assenza) else nonGiustificate.add(assenza)
    }

    if (!giustificate.isEmpty()) {
        println("Assenze giustificate:")
        for (i in giustificate.indices) {
            val assenza = giustificate[i]
            val data = assenza.getString("evtDate")
            val motivo = assenza.getString("justifReasonDesc")
            if (motivo != JSONObject.NULL && motivo != "") println("$data (motivo: $motivo)") else println("$data (nessun motivo specificato)")
        }
    } else println("Non ci sono assenze giustificate.")
    print("\n")

    if (!nonGiustificate.isEmpty()) {
        println("Assenze non giustificate:")
        for (i in nonGiustificate.indices) {
            val assenza = nonGiustificate[i]
            val data = assenza.getString("evtDate")
            println(data)
        }
    } else println("Non ci sono assenze non giustificate.")
    print("\n")
}

fun getLezioni(token: String) {
    val questoAnno = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy"))
    val annoPrecedente = LocalDate.now().minusYears(1).format(DateTimeFormatter.ofPattern("yyyy"))
    var lezioni = JSONObject(req("v1/students/$studentId/lessons/${annoPrecedente}0901/${questoAnno}1231", token)).getJSONArray("lessons")
    val listaMaterie = getListaMaterie(lezioni)

    println("Di quale materia vuoi vedere le lezioni?")
    var stringa = ""
    for (i in 1 .. listaMaterie.size) stringa += "$i. ${listaMaterie[i - 1]}\n"
    stringa += "Altro: Tutte"
    println(stringa)
    val selMateria = readln().toIntOrNull() ?: 0
    var tutteLeLezioni = false
    if (selMateria - 1 in listaMaterie.indices) {
        lezioni = filtraJsonArray(lezioni, "subjectDesc", listaMaterie[selMateria - 1])
        println("\nLezioni di ${listaMaterie[selMateria - 1]}:")
    } else {
        println("\nTutte le lezioni:")
        tutteLeLezioni = true
    }

    for (i in 0 ..< lezioni.length()) {
        val lezione = lezioni.getJSONObject(i)
        var argomento = lezione.getString("lessonArg")
        if (argomento == JSONObject.NULL || argomento == "") argomento = "(nessun argomento dato)"
        val data = lezione.getString("evtDate")
        val autore = lezione.getString("authorName")
        val materia = lezione.getString("subjectDesc")

        if (tutteLeLezioni && materia != JSONObject.NULL && materia != "") {
            if (autore != JSONObject.NULL && autore != "") println("$autore ($materia), $data: $argomento") else println("$materia, $data: $argomento")
        } else {
            if (autore != JSONObject.NULL && autore != "") println("$autore, $data: $argomento") else println("$data: $argomento")
        }
    }
    print("\n")
}
