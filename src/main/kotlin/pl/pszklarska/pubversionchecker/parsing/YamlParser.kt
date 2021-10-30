package pl.pszklarska.pubversionchecker.parsing

import kotlinx.coroutines.*
import pl.pszklarska.pubversionchecker.dto.DependencyDescription
import pl.pszklarska.pubversionchecker.util.UnableToGetLatestVersionException
import pl.pszklarska.pubversionchecker.util.VersionsRepository
import pl.pszklarska.pubversionchecker.util.getDependencies

class YamlParser(
    private val fileContent: String,
    private val versionsRepository: VersionsRepository
) {

    private val parentJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + parentJob)

    suspend fun inspectFile(): List<DependencyDescription> {
        parentJob.cancelChildren(cause = null)
        return getDependencyList()
    }

    private suspend fun getDependencyList(): List<DependencyDescription> {
        val dependencies: List<DependencyDescription> = getAllDependenciesList()
        return getNotMatchingDependenciesList(dependencies)
    }

    private suspend fun getAllDependenciesList(): List<DependencyDescription> {
        return fileContent.getDependencies().map {
            scope.async {
                try {
                    val latestVersion = versionsRepository.getLatestVersion(it.packageName)
                    DependencyDescription(it, latestVersion)
                } catch (e: UnableToGetLatestVersionException) {
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

    private fun getNotMatchingDependenciesList(dependencies: List<DependencyDescription>): List<DependencyDescription> {
        return dependencies.mapNotNull {
            if (it.latestVersion != it.dependency.currentVersion) it else null
        }
    }
}