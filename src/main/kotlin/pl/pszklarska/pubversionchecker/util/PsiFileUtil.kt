package pl.pszklarska.pubversionchecker.util

import com.intellij.psi.PsiFile

fun PsiFile.isPubspecFile(): Boolean {
    return fileType.defaultExtension == YML_EXTENSIONS && name.contains("pubspec")
}

fun PsiFile.readLines(): Map<String, Int> {
    val dependencyToLineIndex = mutableMapOf<String, Int>()
    var line = ""
    var counter = 0
    text.forEach {
        counter++
        if (it == '\n') {
            line = line.trim()
            if (!line.startsWith("#") && line.isPackageName()) {
                dependencyToLineIndex[line] = counter - 2
                printMessage("Found dependency: $line")
            }
            line = ""
        } else {
            line += it
        }
    }
    return dependencyToLineIndex
}