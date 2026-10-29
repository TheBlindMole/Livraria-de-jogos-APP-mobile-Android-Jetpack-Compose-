package com.theblindmole.gamevault.data.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.theblindmole.gamevault.domain.model.GameStatus

@Entity(tableName = "games", indices = [Index("name")])
data class GameEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val platform: String,
    val genre: String,
    @ColumnInfo(name = "release_year") val releaseYear: Int,
    val status: GameStatus,
    @ColumnInfo(name = "is_favorite") val isFavorite: Boolean,
    val rating: Int,
    val notes: String,
    @ColumnInfo(name = "cover_url") val coverUrl: String?,
    @ColumnInfo(name = "rawg_id") val rawgId: Int?,
    @ColumnInfo(name = "created_at") val createdAt: Long,
)
