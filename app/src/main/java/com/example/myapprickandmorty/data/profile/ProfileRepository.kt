package com.example.myapprickandmorty.data.profile

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.tasks.await

class ProfileRepository(private val db: FirebaseFirestore = FirebaseFirestore.getInstance()) {

    private fun doc(uid: String) = db.collection("users").document(uid)

    suspend fun createProfile(profile: UserProfile) {
        doc(profile.uid).set(profile).await()
    }

    suspend fun getProfile(uid: String): UserProfile? =
        doc(uid).get().await().toObject(UserProfile::class.java)

    suspend fun updateName(uid: String, name: String) {
        doc(uid).update("name", name).await()
    }

    /** merge: funciona aunque el documento del perfil aún no exista. */
    suspend fun updateQuizBest(uid: String, best: Int) {
        doc(uid).set(mapOf("quizBest" to best), SetOptions.merge()).await()
    }

    suspend fun updatePhotoUrl(uid: String, url: String) {
        doc(uid).update("photoUrl", url).await()
    }
}
