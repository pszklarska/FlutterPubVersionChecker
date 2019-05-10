package pl.pszklarska.pubversionchecker

data class Response(
    val latest: Latest
)

data class Latest(
    val version: String
)