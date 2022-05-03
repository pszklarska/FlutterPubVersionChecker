package pl.pszklarska.pubversionchecker.annotator

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.ExternalAnnotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.application.runReadAction
import com.intellij.psi.PsiFile
import pl.pszklarska.pubversionchecker.dto.DependencyDescription
import pl.pszklarska.pubversionchecker.parsing.YamlParser
import pl.pszklarska.pubversionchecker.quickfix.UpdateAllDependenciesQuickFix
import pl.pszklarska.pubversionchecker.quickfix.UpdateDependencyQuickFix
import pl.pszklarska.pubversionchecker.util.VersionsRepository

class PubPackagesAnnotator : ExternalAnnotator<PubPackagesAnnotator.Info, PubPackagesAnnotator.Result>() {

    data class Info(val file: PsiFile)
    data class Result(val annotations: List<DependencyDescription>)

    override fun collectInformation(file: PsiFile): Info =
        runReadAction { Info(file) }

    override fun doAnnotate(collectedInfo: Info?): Result? {
        if (collectedInfo == null) return null

        val versionsRepository = VersionsRepository()
        val yamlParser = YamlParser(collectedInfo.file.text, versionsRepository)
        val annotations = yamlParser.inspectFile()
        return if (annotations.isNotEmpty()) Result(annotations) else null
    }

    override fun apply(file: PsiFile, annotationResult: Result?, holder: AnnotationHolder) {
        if (annotationResult == null) return

        annotationResult.annotations.forEach {
            val psiElement = file.findElementAt(it.dependency.index)!!
            holder.newAnnotation(HighlightSeverity.WARNING, "Latest available version is: ${it.latestVersion}")
                .range(psiElement)
                .newFix(UpdateDependencyQuickFix(it.dependency.packageName, it.latestVersion, psiElement))
                .registerFix()
                .newFix(UpdateAllDependenciesQuickFix(annotationResult.annotations))
                .registerFix()
                .create()
        }
    }
}