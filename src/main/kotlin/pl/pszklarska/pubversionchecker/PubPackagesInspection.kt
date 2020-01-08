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
                holder.showProblem(file, it.counter, it.currentVersion, it.latestVersion)
            }
        }
    }
}

private fun ProblemsHolder.showProblem(
    file: PsiFile,
    counter: Int,
    currentVersion: String,
    latestVersion: String
) {

    val psiElement = file.findElementAt(counter)!!
    println("Found problem at $counter")
    registerProblem(
        psiElement,
        "Version $currentVersion is different than the latest $latestVersion",
        DependencyQuickFix(psiElement, latestVersion)
    )
}
