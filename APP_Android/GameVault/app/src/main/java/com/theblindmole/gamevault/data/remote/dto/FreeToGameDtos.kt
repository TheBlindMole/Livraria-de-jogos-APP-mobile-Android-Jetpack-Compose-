package com.theblindmole.gamevault.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FreeToGameDto(
    val id: Int,
    val title: String,
    @SerialName("thumbnail") val thumbnail: String? = null,
    @SerialName("short_description") val shortDescription: String? = null,
    val genre: String? = null,
    val platform: String? = null,
    val publisher: String? = null,
    val developer: String? = null,
    @SerialName("release_date") val releaseDate: String? = null,
)
