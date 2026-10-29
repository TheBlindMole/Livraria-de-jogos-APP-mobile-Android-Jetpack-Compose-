package com.theblindmole.gamevault

import android.app.Application
import coil3.ImageLoader
import coil3.PlatformContext
import coil3.SingletonImageLoader
import coil3.request.crossfade
import com.theblindmole.gamevault.di.AppContainer
import kotlinx.coroutines.launch

class GameVaultApp : Application(), SingletonImageLoader.Factory {

    lateinit var container: AppContainer
        private set

    override fun onCreate() {
        super.onCreate()
        container = AppContainer(this)

        //limpeza de capas sem jogo associado
        container.applicationScope.launch {
            container.gameRepository.deleteUnusedCovers()
        }
    }

    //carregamento de imagens
    override fun newImageLoader(context: PlatformContext): ImageLoader =
        ImageLoader.Builder(context)
            .crossfade(true)
            .build()
}
