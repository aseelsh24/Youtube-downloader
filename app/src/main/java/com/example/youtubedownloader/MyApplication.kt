package com.example.youtubedownloader

import android.app.Application
import com.yausername.youtubedl_android.YoutubeDL

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        try {
            YoutubeDL.getInstance().init(this)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
