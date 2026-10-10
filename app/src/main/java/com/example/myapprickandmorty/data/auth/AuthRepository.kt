package com.example.myapprickandmorty.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class AuthRepository(private val auth: FirebaseAuth = FirebaseAuth.getInstance()) {

    val currentUser: FirebaseUser? get() = auth.currentUser

    suspend fun login(email: String, password: String): FirebaseUser =
        auth.signInWithEmailAndPassword(email.trim(), password).await().user
            ?: error("No se pudo iniciar sesión")

    suspend fun register(email: String, password: String): FirebaseUser =
        auth.createUserWithEmailAndPassword(email.trim(), password).await().user
            ?: error("No se pudo crear la cuenta")

    fun logout() = auth.signOut()
}
