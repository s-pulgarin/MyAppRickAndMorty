package com.example.myapprickandmorty

import android.app.Application
import com.cloudinary.android.MediaManager

class MyApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.CLOUDINARY_CLOUD_NAME.isNotBlank()) {
            // Solo el cloud_name: NUNCA pongas api_secret en la app.
            MediaManager.init(this, mapOf("cloud_name" to BuildConfig.CLOUDINARY_CLOUD_NAME))
        }
    }
}
