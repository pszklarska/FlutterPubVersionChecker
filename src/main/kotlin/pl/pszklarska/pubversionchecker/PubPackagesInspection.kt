package pl.pszklarska.pubversionchecker

import com.google.gson.Gson
import com.intellij.codeInsight.daemon.GroupNames
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.openapi.diagnostic.Logger
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.util.containers.getIfSingle
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern


const val REGEX_DEPENDENCY = ".*(?!version|sdk)\\b\\S+:.+\\.[0-9]+\\.[0-9]+"
const val YML_EXTENSIONS = "yml"
const val PUB_API_URL = "https://pub.dartlang.org/api/packages/"
private val LOG = Logger.getInstance(PubPackagesInspection::class.java)


class PubPackagesInspection : LocalInspectionTool() {

    override fun getDisplayName(): String {
        return "Pub Packages latest versions"
    }

    override fun getGroupDisplayName(): String {
        return GroupNames.DEPENDENCY_GROUP_NAME
    }

    override fun getShortName(): String {
        return "PubVersions"
    }

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return YamlElementVisitor(holder)
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }
}

class YamlElementVisitor(private val holder: ProblemsHolder) : PsiElementVisitor() {

    override fun visitFile(file: PsiFile) {
        ApplicationManager.getApplication().runReadAction {
            checkFile(file)
        }
    }

    private fun checkFile(file: PsiFile) {
        if (file.isPubspecFile()) {
            file.readPackageLines().forEach {
                val dependency = it.first
                val counter = it.second

                val latestVersion = getLatestVersion(dependency)
                val currentVersion = getCurrentVersion(dependency)

                if (latestVersion.compareTo(currentVersion) != 0) {
                    holder.showProblem(file, counter, currentVersion, latestVersion)
                }
            }
        }
    }
}

private fun PsiFile.isPubspecFile(): Boolean {
    return fileType.defaultExtension == YML_EXTENSIONS && name.contains("pubspec")
}

fun PsiFile.readPackageLines(): List<Pair<String, Int>> {
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

fun getCurrentVersion(dependency: String): String {
    val currentVersion = dependency.split(':')[1].replace("^", "").trim()
    printMessage("Current version: $currentVersion")
    return currentVersion
}

fun getLatestVersion(line: String): String {
    val packageName = line.trim().split(':')[0]
    val url = URL(PUB_API_URL + packageName)

    printMessage("Checking latest version for: $packageName")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        inputStream.bufferedReader().use {
            val response = parsePackageResponse(it.lines().getIfSingle()!!)
            val latest = response.latest
            val latestVersion = latest.version
            val latestVersionTrim = latestVersion.trim()
            printMessage("Latest version: $latestVersionTrim")
            return latestVersionTrim
        }
    }

}

private fun printMessage(message: String) {
    LOG.info(message)
}

fun parsePackageResponse(responseString: String): Response {
    return Gson().fromJson(responseString, Response::class.java)
}

private fun ProblemsHolder.showProblem(file: PsiFile, counter: Int, currentVersion: String, latestVersion: String) {
    registerProblem(
        file.findElementAt(counter)!!,
        "Version $currentVersion is different than the latest $latestVersion"
    )

}
