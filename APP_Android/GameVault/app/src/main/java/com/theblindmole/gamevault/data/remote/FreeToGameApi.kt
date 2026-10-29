package com.theblindmole.gamevault.data.remote

import com.theblindmole.gamevault.data.remote.dto.FreeToGameDto
import retrofit2.http.GET

interface FreeToGameApi {

    @GET("games")
    suspend fun getGames(): List<FreeToGameDto>

    companion object {
        const val BASE_URL = "https://www.freetogame.com/api/"
    }
}
