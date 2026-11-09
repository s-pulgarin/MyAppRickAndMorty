package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapprickandmorty.data.model.Episode
import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.ui.episodes.formatAirDate

private val EdBackground = Color(0xFF0F141E)
private val EdGreen = Color(0xFF97CE4C)
private val EdBlue = Color(0xFF00B5CC)
private val EdCard = Color(0xFF1E2738)
private val EdYellow = Color(0xFFF0E14A)

@Composable
fun EpisodeDetailScreen(
    episode: Episode?,
    cast: List<RickCharacter>,
    isLoading: Boolean,
    isLoadingCast: Boolean,
    errorMessage: String?,
    onRetry: () -> Unit,
    onCharacterClick: (Int) -> Unit,
    onBackClick: () -> Unit,
    onCharactersClick: () -> Unit,
    onEpisodesClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                onCharactersClick = onCharactersClick,
                onEpisodesClick = onEpisodesClick,
                onFavoritesClick = onFavoritesClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = EdBackground
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 24.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            fullWidth {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    IconButton(
                        onClick = onBackClick,
                        modifier = Modifier
                            .border(1.dp, EdGreen, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = EdGreen)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Episodio", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = EdGreen)
                }
            }

            when {
                episode != null -> {
                    fullWidth { EpisodeHeaderCard(episode) }
                    fullWidth {
                        Text(
                            text = "Reparto",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = EdGreen,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    when {
                        isLoadingCast -> fullWidth {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = EdGreen)
                            }
                        }
                        cast.isEmpty() && errorMessage != null -> fullWidth {
                            RetryBlock(errorMessage, onRetry)
                        }
                        else -> items(cast, key = { it.id }) { character ->
                            CastMember(character = character, onClick = { onCharacterClick(character.id) })
                        }
                    }
                }
                isLoading -> fullWidth {
                    Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = EdGreen)
                    }
                }
                else -> fullWidth {
                    RetryBlock(errorMessage ?: "No se pudo cargar el episodio", onRetry)
                }
            }
        }
    }
}

private fun androidx.compose.foundation.lazy.grid.LazyGridScope.fullWidth(
    content: @Composable () -> Unit
) = item(span = { GridItemSpan(maxLineSpan) }) { content() }

@Composable
private fun EpisodeHeaderCard(episode: Episode) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = EdCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, EdGreen, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = episode.episode,
                    color = EdYellow,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
                        .border(1.dp, EdYellow, RoundedCornerShape(8.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = "Temporada ${episode.season} · Episodio ${episode.number}",
                    color = EdBlue,
                    fontSize = 14.sp
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = episode.name, color = Color.White, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = "Emitido el ${formatAirDate(episode.air_date)}", color = Color.LightGray, fontSize = 14.sp)
            Text(
                text = "${episode.characterIds.size} personajes en este episodio",
                color = EdGreen,
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun CastMember(character: RickCharacter, onClick: () -> Unit) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onClick() }
            .padding(vertical = 4.dp)
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(84.dp)
                .clip(CircleShape)
                .border(2.dp, EdBlue, CircleShape)
        )
        Spacer(modifier = Modifier.height(6.dp))
        Text(
            text = character.name,
            color = Color.White,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun RetryBlock(message: String, onRetry: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxWidth().padding(top = 40.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(message, color = Color.LightGray, fontSize = 16.sp)
        Spacer(modifier = Modifier.height(12.dp))
        OutlinedButton(onClick = onRetry) { Text("Reintentar", color = EdGreen) }
    }
}
