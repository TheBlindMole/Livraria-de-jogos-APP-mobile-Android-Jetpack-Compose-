package com.theblindmole.gamevault.data.remote

import com.theblindmole.gamevault.data.remote.dto.FreeToGameDto
import com.theblindmole.gamevault.domain.model.RemoteGame

private const val YEAR_LENGTH = 4

fun FreeToGameDto.toDomain(): RemoteGame = RemoteGame(
    id = id,
    name = title,
    releaseYear = releaseDate?.take(YEAR_LENGTH)?.toIntOrNull(),
    coverUrl = thumbnail,
    platforms = platform?.split(",")?.map { it.trim() }?.filter { it.isNotBlank() } ?: emptyList(),
    genres = genre?.takeIf { it.isNotBlank() }?.let { listOf(it.trim()) } ?: emptyList(),
)
