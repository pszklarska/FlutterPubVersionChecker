package pl.pszklarska.pubversionchecker.util

import com.intellij.openapi.diagnostic.Logger
import pl.pszklarska.pubversionchecker.annotator.PubPackagesAnnotator

private val LOG = Logger.getInstance(PubPackagesAnnotator::class.java)

fun printMessage(message: String) {
    println(message)
    LOG.info(message)
}
