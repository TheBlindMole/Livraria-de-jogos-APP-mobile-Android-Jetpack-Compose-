package com.theblindmole.gamevault.ui.common

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.domain.model.SearchError
import com.theblindmole.gamevault.domain.model.SortOption
import com.theblindmole.gamevault.domain.model.ThemeMode
import com.theblindmole.gamevault.domain.validation.ValidationError

@get:StringRes
val GameStatus.labelRes: Int
    get() = when (this) {
        GameStatus.TO_PLAY -> R.string.status_to_play
        GameStatus.PLAYING -> R.string.status_playing
        GameStatus.COMPLETED -> R.string.status_completed
    }

@get:StringRes
val SortOption.labelRes: Int
    get() = when (this) {
        SortOption.RECENT -> R.string.sort_recent
        SortOption.NAME_ASC -> R.string.sort_name_asc
        SortOption.NAME_DESC -> R.string.sort_name_desc
        SortOption.YEAR_DESC -> R.string.sort_year_desc
        SortOption.YEAR_ASC -> R.string.sort_year_asc
        SortOption.RATING_DESC -> R.string.sort_rating_desc
    }

@get:StringRes
val ThemeMode.labelRes: Int
    get() = when (this) {
        ThemeMode.SYSTEM -> R.string.theme_system
        ThemeMode.LIGHT -> R.string.theme_light
        ThemeMode.DARK -> R.string.theme_dark
    }

@get:StringRes
val SearchError.messageRes: Int
    get() = when (this) {
        SearchError.NETWORK -> R.string.search_error_network
        SearchError.UNKNOWN -> R.string.search_error_unknown
    }

@Composable
fun ValidationError.asText(): String = when (this) {
    ValidationError.Required -> stringResource(R.string.error_required)
    ValidationError.InvalidNumber -> stringResource(R.string.error_invalid_number)
    is ValidationError.TooLong -> stringResource(R.string.error_too_long, max)
    is ValidationError.OutOfRange -> stringResource(R.string.error_year_range, min, max)
}
