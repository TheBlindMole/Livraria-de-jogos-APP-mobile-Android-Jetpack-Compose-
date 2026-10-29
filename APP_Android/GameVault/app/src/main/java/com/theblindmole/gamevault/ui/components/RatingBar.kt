package com.theblindmole.gamevault.ui.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.Game

//avaliacao de 0 a 5 estrelas
@Composable
fun RatingBar(
    rating: Int,
    modifier: Modifier = Modifier,
    starSize: Dp = 18.dp,
    onRatingChange: ((Int) -> Unit)? = null,
) {
    Row(modifier = modifier) {
        for (value in 1..Game.MAX_RATING) {
            val filled = value <= rating
            val icon = if (filled) Icons.Filled.Star else Icons.Filled.StarBorder
            val tint = if (filled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant

            if (onRatingChange != null) {
                IconButton(onClick = { onRatingChange(if (value == rating) 0 else value) }) {
                    Icon(
                        imageVector = icon,
                        contentDescription = stringResource(R.string.rating_star, value),
                        tint = tint,
                        modifier = Modifier.size(starSize),
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = tint,
                    modifier = Modifier.size(starSize),
                )
            }
        }
    }
}
