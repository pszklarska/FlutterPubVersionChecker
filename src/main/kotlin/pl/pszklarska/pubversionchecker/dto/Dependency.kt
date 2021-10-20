package pl.pszklarska.pubversionchecker.dto

data class Dependency(
    val currentVersion: String,
    val latestVersion: String,
    val index: Int
)