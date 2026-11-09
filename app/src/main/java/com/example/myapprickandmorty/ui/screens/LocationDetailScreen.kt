package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Public
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapprickandmorty.data.model.Location
import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.ui.locations.translateDimension
import com.example.myapprickandmorty.ui.locations.translateLocationType

private val LocBackground = Color(0xFF0F141E)
private val LocGreen = Color(0xFF97CE4C)
private val LocBlue = Color(0xFF00B5CC)
private val LocCard = Color(0xFF1E2738)

@Composable
fun LocationDetailScreen(
    location: Location?,
    residents: List<RickCharacter>,
    isLoading: Boolean,
    isLoadingResidents: Boolean,
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
        containerColor = LocBackground
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
                            .border(1.dp, LocGreen, CircleShape)
                            .size(40.dp)
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Regresar", tint = LocGreen)
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(text = "Ubicación", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = LocGreen)
                }
            }

            when {
                location != null -> {
                    fullWidth { LocationHeaderCard(location) }
                    fullWidth {
                        Column(modifier = Modifier.padding(top = 8.dp)) {
                            Text(text = "Residentes", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = LocGreen)
                            Text(
                                text = "Personajes vistos aquí por última vez",
                                fontSize = 13.sp,
                                color = Color.Gray
                            )
                        }
                    }
                    when {
                        isLoadingResidents -> fullWidth {
                            Box(Modifier.fillMaxWidth().padding(24.dp), contentAlignment = Alignment.Center) {
                                CircularProgressIndicator(color = LocGreen)
                            }
                        }
                        residents.isEmpty() && errorMessage != null -> fullWidth { RetryBlock(errorMessage, onRetry) }
                        residents.isEmpty() -> fullWidth {
                            Text(
                                text = "No hay residentes registrados en este lugar.",
                                color = Color.LightGray,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(vertical = 16.dp)
                            )
                        }
                        else -> items(residents, key = { it.id }) { character ->
                            CastMember(character = character, onClick = { onCharacterClick(character.id) })
                        }
                    }
                }
                isLoading -> fullWidth {
                    Box(Modifier.fillMaxWidth().padding(top = 80.dp), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LocGreen)
                    }
                }
                else -> fullWidth { RetryBlock(errorMessage ?: "No se pudo cargar el lugar", onRetry) }
            }
        }
    }
}

@Composable
private fun LocationHeaderCard(location: Location) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LocCard),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, LocGreen, RoundedCornerShape(16.dp))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Public, contentDescription = null, tint = LocBlue, modifier = Modifier.size(28.dp))
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                    text = location.name,
                    color = Color.White,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = LocGreen.copy(alpha = 0.3f))
            InfoLine(label = "Tipo", value = translateLocationType(location.type))
            InfoLine(label = "Dimensión", value = translateDimension(location.dimension))
            InfoLine(
                label = "Residentes",
                value = location.residentIds.size.let { if (it == 1) "1 personaje" else "$it personajes" }
            )
        }
    }
}

@Composable
private fun InfoLine(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(text = label, color = Color.Gray, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
        Text(
            text = value,
            color = Color.White,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            textAlign = androidx.compose.ui.text.style.TextAlign.End,
            modifier = Modifier.weight(1f).padding(start = 16.dp)
        )
    }
}
