package com.theblindmole.gamevault.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.theblindmole.gamevault.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {

    @Query("SELECT * FROM games")
    fun observeAll(): Flow<List<GameEntity>>

    @Query("SELECT * FROM games WHERE id = :id")
    fun observeById(id: Long): Flow<GameEntity?>

    @Query("SELECT * FROM games WHERE id = :id")
    suspend fun getById(id: Long): GameEntity?

    @Insert
    suspend fun insert(game: GameEntity): Long

    @Update
    suspend fun update(game: GameEntity)

    @Query("UPDATE games SET is_favorite = :favorite WHERE id = :id")
    suspend fun updateFavorite(id: Long, favorite: Boolean)

    @Query("UPDATE games SET status = :status WHERE id = :id")
    suspend fun updateStatus(id: Long, status: GameStatus)

    @Query("UPDATE games SET rating = :rating WHERE id = :id")
    suspend fun updateRating(id: Long, rating: Int)

    @Query("DELETE FROM games WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query(
        "SELECT COUNT(*) FROM games WHERE name = :name COLLATE NOCASE " +
            "AND platform = :platform COLLATE NOCASE AND id != :excludeId",
    )
    suspend fun countDuplicates(name: String, platform: String, excludeId: Long): Int

    @Query("SELECT cover_url FROM games WHERE cover_url IS NOT NULL")
    suspend fun getCoverUrls(): List<String>
}
