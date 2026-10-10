package com.example.myapprickandmorty.ui.home

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.model.CharacterResponse
import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.data.remote.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import retrofit2.HttpException

class HomeViewModel : ViewModel() {

    var characters by mutableStateOf<List<RickCharacter>>(emptyList()); private set
    var query by mutableStateOf(""); private set
    var isLoading by mutableStateOf(true); private set
    var isLoadingMore by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    private var page = 1
    private var canLoadMore = true
    private var searchJob: Job? = null
    private var loadJob: Job? = null

    init { loadFirstPage() }

    /** Se llama en cada letra; espera 400 ms sin escribir antes de buscar. */
    fun onQueryChange(newQuery: String) {
        query = newQuery
        searchJob?.cancel()
        searchJob = viewModelScope.launch {
            delay(400)
            loadFirstPage()
        }
    }

    fun loadFirstPage() {
        loadJob?.cancel()
        page = 1
        canLoadMore = true
        val q = query.trim()
        loadJob = viewModelScope.launch {
            isLoading = true
            isLoadingMore = false
            error = null
            try {
                val response = fetch(1, q)
                characters = response.results
                canLoadMore = response.info.next != null
            } catch (e: CancellationException) {
                throw e
            } catch (e: HttpException) {
                characters = emptyList()
                canLoadMore = false
                // La API responde 404 cuando la búsqueda no tiene resultados
                if (e.code() != 404) error = "No se pudo cargar los personajes"
            } catch (e: Exception) {
                characters = emptyList()
                canLoadMore = false
                error = "Sin conexión. Revisa tu internet e intenta de nuevo"
            }
            isLoading = false
        }
    }

    fun loadNextPage() {
        if (isLoading || isLoadingMore || !canLoadMore) return
        val q = query.trim()
        loadJob = viewModelScope.launch {
            isLoadingMore = true
            try {
                val response = fetch(page + 1, q)
                page += 1
                characters = (characters + response.results).distinctBy { it.id }
                canLoadMore = response.info.next != null
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Falló al traer más: dejamos lo que ya hay y se reintenta al volver a hacer scroll
            }
            isLoadingMore = false
        }
    }

    private suspend fun fetch(page: Int, name: String): CharacterResponse =
        RetrofitClient.api.getCharacters(page = page, name = name.ifBlank { null })
}
