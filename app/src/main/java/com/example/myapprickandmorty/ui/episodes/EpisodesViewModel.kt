package com.example.myapprickandmorty.ui.episodes

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.model.Episode
import com.example.myapprickandmorty.data.remote.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

/**
 * Compartido por la app: trae los 51 episodios una sola vez (son 3 páginas) y los agrupa por temporada.
 */
class EpisodesViewModel : ViewModel() {

    var episodesBySeason by mutableStateOf<Map<Int, List<Episode>>>(emptyMap()); private set
    var selectedSeason by mutableStateOf(1); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    val seasons: List<Int> get() = episodesBySeason.keys.sorted()
    val currentEpisodes: List<Episode> get() = episodesBySeason[selectedSeason].orEmpty()

    fun loadIfNeeded() {
        if (episodesBySeason.isNotEmpty() || isLoading) return
        viewModelScope.launch {
            isLoading = true
            error = null
            try {
                val first = RetrofitClient.api.getEpisodes(page = 1)
                // Las páginas restantes se piden en paralelo
                val rest = (2..first.info.pages).map { page ->
                    async { RetrofitClient.api.getEpisodes(page = page).results }
                }.awaitAll().flatten()
                episodesBySeason = (first.results + rest)
                    .sortedBy { it.id }
                    .groupBy { it.season }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = "No se pudieron cargar los episodios"
            }
            isLoading = false
        }
    }

    fun selectSeason(season: Int) {
        selectedSeason = season
    }
}

private val apiDateFormat = SimpleDateFormat("MMMM d, yyyy", Locale.US)
private val spanishDateFormat = SimpleDateFormat("d 'de' MMMM 'de' yyyy", Locale.forLanguageTag("es"))

/** "December 2, 2013" -> "2 de diciembre de 2013". Si no se puede leer, devuelve el texto original. */
fun formatAirDate(airDate: String): String =
    try {
        apiDateFormat.parse(airDate)?.let { spanishDateFormat.format(it) } ?: airDate
    } catch (e: Exception) {
        airDate
    }
