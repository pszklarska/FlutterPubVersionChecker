package pl.pszklarska.pubversionchecker.util

import com.intellij.psi.PsiFile

const val YML_EXTENSIONS = "yml"

fun PsiFile.isPubspecFile(): Boolean {
    return fileType.defaultExtension == YML_EXTENSIONS && name.contains("pubspec")
}
