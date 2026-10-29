package com.theblindmole.gamevault.domain.util

import java.text.Normalizer

private val DIACRITICS = "\\p{Mn}+".toRegex()

//remove acentos e normaliza para comparacao
fun String.normalizedForSearch(): String =
    Normalizer.normalize(trim(), Normalizer.Form.NFD)
        .replace(DIACRITICS, "")
        .lowercase()
