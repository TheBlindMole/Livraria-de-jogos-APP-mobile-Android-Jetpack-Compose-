package com.theblindmole.gamevault.data.local

import com.theblindmole.gamevault.domain.model.Game

fun GameEntity.toDomain(): Game = Game(
    id = id,
    name = name,
    platform = platform,
    genre = genre,
    releaseYear = releaseYear,
    status = status,
    isFavorite = isFavorite,
    rating = rating,
    notes = notes,
    coverUrl = coverUrl,
    rawgId = rawgId,
    createdAt = createdAt,
)

fun Game.toEntity(): GameEntity = GameEntity(
    id = id,
    name = name,
    platform = platform,
    genre = genre,
    releaseYear = releaseYear,
    status = status,
    isFavorite = isFavorite,
    rating = rating,
    notes = notes,
    coverUrl = coverUrl,
    rawgId = rawgId,
    createdAt = createdAt,
)
