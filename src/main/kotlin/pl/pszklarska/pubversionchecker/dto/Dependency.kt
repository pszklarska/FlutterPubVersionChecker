package pl.pszklarska.pubversionchecker.dto

data class Dependency(
    val packageName: String,
    val currentVersion: String,
    val index: Int
)