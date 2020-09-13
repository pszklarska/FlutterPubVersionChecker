package pl.pszklarska.pubversionchecker

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import kotlinx.coroutines.runBlocking

class PubPackagesInspection : LocalInspectionTool() {

    private val dependencyChecker = DependencyChecker()

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return YamlElementVisitor(holder, isOnTheFly, dependencyChecker)
    }
}

class YamlElementVisitor(
    private val holder: ProblemsHolder,
    private val isOnTheFly: Boolean,
    private val dependencyChecker: DependencyChecker
) : PsiElementVisitor() {

    override fun visitFile(file: PsiFile) {
        if (!isOnTheFly) return

        runBlocking {

            val fileParser = FileParser(file, dependencyChecker)
            val problemDescriptions = fileParser.checkFile()

            problemDescriptions.forEach {
                holder.showProblem(file, it.currentVersion, it.latestVersion, it.line, it.index)
            }
        }
    }
}

private fun ProblemsHolder.showProblem(
    file: PsiFile,
    currentVersion: String,
    latestVersion: String,
    line: String,
    index: Int
) {

    val psiElement = file.findElementAt(index)!!
    println("Found problem at $line")
    registerProblem(
        psiElement,
        "Version $currentVersion is different from the latest $latestVersion",
        DependencyQuickFix(psiElement, latestVersion)
    )
}
