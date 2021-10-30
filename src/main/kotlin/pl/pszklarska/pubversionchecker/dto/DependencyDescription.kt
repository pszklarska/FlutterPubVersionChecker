package pl.pszklarska.pubversionchecker.dto

data class DependencyDescription(
    val dependency: Dependency,
    val latestVersion: String
)