package com.theblindmole.gamevault.data.repository

import com.theblindmole.gamevault.data.local.GameDao
import com.theblindmole.gamevault.data.local.toDomain
import com.theblindmole.gamevault.data.local.toEntity
import com.theblindmole.gamevault.data.storage.CoverStorage
import com.theblindmole.gamevault.domain.model.Game
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.repository.GameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class OfflineGameRepository(
    private val dao: GameDao,
    private val coverStorage: CoverStorage,
) : GameRepository {

    override fun observeGames(): Flow<List<Game>> =
        dao.observeAll().map { entities -> entities.map { it.toDomain() } }

    override fun observeGame(id: Long): Flow<Game?> =
        dao.observeById(id).map { it?.toDomain() }

    override suspend fun getGame(id: Long): Game? = dao.getById(id)?.toDomain()

    override suspend fun save(game: Game): Long {
        if (game.id == 0L) return dao.insert(game.toEntity())

        val previousCover = dao.getById(game.id)?.coverUrl
        dao.update(game.toEntity())
        if (previousCover != game.coverUrl) coverStorage.delete(previousCover)
        return game.id
    }

    override suspend fun setFavorite(id: Long, favorite: Boolean) {
        dao.updateFavorite(id, favorite)
    }

    override suspend fun setStatus(id: Long, status: GameStatus) {
        dao.updateStatus(id, status)
    }

    override suspend fun setRating(id: Long, rating: Int) {
        dao.updateRating(id, rating.coerceIn(0, Game.MAX_RATING))
    }

    override suspend fun delete(id: Long) {
        val cover = dao.getById(id)?.coverUrl
        dao.deleteById(id)
        coverStorage.delete(cover)
    }

    override suspend fun isDuplicate(name: String, platform: String, excludeId: Long): Boolean =
        dao.countDuplicates(name, platform, excludeId) > 0

    override suspend fun importCover(sourceUri: String): String? = coverStorage.import(sourceUri)

    override suspend fun deleteUnusedCovers() {
        coverStorage.deleteAllExcept(dao.getCoverUrls())
    }
}
