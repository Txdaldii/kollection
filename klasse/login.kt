import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

var studentId = ""

fun login(): String? {
    val client = OkHttpClient()
    val jsonMediaType = "application/json; charset=utf-8".toMediaType()
    var success: Boolean
    var token: String?

    do {
        print("Inserisci il nome utente di Classeviva: ")
        val uid = readln()

        print("Inserisci la password: ")
        val passwordCharArray = System.console()?.readPassword() ?: readLine()?.toCharArray() ?: charArrayOf()
        val password = String(passwordCharArray)
        val requestBody = """
            {
                "ident": null,
                "pass": "$password",
                "uid": "$uid"
            }
        """.trimIndent().toRequestBody(jsonMediaType)

        val request = Request.Builder()
            .url("https://web.spaggiari.eu/rest/v1/auth/login")
            .addHeader("Content-Type", "application/json")
            .addHeader("User-Agent", "CVVS/std/4.2.3 Android/12")
            .addHeader("Z-Dev-ApiKey", "Tg1NWEwNGIgIC0K")
            .post(requestBody)
            .build()

        client.newCall(request).execute().use { response ->
            if (!response.isSuccessful) success = false else {
                success = true
                val resp = JSONObject(response.body?.string()) // Dato che la response è successful, response.body non sarà null
                token = resp.getString("token") // ~~ map["token"] non sarà null
                studentId = resp.getString("ident")!!.drop(1)
                return token
            }
        }
    } while (!success)

    return null // Questa parte del programma non verrà mai raggiunta ma necessita un return
}
