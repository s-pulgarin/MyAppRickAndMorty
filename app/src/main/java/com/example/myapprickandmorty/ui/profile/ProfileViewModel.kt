package com.example.myapprickandmorty.ui.profile

import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myapprickandmorty.data.auth.AuthRepository
import com.example.myapprickandmorty.data.profile.CloudinaryUploader
import com.example.myapprickandmorty.data.profile.ProfileRepository
import com.example.myapprickandmorty.data.profile.UserProfile
import kotlinx.coroutines.launch

class ProfileViewModel : ViewModel() {
    private val auth = AuthRepository()
    private val profiles = ProfileRepository()

    var profile by mutableStateOf<UserProfile?>(null); private set
    var isLoading by mutableStateOf(true); private set
    var isUploading by mutableStateOf(false); private set
    var error by mutableStateOf<String?>(null); private set

    init { load() }

    fun load() {
        val uid = auth.currentUser?.uid ?: run { isLoading = false; return }
        viewModelScope.launch {
            isLoading = true
            try {
                profile = profiles.getProfile(uid)
                    ?: UserProfile(uid = uid, email = auth.currentUser?.email.orEmpty())
            } catch (e: Exception) { error = "No se pudo cargar el perfil" }
            isLoading = false
        }
    }

    fun onPhotoPicked(uri: Uri) {
        val uid = auth.currentUser?.uid ?: return
        viewModelScope.launch {
            isUploading = true; error = null
            try {
                val url = CloudinaryUploader.uploadProfilePhoto(uri)
                profiles.updatePhotoUrl(uid, url)
                profile = profile?.copy(photoUrl = url)
            } catch (e: Exception) { error = "No se pudo subir la foto: ${e.message}" }
            isUploading = false
        }
    }

    fun logout() = auth.logout()
}
