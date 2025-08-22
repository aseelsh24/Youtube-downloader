package com.example.youtubedownloader.ui.settings

import android.app.Application
import android.content.Context
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPreferences = application.getSharedPreferences("settings", Context.MODE_PRIVATE)

    private val _isDarkTheme = MutableStateFlow(sharedPreferences.getBoolean("is_dark_theme", false))
    val isDarkTheme: StateFlow<Boolean> = _isDarkTheme.asStateFlow()

    private val _downloadPath = MutableStateFlow(sharedPreferences.getString("download_path", null))
    val downloadPath: StateFlow<String?> = _downloadPath.asStateFlow()

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        sharedPreferences.edit().putBoolean("is_dark_theme", isDark).apply()
    }

    fun setDownloadPath(uri: Uri) {
        _downloadPath.value = uri.toString()
        sharedPreferences.edit().putString("download_path", uri.toString()).apply()
    }

    fun setLanguage(language: String) {
        sharedPreferences.edit().putString("language", language).apply()
    }

    fun getLanguage(): String {
        return sharedPreferences.getString("language", "en") ?: "en"
    }
}
