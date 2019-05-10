package pl.pszklarska.pubversionchecker

import com.intellij.codeInspection.InspectionToolProvider

class PackagesInspectionProvider : InspectionToolProvider {
    override fun getInspectionClasses(): Array<Class<*>> {
        return arrayOf(PackagesInspection::class.java)
    }
}