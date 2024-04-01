import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.File
import java.io.IOException
import org.json.JSONObject

fun getMimeType(filePath: String): String {
    var mimetypes = JSONObject(mapOf( // Lista di felix19350 su GitHub (https://gist.githubusercontent.com/felix19350/d42ca9a9f505532e0292642ae67b1691/raw/93eb7186bda3f5cfb8d642e9ac870b8b87b8105e/mime-type-catalog.csv), convertita con ChatGPT
        ".aac" to "audio/aac",
        ".abw" to "application/x-abiword",
        ".arc" to "application/octet-stream",
        ".avi" to "video/x-msvideo",
        ".azw" to "application/vnd.amazon.ebook",
        ".bin" to "application/octet-stream",
        ".bz" to "application/x-bzip",
        ".bz2" to "application/x-bzip2",
        ".csh" to "application/x-csh",
        ".css" to "text/css",
        ".csv" to "text/csv",
        ".doc" to "application/msword",
        ".epub" to "application/epub+zip",
        ".gif" to "image/gif",
        ".htm" to "text/html",
        ".html" to "text/html",
        ".ico" to "image/x-icon",
        ".ics" to "text/calendar",
        ".jar" to "application/java-archive",
        ".jpeg" to "image/jpeg",
        ".jpg" to "image/jpeg",
        ".js" to "application/javascript",
        ".json" to "application/json",
        ".midi" to "audio/midi",
        ".mid" to "audio/midi",
        ".mpeg" to "video/mpeg",
        ".mpkg" to "application/vnd.apple.installer+xml",
        ".odp" to "application/vnd.oasis.opendocument.presentation",
        ".ods" to "application/vnd.oasis.opendocument.spreadsheet",
        ".odt" to "application/vnd.oasis.opendocument.text",
        ".oga" to "audio/ogg",
        ".ogv" to "video/ogg",
        ".ogx" to "application/ogg",
        ".pdf" to "application/pdf",
        ".ppt" to "application/vnd.ms-powerpoint",
        ".rar" to "application/x-rar-compressed",
        ".rtf" to "application/rtf",
        ".sh" to "application/x-sh",
        ".svg" to "image/svg+xml",
        ".swf" to "application/x-shockwave-flash",
        ".tar" to "application/x-tar",
        ".tiff" to "image/tiff",
        ".tif" to "image/tiff",
        ".ttf" to "font/ttf",
        ".vsd" to "application/vnd.visio",
        ".wav" to "audio/x-wav",
        ".weba" to "audio/webm",
        ".webm" to "video/webm",
        ".webp" to "image/webp",
        ".woff" to "font/woff",
        ".woff2" to "font/woff2",
        ".xhtml" to "application/xhtml+xml",
        ".xls" to "application/vnd.ms-excel",
        ".xml" to "application/xml",
        ".xul" to "application/vnd.mozilla.xul+xml",
        ".zip" to "application/zip",
        ".3gp" to "video/3gpp",
        ".3g2" to "video/3gpp2",
        ".7z" to "application/x-7z-compressed"
    ))

    // Trova l'ultimo punto nella stringa
    val lastIndex = filePath.lastIndexOf('.')

    // Se c'è un punto e non è all'inizio o alla fine della stringa
    val fileExtension = if (lastIndex != -1 && lastIndex != 0 && lastIndex != filePath.length - 1) {
        filePath.substring(lastIndex + 1)
    } else {
        // Se non c'è un'estensione valida
        "Nessuna estensione valida"
    }

    if (fileExtension in mimetypes.keys().toString()) {
        var mimetype = mimetypes[fileExtension].toString()
        return mimetype
    }
    return ""
}

fun getFileLink(msgFile: String) {
    val url = "https://file.io/"

    // File da caricare
    val file = File(msgFile)
    val mimetype = getMimeType(msgFile)

    // Costruzione del corpo della richiesta multipart
    val requestBody = MultipartBody.Builder()
        .setType(MultipartBody.FORM)
        .addFormDataPart("autoDelete", "true")
        .addFormDataPart("file", file.name, RequestBody.create(mimetype!!.toMediaTypeOrNull(), file))
        .build()

    // Costruzione della richiesta con le intestazioni
    val request = Request.Builder()
        .url(url)
        .header("Accept", "application/json")
        .header("Content-Type", "multipart/form-data")
        .post(requestBody)
        .build()

    // Creazione del client OkHttp e esecuzione della richiesta
    val client = OkHttpClient()
    client.newCall(request).enqueue(object : Callback {
        override fun onFailure(call: Call, e: IOException) {
            println("Errore durante la richiesta: ${e.message}")
        }

        override fun onResponse(call: Call, response: Response) {
            if (response.isSuccessful) {
                val responseBody = JSONObject(response.body?.string())
                println("Manda questo link in chat: ${responseBody.getString("link")}")
            } else {
                println("Errore durante la richiesta: ${response.code}")
            }
        }
    })
}
