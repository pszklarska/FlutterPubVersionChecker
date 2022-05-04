package pl.pszklarska.pubversionchecker.quickfix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.util.IncorrectOperationException
import pl.pszklarska.pubversionchecker.dto.DependencyDescription
import pl.pszklarska.pubversionchecker.util.findVersionIndexInFile


class UpdateAllDependenciesQuickFix(
    private val dependencies: List<DependencyDescription>,
) : BaseIntentionAction() {

    private val description = "Update\u200B all" // zero-length whitespace to place "Update all" below other fixes

    override fun getText(): String {
        return description
    }

    override fun getFamilyName(): String {
        return "Update package"
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val iElementType = IElementType("text", Language.findLanguageByID("yaml"))
        dependencies.forEach {
            val newElement = factory.createDummyHolder("^${it.latestVersion}", iElementType, null)
            val newIndex = file.text.findVersionIndexInFile(it.dependency.packageName)
            val psiElement = file.findElementAt(newIndex)!!
            psiElement.replace(newElement)
        }
    }

}