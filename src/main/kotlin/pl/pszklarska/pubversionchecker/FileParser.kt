package pl.pszklarska.pubversionchecker

import com.intellij.psi.PsiFile
import kotlinx.coroutines.*
import java.util.regex.Pattern
import kotlin.coroutines.CoroutineContext

const val REGEX_DEPENDENCY = ".*(?!version|sdk)\\b\\S+:.+\\.[0-9]+\\.[0-9]+(.*)"
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
            file.readPackageLines().map { async { mapToVersionDescription(it) } }.awaitAll()

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
    private fun mapToVersionDescription(it: Pair<String, Int>): VersionDescription {
        val dependency = it.first
        val counter = it.second

        val latestVersion = dependencyChecker.getLatestVersion(dependency)
        val currentVersion = getCurrentVersion(dependency)

        return VersionDescription(counter, currentVersion, latestVersion)
    }
}

private fun PsiFile.isPubspecFile(): Boolean {
    return fileType.defaultExtension == YML_EXTENSIONS && name.contains("pubspec")
}

private fun PsiFile.readPackageLines(): List<Pair<String, Int>> {
    val linesList = mutableListOf<Pair<String, Int>>()
    var line = ""
    var counter = 0
    text.forEach {
        counter++
        if (it == '\n') {
            line = line.trim()
            if (!line.startsWith("#") && line.isPackageName()) {
                linesList.add(line to counter - 2)
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
    val currentVersion = dependency.split(':')[1].replace("^", "").trim()
    printMessage("Current version: $currentVersion")
    return currentVersion
}


data class VersionDescription(
    val counter: Int,
    val currentVersion: String,
    val latestVersion: String
)