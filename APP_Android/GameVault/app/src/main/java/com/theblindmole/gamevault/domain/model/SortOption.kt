package com.theblindmole.gamevault.domain.model

enum class SortOption(val comparator: Comparator<Game>) {
    RECENT(compareByDescending { it.createdAt }),
    NAME_ASC(compareBy<Game, String>(String.CASE_INSENSITIVE_ORDER) { it.name }),
    NAME_DESC(compareByDescending<Game, String>(String.CASE_INSENSITIVE_ORDER) { it.name }),
    YEAR_DESC(compareByDescending<Game> { it.releaseYear }.thenBy { it.name.lowercase() }),
    YEAR_ASC(compareBy<Game> { it.releaseYear }.thenBy { it.name.lowercase() }),
    RATING_DESC(compareByDescending<Game> { it.rating }.thenBy { it.name.lowercase() }),
}
