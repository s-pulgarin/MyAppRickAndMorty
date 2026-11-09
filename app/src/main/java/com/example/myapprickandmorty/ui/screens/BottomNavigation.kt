package com.example.myapprickandmorty.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class BottomTab { Characters, Favorites, Profile }

/** Barra inferior compartida. [selected] = null cuando la pantalla no es una pestaña (p. ej. el detalle). */
@Composable
fun CustomBottomNavigation(
    selected: BottomTab? = null,
    onCharactersClick: () -> Unit = {},
    onFavoritesClick: () -> Unit = {},
    onProfileClick: () -> Unit = {}
) {
    NavigationBar(
        containerColor = Color(0xFF0F141E),
        contentColor = Color.White
    ) {
        TabItem(selected == BottomTab.Characters, Icons.Default.Group, "Personajes", onCharactersClick)
        TabItem(selected == BottomTab.Favorites, Icons.Default.Favorite, "Favoritos", onFavoritesClick)
        TabItem(selected == BottomTab.Profile, Icons.Default.Person, "Perfil", onProfileClick)
    }
}

@Composable
private fun androidx.compose.foundation.layout.RowScope.TabItem(
    isSelected: Boolean,
    icon: ImageVector,
    label: String,
    onClick: () -> Unit
) {
    NavigationBarItem(
        selected = isSelected,
        onClick = onClick,
        icon = { Icon(icon, contentDescription = label) },
        label = { Text(label) },
        colors = NavigationBarItemDefaults.colors(
            selectedIconColor = Color(0xFF97CE4C),
            selectedTextColor = Color(0xFF97CE4C),
            unselectedIconColor = Color.Gray,
            unselectedTextColor = Color.Gray,
            indicatorColor = Color.Transparent
        )
    )
}
