package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapprickandmorty.data.model.Episode
import com.example.myapprickandmorty.ui.episodes.formatAirDate

private val EpBackground = Color(0xFF0F141E)
private val EpGreen = Color(0xFF97CE4C)
private val EpBlue = Color(0xFF00B5CC)
private val EpCard = Color(0xFF1E2738)

@Composable
fun EpisodesScreen(
    seasons: List<Int>,
    selectedSeason: Int,
    episodes: List<Episode>,
    totalEpisodes: Int,
    isLoading: Boolean,
    errorMessage: String?,
    onSeasonSelect: (Int) -> Unit,
    onEpisodeClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onCharactersClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val listState = rememberLazyListState()
    // Al cambiar de temporada, volver arriba
    LaunchedEffect(selectedSeason) { listState.scrollToItem(0) }

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selected = BottomTab.Episodes,
                onCharactersClick = onCharactersClick,
                onFavoritesClick = onFavoritesClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = EpBackground
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Episodios", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = EpGreen)
            Text(
                text = if (seasons.isEmpty()) "Registro de transmisiones interdimensionales"
                else "$totalEpisodes episodios · ${seasons.size} temporadas",
                fontSize = 14.sp,
                color = EpBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = EpGreen)
                }
                seasons.isEmpty() -> Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(errorMessage ?: "No hay episodios", color = Color.LightGray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onRetry) { Text("Reintentar", color = EpGreen) }
                }
                else -> {
                    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(seasons) { season ->
                            val isSelected = season == selectedSeason
                            FilterChip(
                                selected = isSelected,
                                onClick = { onSeasonSelect(season) },
                                label = { Text("Temporada $season", fontWeight = FontWeight.SemiBold) },
                                colors = FilterChipDefaults.filterChipColors(
                                    containerColor = Color.Transparent,
                                    labelColor = Color.White,
                                    selectedContainerColor = EpGreen.copy(alpha = 0.18f),
                                    selectedLabelColor = EpGreen
                                ),
                                border = FilterChipDefaults.filterChipBorder(
                                    enabled = true,
                                    selected = isSelected,
                                    borderColor = EpBlue.copy(alpha = 0.6f),
                                    selectedBorderColor = EpGreen
                                )
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        state = listState,
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(episodes, key = { it.id }) { episode ->
                            EpisodeCard(episode = episode, onClick = { onEpisodeClick(episode.id) })
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun EpisodeCard(episode: Episode, onClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EpCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EpGreen, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Número del episodio dentro de la temporada
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(56.dp)
                    .border(2.dp, EpBlue, RoundedCornerShape(12.dp))
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("EP", color = EpBlue, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    Text(
                        text = episode.number.toString().padStart(2, '0'),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = episode.name, color = Color.White, fontSize = 17.sp, fontWeight = FontWeight.Bold)
                Text(
                    text = "${episode.episode} • ${formatAirDate(episode.air_date)}",
                    color = Color.LightGray,
                    fontSize = 13.sp
                )
                Text(
                    text = "${episode.characterIds.size} personajes",
                    color = EpGreen,
                    fontSize = 13.sp
                )
            }
            Icon(imageVector = Icons.Default.ChevronRight, contentDescription = "Ver episodio", tint = EpBlue)
        }
    }
}
