package pl.pszklarska.pubversionchecker.util

import com.intellij.openapi.diagnostic.Logger
import pl.pszklarska.pubversionchecker.inspection.PubPackagesInspection

private val LOG = Logger.getInstance(PubPackagesInspection::class.java)

fun printMessage(message: String) {
    println(message)
    LOG.info(message)
}
