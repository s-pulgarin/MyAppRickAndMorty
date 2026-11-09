package com.example.myapprickandmorty.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.model.Episode
import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.data.remote.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Carga un personaje por ID y su primer y último episodio. Vive mientras la pantalla de detalle esté abierta. */
class CharacterDetailViewModel : ViewModel() {

    var character by mutableStateOf<RickCharacter?>(null); private set
    var firstEpisode by mutableStateOf<Episode?>(null); private set
    var lastEpisode by mutableStateOf<Episode?>(null); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    private var loadedId: Int? = null

    /** [cached] es el personaje si ya estaba en la lista de Home: se muestra de inmediato mientras llegan los episodios. */
    fun load(id: Int, cached: RickCharacter? = null) {
        if (loadedId == id) return
        loadedId = id
        viewModelScope.launch {
            error = null
            isLoading = true
            try {
                val c = cached ?: RetrofitClient.api.getCharacterById(id)
                character = c
                loadEpisodes(c.episodeIds)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (character == null) {
                    error = "No se pudo cargar el personaje"
                    loadedId = null // permite reintentar
                }
            }
            isLoading = false
        }
    }

    fun retry(id: Int) = load(id)

    private suspend fun loadEpisodes(ids: List<Int>) {
        val first = ids.firstOrNull() ?: return
        val last = ids.last()
        if (first == last) {
            firstEpisode = RetrofitClient.api.getEpisodeById(first)
        } else {
            val episodes = RetrofitClient.api.getEpisodesByIds("$first,$last")
            firstEpisode = episodes.find { it.id == first }
            lastEpisode = episodes.find { it.id == last }
        }
    }
}
