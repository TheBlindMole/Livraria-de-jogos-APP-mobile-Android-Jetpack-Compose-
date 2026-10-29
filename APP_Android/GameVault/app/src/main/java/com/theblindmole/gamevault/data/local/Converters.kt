package com.theblindmole.gamevault.data.local

import androidx.room.TypeConverter
import com.theblindmole.gamevault.domain.model.GameStatus

class Converters {

    @TypeConverter
    fun fromStatus(status: GameStatus): String = status.name

    @TypeConverter
    fun toStatus(value: String): GameStatus =
        GameStatus.entries.firstOrNull { it.name == value } ?: GameStatus.TO_PLAY
}
