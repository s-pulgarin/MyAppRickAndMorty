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
                isLoading = homeViewModel.isLoading,
                isLoadingMore = homeViewModel.isLoadingMore,
                errorMessage = homeViewModel.error,
                onQueryChange = homeViewModel::onQueryChange,
                onLoadMore = homeViewModel::loadNextPage,
                onCharacterClick = { id -> navController.navigate("detail/$id") },
                onProfileClick = { navController.navigate("profile") }
            )
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("id") ?: 1

            val selectedCharacter = homeViewModel.characters.find { it.id == characterId }

            if (selectedCharacter != null) {
                CharacterDetailScreen(
                    character = selectedCharacter,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}