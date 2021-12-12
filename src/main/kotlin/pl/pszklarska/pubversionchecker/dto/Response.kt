package pl.pszklarska.pubversionchecker.dto

data class Response(
    val latest: Latest
)

data class Latest(
    val version: String
)