package pl.pszklarska.pubversionchecker.util

import java.net.URL
import java.util.zip.GZIPInputStream

class HttpClient {

    fun getFileAsString(url: String): String {
        return GZIPInputStream(URL(url).readBytes().inputStream()).bufferedReader(Charsets.UTF_8).use { it.readText() }
    }
}