package com.example.youtubedownloader

import android.app.Application
import android.util.Log
import com.yausername.youtubedl_android.YoutubeDL
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        // Initialize YoutubeDL without hiding errors
        YoutubeDL.getInstance().init(this)

        // It's recommended to update youtube-dl at startup
        CoroutineScope(Dispatchers.IO).launch {
            try {
                YoutubeDL.getInstance().updateYoutubeDL(this@MyApplication)
            } catch (e: Exception) {
                // Log the error for debugging, but don't crash the app if the update fails
                Log.e("MyApplication", "Failed to update youtube-dl", e)
            }
        }
    }
}
