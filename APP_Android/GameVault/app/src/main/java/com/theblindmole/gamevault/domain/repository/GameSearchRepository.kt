package com.theblindmole.gamevault.domain.repository

import com.theblindmole.gamevault.domain.model.SearchResult

interface GameSearchRepository {
    val isAvailable: Boolean
    suspend fun search(query: String): SearchResult
}
