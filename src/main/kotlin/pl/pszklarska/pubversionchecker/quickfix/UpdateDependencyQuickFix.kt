package pl.pszklarska.pubversionchecker.quickfix

import com.intellij.codeInsight.intention.impl.BaseIntentionAction
import com.intellij.lang.Language
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import com.intellij.util.IncorrectOperationException
import pl.pszklarska.pubversionchecker.resources.Strings


internal class UpdateDependencyQuickFix(
    private val packageName: String, private val latestVersion: String, private val element: PsiElement
) : BaseIntentionAction() {

    override fun getText(): String {
        return "${Strings.updateDescription} $packageName"
    }

    override fun getFamilyName(): String {
        return Strings.fixFamilyName
    }

    override fun isAvailable(project: Project, editor: Editor, file: PsiFile): Boolean {
        return true
    }

    @Throws(IncorrectOperationException::class)
    override fun invoke(project: Project, editor: Editor, file: PsiFile) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val iElementType = IElementType("text", Language.findLanguageByID("yaml"))
        val psiExpression = factory.createDummyHolder("^$latestVersion", iElementType, null)
        element.replace(psiExpression)
    }
}