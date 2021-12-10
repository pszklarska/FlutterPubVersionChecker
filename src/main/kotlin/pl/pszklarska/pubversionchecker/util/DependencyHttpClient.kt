package pl.pszklarska.pubversionchecker.util

import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse

class DependencyHttpClient {
    private val client = HttpClient.newBuilder().build()

    fun getContentAsString(url: String): String {
        val request = HttpRequest.newBuilder()
            .uri(URI.create(url))
            .build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofString())
        return response.body()
    }
}