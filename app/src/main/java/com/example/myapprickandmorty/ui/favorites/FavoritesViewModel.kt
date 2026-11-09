package com.example.myapprickandmorty.ui.favorites

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.favorites.FavoriteCharacter
import com.example.myapprickandmorty.data.favorites.FavoritesRepository
import com.example.myapprickandmorty.data.model.RickCharacter
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch

/**
 * Compartido por toda la app (igual que HomeViewModel). Escucha los favoritos del usuario
 * con sesión y se reinicia solo si cambia de cuenta (logout / login con otro usuario).
 */
class FavoritesViewModel(
    private val repository: FavoritesRepository = FavoritesRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var favorites by mutableStateOf<List<FavoriteCharacter>>(emptyList()); private set
    var favoriteIds by mutableStateOf<Set<Int>>(emptySet()); private set
    var isLoading by mutableStateOf(true); private set
    var error by mutableStateOf<String?>(null); private set

    private var currentUid: String? = null
    private var listenJob: Job? = null

    private val authListener = FirebaseAuth.AuthStateListener { startListening(it.currentUser?.uid) }

    init {
        auth.addAuthStateListener(authListener)
    }

    private fun startListening(uid: String?) {
        if (uid == currentUid && listenJob != null) return
        currentUid = uid
        listenJob?.cancel()
        applyFavorites(emptyList())
        error = null
        if (uid == null) {
            listenJob = null
            isLoading = false
            return
        }
        isLoading = true
        listenJob = viewModelScope.launch {
            repository.observe(uid)
                .catch {
                    error = "No se pudieron cargar tus favoritos"
                    isLoading = false
                }
                .collect {
                    applyFavorites(it)
                    isLoading = false
                }
        }
    }

    private fun applyFavorites(list: List<FavoriteCharacter>) {
        favorites = list
        favoriteIds = list.map { it.id }.toSet()
    }

    fun isFavorite(id: Int) = id in favoriteIds

    fun toggle(character: RickCharacter) {
        val uid = currentUid ?: return
        val wasFavorite = isFavorite(character.id)
        viewModelScope.launch {
            try {
                if (wasFavorite) repository.remove(uid, character.id)
                else repository.add(uid, character)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = "No se pudo actualizar el favorito"
            }
        }
    }

    fun remove(id: Int) {
        val uid = currentUid ?: return
        viewModelScope.launch {
            try {
                repository.remove(uid, id)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = "No se pudo quitar el favorito"
            }
        }
    }

    override fun onCleared() {
        auth.removeAuthStateListener(authListener)
    }
}
