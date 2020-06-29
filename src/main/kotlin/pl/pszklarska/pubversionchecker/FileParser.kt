package pl.pszklarska.pubversionchecker

import com.intellij.psi.PsiFile
import kotlinx.coroutines.*
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

const val REGEX_DEPENDENCY = """.*(?!version|sdk)\b\S+:.+\.[0-9]+\.[0-9]+(.*)"""
const val REGEX_DEPENDENCY_VERSION = """\^(.*?)([\S]+)"""
const val YML_EXTENSIONS = "yml"

class FileParser(
    private val file: PsiFile,
    private val dependencyChecker: DependencyChecker
) : CoroutineScope {

    private val parentJob = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + parentJob)

    override val coroutineContext: CoroutineContext
        get() = scope.coroutineContext

    suspend fun checkFile(): List<VersionDescription> {
        parentJob.cancelChildren(cause = null)

        return if (file.isPubspecFile()) {
            return getVersionsFromFile()
        } else {
            emptyList()
        }
    }

    private suspend fun getVersionsFromFile(): MutableList<VersionDescription> {
        val problemDescriptionList = mutableListOf<VersionDescription>()

        val lines: List<VersionDescription> =
            file.readPackageLines().map { coroutineScope { async { mapToVersionDescription(it) } } }
                .awaitAll()

        lines.forEach { versionDescription ->
            try {
                if (versionDescription.latestVersion != versionDescription.currentVersion) {
                    problemDescriptionList.add(versionDescription)
                }
            } catch (e: UnableToGetLatestVersionException) {
                //no-op
            }
        }
        return problemDescriptionList
    }

    @Throws(UnableToGetLatestVersionException::class)
    private fun mapToVersionDescription(dependency: String): VersionDescription {
        val latestVersion = dependencyChecker.getLatestVersion(dependency)
        val currentVersion = getCurrentVersion(dependency)

        return VersionDescription(currentVersion, latestVersion, dependency)
    }
}

private fun PsiFile.isPubspecFile(): Boolean {
    return fileType.defaultExtension == YML_EXTENSIONS && name.contains("pubspec")
}

private fun PsiFile.readPackageLines(): List<String> {
    val linesList = mutableListOf<String>()
    var line = ""
    text.forEach {
        if (listOf('\n').contains(it)) {
            line = line.trim()
            if (!line.startsWith("#") && line.isPackageName()) {
                linesList.add(line)
                printMessage("Found dependency: $line")
            }
            line = ""
        } else {
            line += it
        }
    }
    return linesList
}

fun String.isPackageName(): Boolean {
    val regexPattern = Pattern.compile(REGEX_DEPENDENCY)
    return regexPattern.matcher(this).matches()
}

private fun getCurrentVersion(dependency: String): String {
    val regex = REGEX_DEPENDENCY_VERSION.toRegex()
    try {
        return regex.find(dependency)?.groupValues?.get(0)?.replace("^", "")!!.trim()
    } catch (e: Exception) {
        print(e)
        throw UnableToReadCurrentVersionException(dependency)
    }
}

fun String.getPackageName(): String {
    return this.trim().split(":")[0]
}

class UnableToReadCurrentVersionException(dependency: String) :
    Exception("Cannot read current version number for dependency: $dependency")

data class VersionDescription(
    val currentVersion: String,
    val latestVersion: String,
    val line: String
)