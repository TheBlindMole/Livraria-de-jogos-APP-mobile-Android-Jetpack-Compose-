package com.theblindmole.gamevault.domain.repository

import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

interface GameRepository {
    fun observeGames(): Flow<List<Game>>
    fun observeGame(id: Long): Flow<Game?>
    suspend fun getGame(id: Long): Game?
    suspend fun save(game: Game): Long
    suspend fun setFavorite(id: Long, favorite: Boolean)
    suspend fun setStatus(id: Long, status: GameStatus)
    suspend fun setRating(id: Long, rating: Int)
    suspend fun delete(id: Long)
    suspend fun isDuplicate(name: String, platform: String, excludeId: Long): Boolean
    suspend fun importCover(sourceUri: String): String?
    suspend fun deleteUnusedCovers()
}
