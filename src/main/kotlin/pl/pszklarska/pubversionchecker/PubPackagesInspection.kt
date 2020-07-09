package pl.pszklarska.pubversionchecker

import com.intellij.codeInspection.LocalInspectionTool
import com.intellij.codeInspection.ProblemsHolder
import com.intellij.psi.PsiElementVisitor
import com.intellij.psi.PsiFile
import kotlinx.coroutines.runBlocking
import org.jetbrains.yaml.YAMLUtil
import org.jetbrains.yaml.psi.impl.YAMLFileImpl
import org.jetbrains.yaml.psi.impl.YAMLKeyValueImpl

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
                holder.showProblem(file, it.currentVersion, it.latestVersion, it.line)
            }
        }
    }
}

private fun ProblemsHolder.showProblem(
    file: PsiFile,
    currentVersion: String,
    latestVersion: String,
    line: String
) {
    YAMLUtil.getTopLevelKeys((file as YAMLFileImpl))
        .firstOrNull() { yamlKeyValue -> yamlKeyValue.keyText == "dependencies" }?.children?.firstOrNull()
        ?.let { dependencyBlock ->
            dependencyBlock.children
                .firstOrNull() { line.getPackageName() in it.text }
                ?.let {
                    registerProblem(
                        it,
                        "Version $currentVersion is different from the latest $latestVersion",
                        DependencyQuickFix((it as YAMLKeyValueImpl).value!!, latestVersion)
                    )
                }
        }
}
