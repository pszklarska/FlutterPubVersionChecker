package pl.pszklarska.pubversionchecker.util

import pl.pszklarska.pubversionchecker.dto.Dependency

class DependencyUtil(
    private val versionsRepository: VersionsRepository
) {

    @Throws(UnableToGetLatestVersionException::class)
    fun mapToDependency(dependencyToLineIndex: Map.Entry<String, Int>): Dependency {
        val dependency = dependencyToLineIndex.key
        val index = dependencyToLineIndex.value

        val latestVersion = versionsRepository.getLatestVersion(dependency)
        val currentVersion = dependency.extractVersion()

        return Dependency(currentVersion, latestVersion, index)
    }

    class UnableToReadCurrentVersionException(dependency: String) :
        Exception("Cannot read current version number for dependency: $dependency")
}

fun String.extractVersion(): String {
    val regex = REGEX_DEPENDENCY.toRegex()
    try {
        return regex.find(this)?.groupValues?.get(1)!!
    } catch (e: Exception) {
        print(e)
        throw DependencyUtil.UnableToReadCurrentVersionException(this)
    }
}