package pl.pszklarska.pubversionchecker

import com.google.gson.Gson
import com.intellij.codeInsight.daemon.GroupNames
import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.LocalInspectionToolSession
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.openapi.application.ApplicationManager
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import com.intellij.util.containers.getIfSingle
import java.net.HttpURLConnection
import java.net.URL
import java.util.regex.Pattern

const val REGEX_DEPENDENCY = ".+(?!version|sdk)\\b\\S+:.+\\..+\\..+"
const val YML_EXTENSIONS = "yml"

class PackagesInspection : LocalInspectionTool() {

    override fun getDisplayName(): String {
        return "Checks for Pub Packages latest versions"
    }

    override fun getGroupDisplayName(): String {
        return GroupNames.DEPENDENCY_GROUP_NAME
    }

    override fun getShortName(): String {
        return "PubVersions"
    }

    override fun buildVisitor(
        holder: ProblemsHolder, isOnTheFly: Boolean, session: LocalInspectionToolSession
    ): PsiElementVisitor {
        return YamlElementVisitor(holder)
    }

    override fun isEnabledByDefault(): Boolean {
        return true
    }
}

class YamlElementVisitor(
    private val holder: ProblemsHolder
) : PsiElementVisitor() {
    override fun visitFile(file: PsiFile) {
        ApplicationManager.getApplication().runReadAction {
            if (file.fileType.defaultExtension == YML_EXTENSIONS) {
                file.readPackageLines().forEach {
                    val dependency = it.first
                    val counter = it.second

                    val latestVersion = getLatestVersion(dependency)
                    val currentVersion = getCurrentVersion(dependency)

                    if (latestVersion.compareTo(currentVersion) != 0) {
                        showProblem(file, counter, currentVersion, latestVersion)
                    }
                }
            }
        }
    }

    private fun showProblem(
        file: PsiFile, counter: Int, currentVersion: String, latestVersion: String
    ) {
        holder.registerProblem(
            file.findElementAt(counter)!!,
            "Version $currentVersion is different than the latest $latestVersion"
        )
    }
}

fun PsiFile.readPackageLines(): List<Pair<String, Int>> {
    val linesList = mutableListOf<Pair<String, Int>>()
    var line = ""
    var counter = 0
    text.forEach {
        counter++
        if (it == '\n') {
            if (line.isPackageName()) {
                linesList.add(line to counter - 2)
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
    return dependency.split(':')[1].replace("^", "").trim()
}

fun getLatestVersion(line: String): String {
    val packageName = line.trim().split(':')[0]
    val url = URL("https://pub.dartlang.org/api/packages/$packageName")

    with(url.openConnection() as HttpURLConnection) {
        requestMethod = "GET"
        inputStream.bufferedReader().use {
            val response = parsePackageResponse(it.lines().getIfSingle()!!)
            return response.latest.version.trim()
        }
    }

}

fun parsePackageResponse(responseString: String): Response {
    return Gson().fromJson(responseString, Response::class.java)
}