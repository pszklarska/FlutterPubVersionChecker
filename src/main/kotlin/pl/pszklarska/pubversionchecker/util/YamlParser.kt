package pl.pszklarska.pubversionchecker.util

import com.intellij.psi.PsiFile
import kotlinx.coroutines.*
import pl.pszklarska.pubversionchecker.dto.Dependency
import java.util.regex.Pattern

const val REGEX_DEPENDENCY = """^\s*(?!version|sdk|ref|url)\S+:\s*[<|=>^]*([0-9]+\.[0-9]+\.[0-9]+\+?\S*)"""
const val YML_EXTENSIONS = "yml"

class YamlParser(
    private val file: PsiFile,
    private val dependencyUtil: DependencyUtil,
) {

    private val parentJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + parentJob)

    suspend fun inspectFile(): List<Dependency> {
        parentJob.cancelChildren(cause = null)

        return if (file.isPubspecFile()) {
            return getDependencyListFromFile()
        } else {
            emptyList()
        }
    }

    private suspend fun getDependencyListFromFile(): List<Dependency> {
        val dependencies: List<Dependency> = getAllDependenciesList()
        return getNotMatchingDependenciesList(dependencies)
    }

    private suspend fun getAllDependenciesList(): List<Dependency> {
        return file.readLines().map {
            scope.async {
                try {
                    dependencyUtil.mapToDependency(it)
                } catch (e: UnableToGetLatestVersionException) {
                    null
                }
            }
        }.awaitAll().filterNotNull()
    }

    private fun getNotMatchingDependenciesList(dependencies: List<Dependency>): List<Dependency> {
        return dependencies.mapNotNull {
            try {
                if (it.latestVersion != it.currentVersion) it else null
            } catch (e: UnableToGetLatestVersionException) {
                null
            }
        }
    }
}

fun String.isPackageName(): Boolean {
    val regexPattern = Pattern.compile(REGEX_DEPENDENCY)
    return regexPattern.matcher(this).find()
}