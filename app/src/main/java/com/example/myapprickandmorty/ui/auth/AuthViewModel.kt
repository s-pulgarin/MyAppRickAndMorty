package com.example.myapprickandmorty.ui.auth

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.auth.AuthRepository
import com.example.myapprickandmorty.data.profile.ProfileRepository
import com.example.myapprickandmorty.data.profile.UserProfile
import com.google.firebase.auth.FirebaseAuthException
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthInvalidUserException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import kotlinx.coroutines.launch

class AuthViewModel : ViewModel() {
    private val auth = AuthRepository()
    private val profiles = ProfileRepository()

    var isLoading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    fun clearError() { error = null }

    fun login(email: String, password: String, onSuccess: () -> Unit) {
        if (email.isBlank() || password.isBlank()) { error = "Completa todos los campos"; return }
        viewModelScope.launch {
            isLoading = true; error = null
            try {
                auth.login(email, password)
                onSuccess()
            } catch (e: Exception) { error = e.toMessage() }
            isLoading = false
        }
    }

    fun register(name: String, email: String, password: String, onSuccess: () -> Unit) {
        if (name.isBlank() || email.isBlank() || password.isBlank()) { error = "Completa todos los campos"; return }
        if (password.length < 6) { error = "La contraseña debe tener al menos 6 caracteres"; return }
        viewModelScope.launch {
            isLoading = true; error = null
            try {
                val user = auth.register(email, password)
                profiles.createProfile(UserProfile(uid = user.uid, name = name.trim(), email = email.trim()))
                onSuccess()
            } catch (e: Exception) { error = e.toMessage() }
            isLoading = false
        }
    }

    private fun Exception.toMessage() = when (this) {
        is FirebaseAuthWeakPasswordException -> "La contraseña es muy débil"
        is FirebaseAuthUserCollisionException -> "Ya existe una cuenta con ese correo"
        is FirebaseAuthInvalidUserException,
        is FirebaseAuthInvalidCredentialsException -> "Correo o contraseña incorrectos"
        is FirebaseAuthException -> "Error de autenticación (${errorCode})"
        else -> message ?: "Ocurrió un error inesperado"
    }
}
