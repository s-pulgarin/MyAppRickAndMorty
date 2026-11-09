package com.example.myapprickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapprickandmorty.ui.screens.CharacterDetailScreen
import com.example.myapprickandmorty.ui.screens.HomeScreen
import com.example.myapprickandmorty.ui.screens.SplashScreen
import com.example.myapprickandmorty.ui.auth.LoginScreen
import com.example.myapprickandmorty.ui.auth.RegisterScreen
import com.example.myapprickandmorty.ui.profile.ProfileScreen
import com.example.myapprickandmorty.ui.home.HomeViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.firebase.auth.FirebaseAuth
import com.example.myapprickandmorty.ui.theme.MyAppRickAndMortyTheme
import com.example.myapprickandmorty.ui.detail.CharacterDetailViewModel
import com.example.myapprickandmorty.ui.favorites.FavoritesViewModel
import com.example.myapprickandmorty.ui.screens.FavoritesScreen
import androidx.navigation.NavController

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyAppRickAndMortyTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()


    // Un solo ViewModel compartido: mantiene la lista y la búsqueda al navegar
    val homeViewModel: HomeViewModel = viewModel()
    val favoritesViewModel: FavoritesViewModel = viewModel()

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    // Si ya hay sesión activa va directo al home; si no, al login
                    val destination = if (FirebaseAuth.getInstance().currentUser != null) "home" else "login"
                    navController.navigate(destination) {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("login") {
            LoginScreen(
                onLoggedIn = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onGoToRegister = { navController.navigate("register") }
            )
        }

        composable("register") {
            RegisterScreen(
                onRegistered = {
                    navController.navigate("home") { popUpTo("login") { inclusive = true } }
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable("profile") {
            ProfileScreen(
                onBack = { navController.popBackStack() },
                onLoggedOut = {
                    navController.navigate("login") { popUpTo(0) { inclusive = true } }
                }
            )
        }

        composable("home") {
            HomeScreen(
                characterList = homeViewModel.characters,
                query = homeViewModel.query,
                filters = homeViewModel.filters,
                totalCount = homeViewModel.totalCount,
                isLoading = homeViewModel.isLoading,
                isLoadingMore = homeViewModel.isLoadingMore,
                errorMessage = homeViewModel.error,
                onQueryChange = homeViewModel::onQueryChange,
                onFiltersChange = homeViewModel::onFiltersChange,
                onLoadMore = homeViewModel::loadNextPage,
                onCharacterClick = { id -> navController.navigate("detail/$id") },
                onFavoritesClick = { navController.goToTab("favorites") },
                onProfileClick = { navController.goToTab("profile") }
            )
        }

        composable("favorites") {
            FavoritesScreen(
                favorites = favoritesViewModel.favorites,
                isLoading = favoritesViewModel.isLoading,
                errorMessage = favoritesViewModel.error,
                onCharacterClick = { id -> navController.navigate("detail/$id") },
                onRemoveFavorite = favoritesViewModel::remove,
                onCharactersClick = { navController.goToTab("home") },
                onProfileClick = { navController.goToTab("profile") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("id") ?: 1
            // Este ViewModel vive solo mientras esta pantalla esté en la pila
            val detailViewModel: CharacterDetailViewModel = viewModel()

            LaunchedEffect(characterId) {
                // Si ya está en la lista de Home se muestra al instante; si no (p. ej. desde Favoritos), se pide a la API
                detailViewModel.load(characterId, homeViewModel.characters.find { it.id == characterId })
            }

            val character = detailViewModel.character
            CharacterDetailScreen(
                character = character,
                firstEpisode = detailViewModel.firstEpisode,
                lastEpisode = detailViewModel.lastEpisode,
                isLoading = detailViewModel.isLoading,
                errorMessage = detailViewModel.error,
                isFavorite = favoritesViewModel.isFavorite(characterId),
                onToggleFavorite = { character?.let(favoritesViewModel::toggle) },
                onRetry = { detailViewModel.retry(characterId) },
                onBackClick = { navController.popBackStack() },
                onCharactersClick = { navController.goToTab("home") },
                onFavoritesClick = { navController.goToTab("favorites") },
                onProfileClick = { navController.goToTab("profile") }
            )
        }
    }
}
/**
 * Navega entre pestañas de la barra inferior sin apilar pantallas repetidas:
 * "home" siempre queda como base de la pila, así el botón Atrás vuelve a Personajes.
 */
private fun NavController.goToTab(route: String) {
    if (route == "home") {
        popBackStack("home", inclusive = false)
        return
    }
    navigate(route) {
        popUpTo("home")
        launchSingleTop = true
    }
}
