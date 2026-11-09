package com.example.myapprickandmorty.data.favorites

import com.example.myapprickandmorty.data.model.RickCharacter
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

/**
 * Copia mínima de un personaje guardada en Firestore. Se guarda nombre, imagen, etc.
 * para que la pantalla de Favoritos se pinte sin tener que llamar a la API.
 */
data class FavoriteCharacter(
    val id: Int = 0,
    val name: String = "",
    val image: String = "",
    val status: String = "",
    val species: String = "",
    val addedAt: Long = 0L
)

/** Favoritos de cada usuario en users/{uid}/favorites/{characterId}. */
class FavoritesRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private fun collection(uid: String) =
        db.collection("users").document(uid).collection("favorites")

    /** Emite la lista cada vez que cambia (también al agregar/quitar desde este mismo celular). */
    fun observe(uid: String): Flow<List<FavoriteCharacter>> = callbackFlow {
        val registration = collection(uid)
            .orderBy("addedAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                trySend(snapshot?.toObjects(FavoriteCharacter::class.java).orEmpty())
            }
        awaitClose { registration.remove() }
    }

    suspend fun add(uid: String, character: RickCharacter) {
        val favorite = FavoriteCharacter(
            id = character.id,
            name = character.name,
            image = character.image,
            status = character.status,
            species = character.species,
            addedAt = System.currentTimeMillis()
        )
        collection(uid).document(character.id.toString()).set(favorite).await()
    }

    suspend fun remove(uid: String, characterId: Int) {
        collection(uid).document(characterId.toString()).delete().await()
    }
}
