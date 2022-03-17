package pl.pszklarska.pubversionchecker.inspection

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import kotlinx.coroutines.*
import org.jetbrains.yaml.psi.YamlPsiElementVisitor
import pl.pszklarska.pubversionchecker.parsing.YamlParser
import pl.pszklarska.pubversionchecker.quickfix.UpdateAllDependenciesQuickFix
import pl.pszklarska.pubversionchecker.quickfix.UpdateDependencyQuickFix
import pl.pszklarska.pubversionchecker.reporting.CrashReporting
import pl.pszklarska.pubversionchecker.util.VersionsRepository
import pl.pszklarska.pubversionchecker.util.isPubspecFile

class PubPackagesInspection : LocalInspectionTool() {

    override fun buildVisitor(holder: ProblemsHolder, isOnTheFly: Boolean): PsiElementVisitor {
        return YamlElementVisitor(holder, isOnTheFly)
    }
}

class YamlElementVisitor(
    private val holder: ProblemsHolder,
    private val isOnTheFly: Boolean,
) : YamlPsiElementVisitor() {

    private val updateOneDependencyDescription = "Latest available version is: "

    override fun visitFile(file: PsiFile) {
        if (!isOnTheFly) return
        if (!file.isPubspecFile()) return

        val crashReporting = CrashReporting()
        crashReporting.init()

        val versionsRepository = VersionsRepository()
        val yamlParser = YamlParser(file.text, versionsRepository)

        CoroutineScope(Dispatchers.Main).launch {
            val notMatchingDependencies = yamlParser.inspectFile()
            notMatchingDependencies.forEach {
                val psiElement = file.findElementAt(it.dependency.index)!!

                holder.registerProblem(
                    psiElement,
                    updateOneDependencyDescription + it.latestVersion,
                    ProblemHighlightType.WARNING,
                    UpdateDependencyQuickFix(psiElement, it.latestVersion, it.dependency.packageName),
                    UpdateAllDependenciesQuickFix(file, notMatchingDependencies)
                )

            }
        }
    }
}
