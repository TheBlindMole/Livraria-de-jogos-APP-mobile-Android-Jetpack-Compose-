package com.theblindmole.gamevault.di

import android.content.Context
import com.theblindmole.gamevault.data.local.GameDatabase
import com.theblindmole.gamevault.data.preferences.DataStoreSettingsRepository
import com.theblindmole.gamevault.data.remote.FreeToGameApi
import com.theblindmole.gamevault.data.repository.FreeToGameSearchRepository
import com.theblindmole.gamevault.data.repository.OfflineGameRepository
import com.theblindmole.gamevault.data.storage.CoverStorage
import com.theblindmole.gamevault.domain.repository.GameRepository
import com.theblindmole.gamevault.domain.repository.GameSearchRepository
import com.theblindmole.gamevault.domain.repository.SettingsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import java.util.concurrent.TimeUnit

//injecao de dependencias manual
class AppContainer(context: Context) {

    private val appContext = context.applicationContext

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    //rede
    private val json = Json {
        ignoreUnknownKeys = true
        coerceInputValues = true
    }

    private val okHttpClient: OkHttpClient by lazy {
        OkHttpClient.Builder()
            .connectTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .readTimeout(TIMEOUT_SECONDS, TimeUnit.SECONDS)
            .build()
    }

    private val freeToGameApi: FreeToGameApi by lazy {
        Retrofit.Builder()
            .baseUrl(FreeToGameApi.BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(json.asConverterFactory(JSON_MEDIA_TYPE.toMediaType()))
            .build()
            .create(FreeToGameApi::class.java)
    }

    //armazenamento local
    private val database: GameDatabase by lazy { GameDatabase.create(appContext) }

    private val coverStorage: CoverStorage by lazy { CoverStorage(appContext) }

    //repositorios
    val gameRepository: GameRepository by lazy {
        OfflineGameRepository(database.gameDao(), coverStorage)
    }

    val gameSearchRepository: GameSearchRepository by lazy {
        FreeToGameSearchRepository(freeToGameApi)
    }

    val settingsRepository: SettingsRepository by lazy {
        DataStoreSettingsRepository(appContext)
    }

    private companion object {
        const val TIMEOUT_SECONDS = 15L
        const val JSON_MEDIA_TYPE = "application/json"
    }
}
