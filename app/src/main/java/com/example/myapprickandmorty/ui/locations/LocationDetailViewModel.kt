package com.example.myapprickandmorty.ui.locations

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.model.Location
import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.data.remote.RetrofitClient
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.launch

/** Carga un lugar y sus residentes. Vive mientras la pantalla del lugar esté abierta. */
class LocationDetailViewModel : ViewModel() {

    var location by mutableStateOf<Location?>(null); private set
    var residents by mutableStateOf<List<RickCharacter>>(emptyList()); private set
    var isLoading by mutableStateOf(false); private set
    var isLoadingResidents by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    private var loadedId: Int? = null

    fun load(id: Int) {
        if (loadedId == id) return
        loadedId = id
        viewModelScope.launch {
            error = null
            isLoading = location?.id != id
            try {
                val loc = location?.takeIf { it.id == id } ?: RetrofitClient.api.getLocationById(id)
                location = loc
                isLoading = false
                isLoadingResidents = true
                residents = loadResidents(loc.residentIds)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                error = if (location == null) "No se pudo cargar el lugar" else "No se pudieron cargar los residentes"
                loadedId = null // permite reintentar
            }
            isLoading = false
            isLoadingResidents = false
        }
    }

    private suspend fun loadResidents(ids: List<Int>): List<RickCharacter> = when (ids.size) {
        0 -> emptyList()
        1 -> listOf(RetrofitClient.api.getCharacterById(ids.first()))
        else -> RetrofitClient.api.getCharactersByIds(ids.joinToString(","))
    }
}

/** Traduce los tipos de lugar más comunes; el resto se muestra tal cual. */
fun translateLocationType(type: String): String = when (type.lowercase()) {
    "planet" -> "Planeta"
    "space station" -> "Estación espacial"
    "dimension" -> "Dimensión"
    "microverse" -> "Microverso"
    "miniverse" -> "Miniverso"
    "teenyverse" -> "Teenyverso"
    "spacecraft" -> "Nave espacial"
    "dream" -> "Sueño"
    "nightmare" -> "Pesadilla"
    "memory" -> "Recuerdo"
    "consciousness" -> "Consciencia"
    "reality" -> "Realidad"
    "liquid" -> "Líquido"
    "mount" -> "Montaña"
    "woods" -> "Bosque"
    "space" -> "Espacio"
    "cluster" -> "Cúmulo"
    "country" -> "País"
    "game" -> "Juego"
    "hell" -> "Infierno"
    "machine" -> "Máquina"
    "box" -> "Caja"
    "convention" -> "Convención"
    "customs" -> "Aduana"
    "daycare" -> "Guardería"
    "police department" -> "Departamento de policía"
    "fantasy town" -> "Pueblo de fantasía"
    "", "unknown" -> "Desconocido"
    else -> type
}

fun translateDimension(dimension: String): String =
    if (dimension.isBlank() || dimension.equals("unknown", ignoreCase = true)) "Desconocida" else dimension
