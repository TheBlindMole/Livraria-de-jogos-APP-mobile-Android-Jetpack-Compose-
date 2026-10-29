package com.theblindmole.gamevault.ui.theme

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp

//paleta principal
val Ink = Color(0xFF171A21)
val SkyBlue = Color(0xFF66C0F4)
val Navy = Color(0xFF1B2838)
val Steel = Color(0xFF2A475E)
val Mist = Color(0xFFC7D5E0)

//tons derivados para o modo escuro
val InkNavy = lerp(Ink, Navy, 0.5f)
val NavySteel = lerp(Navy, Steel, 0.5f)
val SteelMist = lerp(Steel, Mist, 0.35f)
val MistMuted = lerp(Mist, Steel, 0.3f)

//tons derivados para o modo claro
val MistSoft = lerp(Mist, Color.White, 0.35f)
val MistLight = lerp(Mist, Color.White, 0.5f)
val MistPale = lerp(Mist, Color.White, 0.7f)
val MistLighter = lerp(Mist, Color.White, 0.8f)
val MistLightest = lerp(Mist, Color.White, 0.9f)

//cores de erro
val ErrorRed = Color(0xFFE5534B)
val ErrorRedDeep = lerp(ErrorRed, Ink, 0.25f)
val ErrorContainerDark = lerp(Ink, ErrorRed, 0.3f)
val ErrorContainerLight = lerp(MistLighter, ErrorRed, 0.2f)
