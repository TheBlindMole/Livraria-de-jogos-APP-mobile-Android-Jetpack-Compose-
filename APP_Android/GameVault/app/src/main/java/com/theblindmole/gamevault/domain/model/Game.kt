package com.theblindmole.gamevault.domain.model

data class Game(
    val id: Long = 0,
    val name: String,
    val platform: String,
    val genre: String,
    val releaseYear: Int,
    val status: GameStatus,
    val isFavorite: Boolean = false,
    val rating: Int = 0,
    val notes: String = "",
    val coverUrl: String? = null,
    val rawgId: Int? = null,
    val createdAt: Long = System.currentTimeMillis(),
) {
    companion object {
        const val MAX_RATING = 5
    }
}
