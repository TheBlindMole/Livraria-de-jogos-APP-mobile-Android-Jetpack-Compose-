package com.theblindmole.gamevault.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.ui.common.labelRes
import com.theblindmole.gamevault.ui.theme.Ink
import com.theblindmole.gamevault.ui.theme.SkyBlue

//etiqueta de estado
@Composable
fun StatusBadge(status: GameStatus, modifier: Modifier = Modifier) {
    val (container, content) = statusColors(status)
    Surface(
        color = container,
        contentColor = content,
        shape = CircleShape,
        modifier = modifier,
    ) {
        Text(
            text = stringResource(status.labelRes),
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
        )
    }
}

//seletor de estado
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatusSelector(
    selected: GameStatus,
    onSelect: (GameStatus) -> Unit,
    modifier: Modifier = Modifier,
) {
    val options = GameStatus.entries
    SingleChoiceSegmentedButtonRow(modifier = modifier.fillMaxWidth()) {
        options.forEachIndexed { index, status ->
            SegmentedButton(
                selected = status == selected,
                onClick = { onSelect(status) },
                shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                label = {
                    Text(
                        text = stringResource(status.labelRes),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
            )
        }
    }
}

@Composable
private fun statusColors(status: GameStatus): Pair<Color, Color> {
    val colors = MaterialTheme.colorScheme
    return when (status) {
        GameStatus.TO_PLAY -> colors.surfaceContainerHighest to colors.onSurface
        GameStatus.PLAYING -> SkyBlue to Ink
        GameStatus.COMPLETED -> colors.tertiary to colors.onTertiary
    }
}
