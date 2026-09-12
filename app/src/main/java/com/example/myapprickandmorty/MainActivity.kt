package com.example.myapprickandmorty

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.data.remote.RetrofitClient
import com.example.myapprickandmorty.ui.screens.CharacterDetailScreen
import com.example.myapprickandmorty.ui.screens.HomeScreen
import com.example.myapprickandmorty.ui.screens.SplashScreen
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


    var characterList by remember { mutableStateOf<List<RickCharacter>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }


    LaunchedEffect(Unit) {
        try {
            val response = RetrofitClient.api.getCharacters()
            characterList = response.results
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            isLoading = false
        }
    }

    NavHost(navController = navController, startDestination = "splash") {
        composable("splash") {
            SplashScreen(
                onNavigateToHome = {
                    navController.navigate("home") {
                        popUpTo("splash") { inclusive = true }
                    }
                }
            )
        }

        composable("home") {

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Color(0xFF97CE4C))
                }
            } else {
                HomeScreen(
                    characterList = characterList,
                    onCharacterClick = { id ->
                        navController.navigate("detail/$id")
                    }
                )
            }
        }

        composable(
            route = "detail/{id}",
            arguments = listOf(navArgument("id") { type = NavType.IntType })
        ) { backStackEntry ->
            val characterId = backStackEntry.arguments?.getInt("id") ?: 1

            val selectedCharacter = characterList.find { it.id == characterId }
                ?: characterList.firstOrNull()

            if (selectedCharacter != null) {
                CharacterDetailScreen(
                    character = selectedCharacter,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}