package pl.pszklarska.pubversionchecker.dto

import kotlinx.serialization.Serializable

@Serializable
data class Response(
    val latest: Latest
)

@Serializable
data class Latest(
    val version: String
)