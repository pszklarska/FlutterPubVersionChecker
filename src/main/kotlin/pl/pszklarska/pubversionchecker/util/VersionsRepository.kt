package pl.pszklarska.pubversionchecker.util

import com.google.gson.Gson
import pl.pszklarska.pubversionchecker.dto.Response
import java.io.IOException

const val PUB_API_URL = "https://pub.dartlang.org/api/packages/"

class VersionsRepository {

    private val gson = Gson()
    private val httpClient = DependencyHttpClient()

    private val dependencyList = mutableListOf<Dependency>()

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
            println(e)
            throw UnableToGetLatestVersionException(packageName)
        }
    }

    private fun parseResponse(responseString: String): Response {
        return gson.fromJson(responseString, Response::class.java)
    }

}

class UnableToGetLatestVersionException(dependency: String) :
    Exception("Cannot get the latest version number for package: $dependency")


class UnableToGetPackageNameException(dependency: String) :
    Exception("Cannot read package name for dependency: $dependency")

private data class Dependency(
    val packageName: String,
    val version: String
)