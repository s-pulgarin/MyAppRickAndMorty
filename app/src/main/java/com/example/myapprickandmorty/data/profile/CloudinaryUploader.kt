package com.example.myapprickandmorty.data.profile

import android.net.Uri
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import com.example.myapprickandmorty.BuildConfig
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlinx.coroutines.suspendCancellableCoroutine

object CloudinaryUploader {

    /** Sube la imagen con un unsigned preset y devuelve la URL https segura. */
    suspend fun uploadProfilePhoto(uri: Uri): String = suspendCancellableCoroutine { cont ->
        val requestId = MediaManager.get()
            .upload(uri)
            .unsigned(BuildConfig.CLOUDINARY_UPLOAD_PRESET)
            .option("folder", "rickandmorty/profiles")
            .callback(object : UploadCallback {
                override fun onStart(requestId: String) {}
                override fun onProgress(requestId: String, bytes: Long, totalBytes: Long) {}
                override fun onReschedule(requestId: String, error: ErrorInfo) {}
                override fun onSuccess(requestId: String, resultData: MutableMap<Any?, Any?>) {
                    val url = resultData["secure_url"] as? String
                    if (url != null) cont.resume(url)
                    else cont.resumeWithException(Exception("Cloudinary no devolvió URL"))
                }
                override fun onError(requestId: String, error: ErrorInfo) {
                    cont.resumeWithException(Exception(error.description))
                }
            })
            .dispatch()
        cont.invokeOnCancellation { MediaManager.get().cancelRequest(requestId) }
    }
}
