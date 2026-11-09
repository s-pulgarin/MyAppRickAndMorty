package com.example.myapprickandmorty.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
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
import com.example.myapprickandmorty.data.favorites.FavoriteCharacter

@Composable
fun FavoritesScreen(
    favorites: List<FavoriteCharacter>,
    isLoading: Boolean,
    errorMessage: String?,
    onCharacterClick: (Int) -> Unit,
    onRemoveFavorite: (Int) -> Unit,
    onCharactersClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val backgroundColor = Color(0xFF0F141E)
    val rickGreen = Color(0xFF97CE4C)
    val textBlue = Color(0xFF00B5CC)

    Scaffold(
        bottomBar = {
            CustomBottomNavigation(
                selected = BottomTab.Favorites,
                onCharactersClick = onCharactersClick,
                onProfileClick = onProfileClick
            )
        },
        containerColor = backgroundColor
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = "Favoritos", fontSize = 32.sp, fontWeight = FontWeight.Bold, color = rickGreen)
            Text(
                text = if (favorites.isEmpty()) "Tu colección del multiverso"
                else "${favorites.size} sujeto${if (favorites.size == 1) "" else "s"} en tu colección",
                fontSize = 14.sp,
                color = textBlue
            )
            Spacer(modifier = Modifier.height(16.dp))

            when {
                isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = rickGreen)
                }
                favorites.isEmpty() -> Column(
                    modifier = Modifier.fillMaxSize().padding(horizontal = 24.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.FavoriteBorder,
                        contentDescription = null,
                        tint = textBlue,
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = errorMessage ?: "Aún no tienes favoritos",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                    if (errorMessage == null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Toca el corazón en la ficha de un personaje para guardarlo aquí.",
                            color = Color.LightGray,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center
                        )
                    }
                    Spacer(modifier = Modifier.height(20.dp))
                    OutlinedButton(onClick = onCharactersClick) {
                        Text("Explorar personajes", color = rickGreen)
                    }
                }
                else -> LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(favorites, key = { it.id }) { favorite ->
                        FavoriteCard(
                            favorite = favorite,
                            onClick = { onCharacterClick(favorite.id) },
                            onRemove = { onRemoveFavorite(favorite.id) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun FavoriteCard(favorite: FavoriteCharacter, onClick: () -> Unit, onRemove: () -> Unit) {
    val rickGreen = Color(0xFF97CE4C)
    val textBlue = Color(0xFF00B5CC)

    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1E2738)),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, rickGreen, RoundedCornerShape(16.dp))
            .clickable { onClick() }
    ) {
        Row(
            modifier = Modifier.padding(12.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = favorite.image,
                contentDescription = favorite.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(2.dp, textBlue, CircleShape)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = favorite.name, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val statusColor = when (favorite.status.lowercase()) {
                        "alive" -> Color.Green
                        "dead" -> Color.Red
                        else -> Color.Gray
                    }
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(statusColor)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "${favorite.status} • ${favorite.species}",
                        color = Color.LightGray,
                        fontSize = 14.sp
                    )
                }
            }
            IconButton(onClick = onRemove) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "Quitar de favoritos",
                    tint = Color(0xFFE5484D)
                )
            }
        }
    }
}
