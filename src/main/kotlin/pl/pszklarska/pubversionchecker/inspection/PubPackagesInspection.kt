package pl.pszklarska.pubversionchecker.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import kotlinx.coroutines.runBlocking
import pl.pszklarska.pubversionchecker.quickfix.DependencyQuickFix
import pl.pszklarska.pubversionchecker.util.DependencyUtil
import pl.pszklarska.pubversionchecker.util.VersionsRepository
import pl.pszklarska.pubversionchecker.util.YamlParser

class PubPackagesInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return YamlElementVisitor(holder, isOnTheFly)
    }
}

class YamlElementVisitor(
    private val holder: ProblemsHolder,
    private val isOnTheFly: Boolean,
) : PsiElementVisitor() {

    override fun visitFile(file: PsiFile) {
        if (!isOnTheFly) return

        runBlocking {

            val versionsRepository = VersionsRepository()
            val dependencyUtil = DependencyUtil(versionsRepository)
            val yamlParser = YamlParser(file, dependencyUtil)
            val problemDescriptions = yamlParser.inspectFile()

            problemDescriptions.forEach {
                holder.showProblem(file, it.latestVersion, it.index)
            }
        }
    }
}

private fun ProblemsHolder.showProblem(
    file: PsiFile,
    latestVersion: String,
    index: Int
) {

    val psiElement = file.findElementAt(index)!!
    registerProblem(
        psiElement,
        "Latest available version is: $latestVersion",
        DependencyQuickFix(psiElement, latestVersion)
    )
}
