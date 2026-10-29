package com.theblindmole.gamevault.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [GameEntity::class], version = 1, exportSchema = false)
@TypeConverters(Converters::class)
abstract class GameDatabase : RoomDatabase() {

    abstract fun gameDao(): GameDao

    companion object {
        private const val DATABASE_NAME = "game_vault.db"

        fun create(context: Context): GameDatabase =
            Room.databaseBuilder(context, GameDatabase::class.java, DATABASE_NAME).build()
    }
}
