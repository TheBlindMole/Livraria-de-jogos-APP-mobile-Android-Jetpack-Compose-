package com.theblindmole.gamevault.domain.model

data class RemoteGame(
    val id: Int,
    val name: String,
    val releaseYear: Int?,
    val coverUrl: String?,
    val platforms: List<String>,
    val genres: List<String>,
)

sealed interface SearchResult {
    data class Success(val games: List<RemoteGame>) : SearchResult
    data class Failure(val error: SearchError) : SearchResult
}

enum class SearchError {
    NETWORK,
    UNKNOWN,
}
