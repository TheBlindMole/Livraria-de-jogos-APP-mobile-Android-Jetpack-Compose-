package com.theblindmole.gamevault.ui.screens.stats

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.theblindmole.gamevault.R
import com.theblindmole.gamevault.domain.model.CollectionStats
import com.theblindmole.gamevault.domain.model.CountEntry
import com.theblindmole.gamevault.domain.model.GameStatus
import com.theblindmole.gamevault.ui.AppViewModelProvider
import com.theblindmole.gamevault.ui.common.labelRes
import com.theblindmole.gamevault.ui.components.BackButton
import com.theblindmole.gamevault.ui.components.EmptyState
import com.theblindmole.gamevault.ui.components.LoadingIndicator
import com.theblindmole.gamevault.ui.components.RatingBar
import com.theblindmole.gamevault.ui.components.SectionCard
import kotlin.math.roundToInt

private const val PERCENT = 100

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onBack: () -> Unit,
    viewModel: StatsViewModel = viewModel(factory = AppViewModelProvider.Factory),
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.stats_title)) },
                navigationIcon = { BackButton(onClick = onBack) },
            )
        },
    ) { innerPadding ->
        val contentModifier = Modifier.padding(innerPadding)
        when (val state = uiState) {
            StatsUiState.Loading -> LoadingIndicator(contentModifier)
            is StatsUiState.Ready ->
                if (state.stats.total == 0) {
                    EmptyState(
                        icon = Icons.Outlined.BarChart,
                        title = stringResource(R.string.empty_collection_title),
                        message = stringResource(R.string.stats_empty_message),
                        modifier = contentModifier,
                    )
                } else {
                    StatsContent(stats = state.stats, modifier = contentModifier)
                }
        }
    }
}

@Composable
private fun StatsContent(stats: CollectionStats, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        //resumo
        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            SummaryTile(
                value = stats.total.toString(),
                label = stringResource(R.string.stats_total),
                modifier = Modifier.weight(1f),
            )
            SummaryTile(
                value = stats.favorites.toString(),
                label = stringResource(R.string.stats_favorites),
                modifier = Modifier.weight(1f),
            )
            SummaryTile(
                value = stringResource(R.string.percent_value, (stats.completionRate * PERCENT).roundToInt()),
                label = stringResource(R.string.stats_completed),
                modifier = Modifier.weight(1f),
            )
        }

        //distribuicao por estado
        SectionCard(title = stringResource(R.string.stats_by_status)) {
            GameStatus.entries.forEach { status ->
                val count = stats.countByStatus[status] ?: 0
                BarRow(
                    label = stringResource(status.labelRes),
                    count = count,
                    fraction = count.toFloat() / stats.total,
                )
            }
        }

        //avaliacao media
        SectionCard(title = stringResource(R.string.stats_average_rating)) {
            if (stats.ratedCount == 0) {
                Text(
                    text = stringResource(R.string.stats_no_ratings),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    Text(
                        text = stringResource(R.string.stats_rating_value, stats.averageRating),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    RatingBar(rating = stats.averageRating.roundToInt(), starSize = 20.dp)
                }
                Text(
                    text = pluralStringResource(R.plurals.stats_rated_count, stats.ratedCount, stats.ratedCount),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        RankingCard(title = stringResource(R.string.stats_top_platforms), entries = stats.topPlatforms)
        RankingCard(title = stringResource(R.string.stats_top_genres), entries = stats.topGenres)

        //intervalo de anos
        if (stats.oldestYear != null && stats.newestYear != null) {
            SectionCard(title = stringResource(R.string.stats_years)) {
                Text(
                    text = stringResource(R.string.stats_year_span, stats.oldestYear, stats.newestYear),
                    style = MaterialTheme.typography.bodyLarge,
                )
            }
        }
    }
}

@Composable
private fun SummaryTile(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

//ranking relativo ao valor mais alto
@Composable
private fun RankingCard(title: String, entries: List<CountEntry>) {
    if (entries.isEmpty()) return
    val highest = entries.first().count.toFloat()
    SectionCard(title = title) {
        entries.forEach { entry ->
            BarRow(label = entry.label, count = entry.count, fraction = entry.count / highest)
        }
    }
}

@Composable
private fun BarRow(label: String, count: Int, fraction: Float) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f),
            )
            Text(text = count.toString(), style = MaterialTheme.typography.labelLarge)
        }
        LinearProgressIndicator(
            progress = { fraction },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
            strokeCap = StrokeCap.Round,
            gapSize = 0.dp,
            drawStopIndicator = {},
        )
    }
}
