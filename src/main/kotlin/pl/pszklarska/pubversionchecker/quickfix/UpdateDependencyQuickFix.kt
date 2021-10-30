package pl.pszklarska.pubversionchecker.quickfix

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType


const val DEBUG_NAME = "text"
const val YAML = "yaml"

class UpdateDependencyQuickFix(
    psiElement: PsiElement,
    private val latestVersion: String,
    private val packageName: String
) :
    LocalQuickFixOnPsiElement(psiElement) {

    private val description = "Update $packageName"

    override fun getFamilyName(): String = description

    override fun getText(): String = description

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val iElementType = IElementType(DEBUG_NAME, Language.findLanguageByID(YAML))
        val psiExpression = factory.createDummyHolder("^$latestVersion", iElementType, null)
        startElement.replace(psiExpression)
    }
}