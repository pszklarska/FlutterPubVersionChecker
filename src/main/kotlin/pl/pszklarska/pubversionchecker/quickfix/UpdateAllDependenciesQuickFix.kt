package pl.pszklarska.pubversionchecker.quickfix

import com.intellij.codeInspection.LocalQuickFixOnPsiElement
import com.intellij.lang.Language
import com.intellij.openapi.project.Project
import com.intellij.psi.JavaPsiFacade
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import pl.pszklarska.pubversionchecker.dto.DependencyDescription
import pl.pszklarska.pubversionchecker.util.findVersionIndexInFile


class UpdateAllDependenciesQuickFix(
    psiElement: PsiElement,
    private val dependencies: List<DependencyDescription>,
) :
    LocalQuickFixOnPsiElement(psiElement) {
    private val description = "Update\u200B all" // zero-length whitespace to place "Update all" below other fixes

    override fun getFamilyName(): String = description

    override fun getText(): String = description

    override fun invoke(project: Project, file: PsiFile, startElement: PsiElement, endElement: PsiElement) {
        val factory = JavaPsiFacade.getElementFactory(project)
        val iElementType = IElementType(DEBUG_NAME, Language.findLanguageByID(YAML))
        dependencies.forEach {
            val newElement = factory.createDummyHolder("^${it.latestVersion}", iElementType, null)
            val newIndex = file.text.findVersionIndexInFile(it.dependency.packageName)
            val psiElement = file.findElementAt(newIndex)!!
            psiElement.replace(newElement)
        }
    }

}