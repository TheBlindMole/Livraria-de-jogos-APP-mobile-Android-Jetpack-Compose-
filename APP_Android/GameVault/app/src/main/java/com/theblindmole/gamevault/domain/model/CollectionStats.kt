package com.theblindmole.gamevault.domain.model

data class CountEntry(val label: String, val count: Int)

data class CollectionStats(
    val total: Int = 0,
    val favorites: Int = 0,
    val countByStatus: Map<GameStatus, Int> = emptyMap(),
    val averageRating: Float = 0f,
    val ratedCount: Int = 0,
    val topPlatforms: List<CountEntry> = emptyList(),
    val topGenres: List<CountEntry> = emptyList(),
    val oldestYear: Int? = null,
    val newestYear: Int? = null,
) {
    val completed: Int
        get() = countByStatus[GameStatus.COMPLETED] ?: 0

    val completionRate: Float
        get() = if (total == 0) 0f else completed.toFloat() / total

    companion object {
        private const val TOP_LIMIT = 5

        fun from(games: List<Game>): CollectionStats {
            val rated = games.filter { it.rating > 0 }
            return CollectionStats(
                total = games.size,
                favorites = games.count { it.isFavorite },
                countByStatus = GameStatus.entries.associateWith { status -> games.count { it.status == status } },
                averageRating = if (rated.isEmpty()) 0f else rated.map { it.rating }.average().toFloat(),
                ratedCount = rated.size,
                topPlatforms = games.topBy { it.platform },
                topGenres = games.topBy { it.genre },
                oldestYear = games.minOfOrNull { it.releaseYear },
                newestYear = games.maxOfOrNull { it.releaseYear },
            )
        }

        //agrupa sem diferenciar maiusculas
        private fun List<Game>.topBy(selector: (Game) -> String): List<CountEntry> =
            groupBy { selector(it).trim().lowercase() }
                .map { (_, items) -> CountEntry(selector(items.first()).trim(), items.size) }
                .sortedWith(compareByDescending<CountEntry> { it.count }.thenBy { it.label.lowercase() })
                .take(TOP_LIMIT)
    }
}
