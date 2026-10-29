package com.theblindmole.gamevault.domain.model

import com.theblindmole.gamevault.domain.util.normalizedForSearch

data class GameFilter(
    val query: String = "",
    val status: GameStatus? = null,
    val platform: String? = null,
    val genre: String? = null,
    val favoritesOnly: Boolean = false,
) {
    val hasActiveFilters: Boolean
        get() = status != null || platform != null || genre != null || favoritesOnly
}

//pesquisa filtros e ordenacao
fun List<Game>.filterAndSort(filter: GameFilter, sort: SortOption): List<Game> {
    val query = filter.query.normalizedForSearch()
    return asSequence()
        .filter { query.isEmpty() || it.name.normalizedForSearch().contains(query) }
        .filter { filter.status == null || it.status == filter.status }
        .filter { filter.platform == null || it.platform.equals(filter.platform, ignoreCase = true) }
        .filter { filter.genre == null || it.genre.equals(filter.genre, ignoreCase = true) }
        .filter { !filter.favoritesOnly || it.isFavorite }
        .sortedWith(sort.comparator)
        .toList()
}

//valores unicos para opcoes de filtro
fun List<Game>.distinctValues(selector: (Game) -> String): List<String> =
    map { selector(it).trim() }
        .filter { it.isNotEmpty() }
        .distinctBy { it.lowercase() }
        .sortedWith(String.CASE_INSENSITIVE_ORDER)
