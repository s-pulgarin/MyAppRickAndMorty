package com.example.myapprickandmorty.ui.episodes

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

/** Carga un episodio y su reparto. Vive mientras la pantalla del episodio esté abierta. */
class EpisodeDetailViewModel : ViewModel() {

    var episode by mutableStateOf<Episode?>(null); private set
    var cast by mutableStateOf<List<RickCharacter>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var isLoadingCast by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    private var loadedId: Int? = null

    /** [cached] es el episodio si ya estaba en la lista: se muestra al instante mientras llega el reparto. */
    fun load(id: Int, cached: Episode? = null) {
        if (loadedId == id) return
        loadedId = id
        viewModelScope.launch {
            error = null
            isLoading = cached == null
            try {
                val ep = cached ?: RetrofitClient.api.getEpisodeById(id)
                episode = ep
                isLoading = false
                isLoadingCast = true
                cast = loadCast(ep.characterIds)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = if (episode == null) "No se pudo cargar el episodio" else "No se pudo cargar el reparto"
                loadedId = null // permite reintentar
            }
            isLoading = false
            isLoadingCast = false
        }
    }

    fun retry(id: Int) = load(id, episode?.takeIf { it.id == id })

    private suspend fun loadCast(ids: List<Int>): List<RickCharacter> = when (ids.size) {
        0 -> emptyList()
        1 -> listOf(RetrofitClient.api.getCharacterById(ids.first()))
        else -> RetrofitClient.api.getCharactersByIds(ids.joinToString(","))
    }
}
