package pl.pszklarska.pubversionchecker.util

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import pl.pszklarska.pubversionchecker.dto.Response
import pl.pszklarska.pubversionchecker.util.exceptions.UnableToGetLatestVersionException
import java.io.IOException

const val PUB_API_URL = "https://pub.dartlang.org/api/packages/"

class VersionsRepository {

    private val httpClient = DependencyHttpClient()

    private val dependencyList = mutableListOf<Dependency>()

    private val mapper = ObjectMapper()
        .registerModule(KotlinModule())
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

    fun getLatestVersion(packageName: String): String {
        printMessage("Checking latest version for: $packageName")

        val cachedDependency = dependencyList.find { it.packageName == packageName }
        return if (cachedDependency != null) {
            cachedDependency.version
        } else {
            val dependencyVersion = fetchDependencyVersion(packageName)
            dependencyList.add(Dependency(packageName, dependencyVersion))
            dependencyVersion
        }
    }

    private fun fetchDependencyVersion(packageName: String): String {
        try {
            val jsonResponse = httpClient.getContentAsString(PUB_API_URL + packageName)
            val response = parseResponse(jsonResponse)
            return response.latest.version.trim()
        } catch (e: IOException) {
            throw UnableToGetLatestVersionException(packageName, e)
        }
    }

    private fun parseResponse(responseString: String): Response {
        return mapper.readValue(responseString)
    }

}

private data class Dependency(
    val packageName: String,
    val version: String
)