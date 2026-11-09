package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.myapprickandmorty.data.model.Episode
import com.example.myapprickandmorty.data.model.RickCharacter

private val BackgroundColor = Color(0xFF0F141E)
private val RickGreen = Color(0xFF97CE4C)
private val CardColor = Color(0xFF1E2738)
private val PortalYellow = Color(0xFFF0E14A)
private val TextBlue = Color(0xFF00B5CC)
private val FavoriteRed = Color(0xFFE5484D)

@Composable
fun CharacterDetailScreen(
    character: RickCharacter?,
    firstEpisode: Episode?,
    lastEpisode: Episode?,
    isLoading: Boolean,
    errorMessage: String?,
    isFavorite: Boolean,
    onToggleFavorite: () -> Unit,
    onRetry: () -> Unit,
    onBackClick: () -> Unit,
    onCharactersClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                onCharactersClick = onCharactersClick,
                onFavoritesClick = onFavoritesClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = BackgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .border(1.dp, RickGreen, CircleShape)
                        .size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Regresar",
                        tint = RickGreen
                    )
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = "Ficha de Sujeto",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = RickGreen,
                    modifier = Modifier.weight(1f)
                )
                if (character != null) {
                    IconButton(
                        onClick = onToggleFavorite,
                        modifier = Modifier
                            .border(1.dp, if (isFavorite) FavoriteRed else RickGreen, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Filled.FavoriteBorder,
                            contentDescription = if (isFavorite) "Quitar de favoritos" else "Agregar a favoritos",
                            tint = if (isFavorite) FavoriteRed else RickGreen
                        )
                    }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))

            when {
                character != null -> CharacterDetailContent(character, firstEpisode, lastEpisode, isLoading)
                isLoading -> Box(
                    modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = RickGreen)
                }
                else -> Column(
                    modifier = Modifier.fillMaxWidth().padding(top = 80.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(errorMessage ?: "No se pudo cargar el personaje", color = Color.LightGray, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(12.dp))
                    OutlinedButton(onClick = onRetry) { Text("Reintentar", color = RickGreen) }
                }
            }
            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
private fun CharacterDetailContent(
    character: RickCharacter,
    firstEpisode: Episode?,
    lastEpisode: Episode?,
    isLoading: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(250.dp)
            .border(2.dp, RickGreen, RoundedCornerShape(16.dp))
            .clip(RoundedCornerShape(16.dp))
    ) {
        AsyncImage(
            model = character.image,
            contentDescription = character.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(12.dp)
                .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(12.dp))
                .border(1.dp, PortalYellow, RoundedCornerShape(12.dp))
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(text = "ID: ${character.id}", color = PortalYellow, fontWeight = FontWeight.Bold)
        }
    }
    Spacer(modifier = Modifier.height(24.dp))

    SectionCard {
        DetailRow(label = "Nombre", value = character.name)
        RowDivider()
        DetailRowStatus(label = "Estado", status = character.status)
        RowDivider()
        DetailRow(label = "Especie", value = character.species)
        RowDivider()
        DetailRow(label = "Tipo", value = character.type.ifBlank { "—" })
        RowDivider()
        DetailRow(label = "Género", value = translateGender(character.gender))
        RowDivider()
        DetailRow(label = "Origen", value = placeName(character.origin.name))
        RowDivider()
        DetailRow(label = "Última ubicación", value = placeName(character.location?.name))
    }

    Spacer(modifier = Modifier.height(24.dp))
    Text(text = "Apariciones", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = RickGreen)
    Spacer(modifier = Modifier.height(12.dp))

    val count = character.episodeIds.size
    SectionCard {
        DetailRow(label = "Episodios", value = if (count == 1) "1 episodio" else "$count episodios")
        if (count > 0) {
            RowDivider()
            EpisodeRow(label = "Primera aparición", episode = firstEpisode, isLoading = isLoading)
            if (count > 1) {
                RowDivider()
                EpisodeRow(label = "Última aparición", episode = lastEpisode, isLoading = isLoading)
            }
        }
    }
}

@Composable
private fun SectionCard(content: @Composable ColumnScope.() -> Unit) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardColor),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, RickGreen, RoundedCornerShape(16.dp))
    ) {
        Column(content = content)
    }
}

@Composable
private fun RowDivider() = HorizontalDivider(color = RickGreen.copy(alpha = 0.3f))

@Composable
private fun EpisodeRow(label: String, episode: Episode?, isLoading: Boolean) {
    Column(modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp)) {
        Text(text = label, color = Color.Gray, fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
        Spacer(modifier = Modifier.height(4.dp))
        when {
            episode != null -> {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = episode.episode,
                        color = TextBlue,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .border(1.dp, TextBlue, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = episode.name,
                        color = Color.White,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f)
                    )
                }
                Text(text = episode.air_date, color = Color.LightGray, fontSize = 13.sp)
            }
            isLoading -> Text(text = "Cargando…", color = Color.LightGray, fontSize = 14.sp)
            else -> Text(text = "No disponible", color = Color.LightGray, fontSize = 14.sp)
        }
    }
}

private fun translateGender(gender: String) = when (gender.lowercase()) {
    "male" -> "Masculino"
    "female" -> "Femenino"
    "genderless" -> "Sin género"
    else -> "Desconocido"
}

private fun translateStatus(status: String) = when (status.lowercase()) {
    "alive" -> "Vivo"
    "dead" -> "Muerto"
    else -> "Desconocido"
}

private fun placeName(name: String?) =
    if (name.isNullOrBlank() || name.equals("unknown", ignoreCase = true)) "Desconocido" else name

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Text(
            text = value,
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}

@Composable
fun DetailRowStatus(label: String, status: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, color = Color.Gray, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        Row(verticalAlignment = Alignment.CenterVertically) {
            val statusColor = when (status.lowercase()) {
                "alive", "vivo" -> Color.Green
                "dead", "muerto" -> Color.Red
                else -> Color.Gray
            }
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(statusColor)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(text = translateStatus(status), color = statusColor, fontSize = 16.sp, fontWeight = FontWeight.Bold)
        }
    }
}
