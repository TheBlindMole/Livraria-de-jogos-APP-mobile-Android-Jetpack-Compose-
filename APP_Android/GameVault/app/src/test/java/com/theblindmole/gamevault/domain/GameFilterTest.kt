package com.theblindmole.gamevault.domain

import com.theblindmole.gamevault.domain.model.CollectionStats
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameFilter
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.model.SortOption
import com.theblindmole.gamevault.domain.model.filterAndSort
import org.junit.Assert.assertEquals
import org.junit.Test

class GameFilterTest {

    private val games = listOf(
        game(1, "Pokémon Scarlet", "Switch", 2022, GameStatus.PLAYING, favorite = true, rating = 4),
        game(2, "Hades", "PC", 2020, GameStatus.COMPLETED, rating = 5),
        game(3, "Celeste", "PC", 2018, GameStatus.TO_PLAY),
    )

    @Test
    fun searchIgnoresCaseAndAccents() {
        val result = games.filterAndSort(GameFilter(query = "pokemon"), SortOption.NAME_ASC)
        assertEquals(listOf(1L), result.map { it.id })
    }

    @Test
    fun filtersCombine() {
        val filter = GameFilter(platform = "pc", status = GameStatus.COMPLETED)
        val result = games.filterAndSort(filter, SortOption.NAME_ASC)
        assertEquals(listOf(2L), result.map { it.id })
    }

    @Test
    fun favoritesOnlyFilter() {
        val result = games.filterAndSort(GameFilter(favoritesOnly = true), SortOption.NAME_ASC)
        assertEquals(listOf(1L), result.map { it.id })
    }

    @Test
    fun sortByYearDescending() {
        val result = games.filterAndSort(GameFilter(), SortOption.YEAR_DESC)
        assertEquals(listOf(1L, 2L, 3L), result.map { it.id })
    }

    @Test
    fun statsAreCalculated() {
        val stats = CollectionStats.from(games)
        assertEquals(3, stats.total)
        assertEquals(1, stats.favorites)
        assertEquals(4.5f, stats.averageRating)
        assertEquals("PC", stats.topPlatforms.first().label)
        assertEquals(2018, stats.oldestYear)
    }

    private fun game(
        id: Long,
        name: String,
        platform: String,
        year: Int,
        status: GameStatus,
        favorite: Boolean = false,
        rating: Int = 0,
    ) = Game(
        id = id,
        name = name,
        platform = platform,
        genre = "Action",
        releaseYear = year,
        status = status,
        isFavorite = favorite,
        rating = rating,
        createdAt = id,
    )
}
