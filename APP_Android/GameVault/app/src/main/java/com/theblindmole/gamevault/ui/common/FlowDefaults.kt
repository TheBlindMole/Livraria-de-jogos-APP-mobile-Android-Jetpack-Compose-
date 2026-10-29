package com.theblindmole.gamevault.ui.common

import kotlinx.coroutines.flow.SharingStarted

private const val STOP_TIMEOUT_MILLIS = 5_000L

//mantem o fluxo ativo durante rotacoes de ecra
val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(STOP_TIMEOUT_MILLIS)
