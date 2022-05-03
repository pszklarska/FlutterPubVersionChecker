package pl.pszklarska.pubversionchecker.parsing

import io.sentry.Sentry
import pl.pszklarska.pubversionchecker.dto.DependencyDescription
import pl.pszklarska.pubversionchecker.util.VersionsRepository
import pl.pszklarska.pubversionchecker.util.exceptions.UnableToGetCurrentVersionException
import pl.pszklarska.pubversionchecker.util.exceptions.UnableToGetLatestVersionException
import pl.pszklarska.pubversionchecker.util.exceptions.UnableToGetPackageNameException
import pl.pszklarska.pubversionchecker.util.getDependencies

class YamlParser(
    private val fileContent: String,
    private val versionsRepository: VersionsRepository
) {

    fun inspectFile(): List<DependencyDescription> {
        return getDependencyList()
    }

    private fun getDependencyList(): List<DependencyDescription> {
        val dependencies: List<DependencyDescription> = getAllDependenciesList()
        return getNotMatchingDependenciesList(dependencies)
    }

    private fun getAllDependenciesList(): List<DependencyDescription> {
        return fileContent.getDependencies().mapNotNull {
            try {
                val latestVersion = versionsRepository.getLatestVersion(it.packageName)
                DependencyDescription(it, latestVersion)
            } catch (e: UnableToGetLatestVersionException) {
                Sentry.captureException(e)
                null
            } catch (e: UnableToGetCurrentVersionException) {
                Sentry.captureException(e)
                null
            } catch (e: UnableToGetPackageNameException) {
                Sentry.captureException(e)
                null
            }
        }
    }

    private fun getNotMatchingDependenciesList(dependencies: List<DependencyDescription>): List<DependencyDescription> {
        return dependencies.mapNotNull {
            if (it.latestVersion != it.dependency.currentVersion) it else null
        }
    }
}