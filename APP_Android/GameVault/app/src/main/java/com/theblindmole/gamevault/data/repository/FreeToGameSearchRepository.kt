package com.theblindmole.gamevault.data.repository

import com.theblindmole.gamevault.data.remote.FreeToGameApi
import com.theblindmole.gamevault.data.remote.dto.FreeToGameDto
import com.theblindmole.gamevault.data.remote.toDomain
import com.theblindmole.gamevault.domain.model.SearchError
import com.theblindmole.gamevault.domain.model.SearchResult
import com.theblindmole.gamevault.domain.repository.GameSearchRepository
import kotlinx.serialization.SerializationException
import retrofit2.HttpException
import java.io.IOException
import kotlin.coroutines.cancellation.CancellationException

class FreeToGameSearchRepository(
    private val api: FreeToGameApi,
) : GameSearchRepository {

    private var cachedGames: List<FreeToGameDto>? = null

    override val isAvailable: Boolean
        get() = true

    override suspend fun search(query: String): SearchResult {
        val trimmedQuery = query.trim()
        if (trimmedQuery.length < 2) return SearchResult.Success(emptyList())

        return try {
            val games = cachedGames ?: api.getGames().also { cachedGames = it }
            val filtered = games.filter { game ->
                game.title.contains(trimmedQuery, ignoreCase = true) ||
                game.genre?.contains(trimmedQuery, ignoreCase = true) == true ||
                game.developer?.contains(trimmedQuery, ignoreCase = true) == true
            }.take(MAX_SEARCH_RESULTS).map { it.toDomain() }

            SearchResult.Success(filtered)
        } catch (e: CancellationException) {
            throw e
        } catch (e: IOException) {
            SearchResult.Failure(SearchError.NETWORK)
        } catch (e: HttpException) {
            SearchResult.Failure(SearchError.UNKNOWN)
        } catch (e: SerializationException) {
            SearchResult.Failure(SearchError.UNKNOWN)
        }
    }

    private companion object {
        const val MAX_SEARCH_RESULTS = 20
    }
}
