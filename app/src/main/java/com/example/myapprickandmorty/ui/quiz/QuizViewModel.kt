package com.example.myapprickandmorty.ui.quiz

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.model.RickCharacter
import com.example.myapprickandmorty.data.profile.ProfileRepository
import com.example.myapprickandmorty.data.remote.RetrofitClient
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

/**
 * Quiz "¿Vivo o muerto?": se muestra un personaje al azar y el jugador adivina su estado.
 * Modo racha: la partida sigue mientras aciertes; al fallar termina. El récord se guarda en Firestore.
 */
class QuizViewModel(
    private val profiles: ProfileRepository = ProfileRepository(),
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()
) : ViewModel() {

    var current by mutableStateOf<RickCharacter?>(null); private set
    /** null = sin responder; true/false = si la última respuesta fue correcta. */
    var lastAnswerCorrect by mutableStateOf<Boolean?>(null); private set
    var score by mutableStateOf(0); private set
    var best by mutableStateOf(0); private set
    var isNewRecord by mutableStateOf(false); private set
    var isGameOver by mutableStateOf(false); private set
    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    private val queue = ArrayDeque<RickCharacter>()
    private val seen = mutableSetOf<Int>()
    private var refillJob: Job? = null
    private var bestAtStart = 0
    /** No se guarda nada hasta leer el récord actual, para no sobrescribir uno mayor. */
    private var bestLoaded = false

    init {
        loadBest()
        nextCharacter()
    }

    private fun loadBest() {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            try {
                val saved = profiles.getProfile(uid)?.quizBest ?: 0
                bestAtStart = saved
                bestLoaded = true
                if (saved >= best) {
                    best = saved
                    isNewRecord = false
                } else {
                    // Ya se superó el récord antes de que terminara de cargar
                    isNewRecord = true
                    saveBest()
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Sin récord guardado no se rompe el juego
            }
        }
    }

    /** El jugador responde: true = "Vivo", false = "Muerto". */
    fun answer(guessAlive: Boolean) {
        val character = current ?: return
        if (lastAnswerCorrect != null) return
        val isAlive = character.status.equals("alive", ignoreCase = true)
        val correct = guessAlive == isAlive
        lastAnswerCorrect = correct
        if (correct) {
            score++
            if (score > best) {
                best = score
                isNewRecord = bestLoaded && score > bestAtStart
                saveBest()
            }
        } else {
            isGameOver = true
        }
    }

    /** Pasa al siguiente personaje después de un acierto. */
    fun next() {
        if (isGameOver) return
        nextCharacter()
    }

    fun restart() {
        score = 0
        isGameOver = false
        isNewRecord = false
        bestAtStart = best
        nextCharacter()
    }

    private fun nextCharacter() {
        lastAnswerCorrect = null
        error = null
        val fromQueue = queue.removeFirstOrNull()
        if (fromQueue != null) {
            current = fromQueue
            if (queue.size < 3) refill()
            return
        }
        current = null
        isLoading = true
        refill(thenShow = true)
    }

    /**
     * Pide 20 personajes al azar en una sola llamada y se queda con los vivos o muertos
     * ("unknown" no sirve para este juego).
     */
    private fun refill(thenShow: Boolean = false) {
        if (refillJob?.isActive == true && !thenShow) return
        refillJob = viewModelScope.launch {
            try {
                // Si ya casi se vieron todos, se empieza de nuevo
                if (826 - seen.size < 20) seen.clear()
                val ids = (1..826).filter { it !in seen }.shuffled().take(20)
                val batch = RetrofitClient.api.getCharactersByIds(ids.joinToString(","))
                    .filter { it.status.equals("alive", true) || it.status.equals("dead", true) }
                seen += batch.map { it.id }
                queue.addAll(batch.shuffled())
                if (thenShow) {
                    current = queue.removeFirstOrNull()
                    isLoading = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                if (thenShow) {
                    isLoading = false
                    error = "No se pudo cargar el personaje"
                }
            }
        }
    }

    fun retry() = nextCharacter()

    private fun saveBest() {
        val uid = auth.currentUser?.uid ?: return
        if (!bestLoaded) return
        val value = best
        viewModelScope.launch {
            try {
                profiles.updateQuizBest(uid, value)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                // Si falla, se intenta de nuevo con el próximo récord
            }
        }
    }
}
